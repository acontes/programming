package org.objectweb.proactive.core.ssh;

import static org.objectweb.proactive.core.ssh.SSH.logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.proactive.core.util.Sleeper;


public class SshTunnelPool {
    /** The SSH configuration to use in this pool */
    final private SshConfig config;
    /** A cache to remember if plain socket connection works for a given destination */
    final private TryCache tryCache;
    /** SSH connection & tunnels cache */
    final private Map<String, Pair> cache;
    /** The thread in charge of tunnel & connection garbage collection */
    final private Thread gcThread;

    public SshTunnelPool(final SshConfig config) {
        logger.debug("Created a new SSH tunnel pool");

        this.config = config;
        this.tryCache = new TryCache();
        this.cache = new HashMap<String, Pair>();

        this.gcThread = new Thread(new GCThread());
        this.gcThread.setDaemon(true);
        this.gcThread.setName("SSH Tunnel pool GC");
        if (this.config.getGcInterval() > 0) {
            logger.debug("Starting SSH GC thread");
            this.gcThread.start();
        }
    }

    /**
     * Return a socket connected to the remote host
     * 
     * @param host the remote host to connect to
     * @param port the remote port  to connect to
     * @return A socket connected to the remote endpoint
     * @throws IOException If the connection cannot be opened
     */
    public Socket getSocket(String host, int port) throws IOException {
        Socket socket = null;

        if (config.tryPlainSocket()) {
            // Try plain socket connections
            if (this.tryCache.shouldTryDirect(host, port)) {
                try {
                    // Try direct connection (never tried or was successful)
                    InetSocketAddress address = new InetSocketAddress(host, port);
                    socket = new Socket();
                    socket.connect(address, this.config.getConnectTimeout());
                    this.tryCache.recordTrySuccess(host, port);
                } catch (IOException e) {
                    this.tryCache.recordTryFailure(host, port);
                    socket = null;
                }
            }
        }

        if (socket == null) {
            // SSH tunnel must be used
            synchronized (this.cache) {
                String username = this.config.getUsername(host, 22);

                Pair pair = this.cache.get(host);
                if (pair == null) {
                    // Open a SSH connection
                    String[] keys = new SSHKeys(this.config.getKeyDir()).getKeys();
                    SshConnection cnx = new SshConnection(username, host, 22, keys);
                    pair = new Pair(cnx);
                    this.cache.put(host, pair);
                }

                SshTunnelStatefull tunnel = pair.getTunnel(host, port);
                if (tunnel == null) {
                    // Open a tunnel
                    tunnel = new SshTunnelStatefull(pair.cnx, host, port);
                    pair.registerTunnel(tunnel);
                }

                // Grab a socket
                socket = tunnel.getSocket();
            }
        }

        return socket;
    }

    /**
     * This class maintains state which reflects whether a given host has already
     * been contacted through a tunnel or a direct connection or has
     * never been contacted before.
     */
    private static class TryCache {
        /*
         * - Key does not exist:         never tried
         * - Key exists, value is true:  direct connection ok
         * - Key exists, value is false: direct connection nok
         */
        final private ConcurrentHashMap<String, Boolean> _hash;

        TryCache() {
            _hash = new ConcurrentHashMap<String, Boolean>();
        }

        private String getKey(String host, int port) {
            return host + ":" + port;
        }

        boolean everTried(String host, int port) {
            String key = getKey(host, port);
            return (_hash.get(key) != null);
        }

        boolean shouldTryDirect(String host, int port) {
            String key = getKey(host, port);
            Boolean b = _hash.get(key);

            if (b == null)
                return true;

            if (b.booleanValue())
                return true;

            return false;
        }

        void recordTrySuccess(String host, int port) {
            String key = getKey(host, port);
            _hash.put(key, Boolean.valueOf(true));
        }

        void recordTryFailure(String host, int port) {
            String key = getKey(host, port);
            _hash.put(key, Boolean.valueOf(false));
        }
    }

    /**
     * A SshTunnel which manage statistics about the number of opened sockets
     */
    private static class SshTunnelStatefull extends SshTunnel {
        /** number of currently open sockets */
        final private AtomicInteger users = new AtomicInteger();
        /** If users == 0, the timestamp of the last call to close() */
        final private AtomicLong unusedSince = new AtomicLong();

        SshTunnelStatefull(SshConnection connection, String distantHost, int distantPort) throws IOException {
            super(connection, distantHost, distantPort);
        }

        @Override
        public Socket getSocket() throws IOException {
            this.users.incrementAndGet();

            InetSocketAddress address = new InetSocketAddress(this.getPort());
            Socket socket = new Socket() {
                public synchronized void close() throws IOException {
                    users.decrementAndGet();
                    unusedSince.set(System.currentTimeMillis());
                    super.close();
                }
            };
            socket.connect(address);
            return socket;
        }

        public long unusedSince() {
            if (this.users.get() == 0) {
                return this.unusedSince.get();
            } else {
                return Long.MAX_VALUE;
            }
        }
    }

    private static class Pair {
        final private SshConnection cnx;
        final private Map<String, SshTunnelStatefull> tunnels;

        private Pair(SshConnection cnx) {
            this.cnx = cnx;
            this.tunnels = new HashMap<String, SshTunnelStatefull>();
        }

        public SshTunnelStatefull getTunnel(String host, int port) {
            return this.tunnels.get(buildKey(host, port));
        }

        public void registerTunnel(SshTunnelStatefull tunnel) {
            String host = tunnel.getDistantHost();
            int port = tunnel.getDistantPort();

            this.tunnels.put(buildKey(host, port), tunnel);
        }

        private String buildKey(String host, int port) {
            return host + ":" + port;
        }
    }

    /**
     *  Performs garbage collection of the SSH tunnels and SSH connections
     */
    private final class GCThread implements Runnable {
        private Sleeper sleeper;

        public GCThread() {
            this.sleeper = new Sleeper(config.getGcInterval());
        }

        public void run() {
            while (true) {
                sleeper.sleep();

                synchronized (cache) {
                    logger.trace("Running garbage collection");
                    long ctime = System.currentTimeMillis(); // Avoid too many context switches 

                    // Purge unused tunnels
                    for (Pair p : cache.values()) {
                        for (Iterator<SshTunnelStatefull> iT = p.tunnels.values().iterator(); iT.hasNext();) {
                            SshTunnelStatefull t = iT.next();
                            if (ctime - t.unusedSince() > config.getGcInterval()) {
                                try {
                                    t.close();
                                } catch (Exception e) {
                                    logger.error("", e);
                                }
                                iT.remove();
                            }
                        }
                    }

                    // Purge unused connections (no opened tunnel)
                    for (Iterator<Pair> iP = cache.values().iterator(); iP.hasNext();) {
                        Pair p = iP.next();
                        if (p.tunnels.isEmpty()) {
                            p.cnx.close();
                            iP.remove();
                        }
                    }
                }
            }
        }
    }
}
