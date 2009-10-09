package org.objectweb.proactive.core.ssh;

import static org.objectweb.proactive.core.ssh.SSH.logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.Sleeper;


public class SshTunnelPool {
    /** The SSH configuration to use in this pool */
    private SshConfig config;
    /** A cache to remember if plain socket connection works for a given destination */
    final private TryCache tryCache;
    /** SSH connection & tunnels cache */
    final private Map<String, Pair> cache;
    /** SSH proxy connection & sessions cache */
    final private Map<String, List<ProxyPair>> proxyCommandCache;
    /** The thread in charge of tunnel & connection garbage collection */
    private Thread gcThread = null;

    public SshTunnelPool() {
        logger.debug("Created a new SSH tunnel pool");

        this.tryCache = new TryCache();
        this.cache = new HashMap<String, Pair>();
        this.proxyCommandCache = new HashMap<String, List<ProxyPair>>();
    }

    public void createAndStartGCThread() {
        if (config == null && gcThread == null) {
            // Throw an exception ?
            return;
        }
        this.gcThread = new Thread(new GCThread());
        this.gcThread.setDaemon(true);
        this.gcThread.setName("SSH Tunnel pool GC");
        if (this.config.getGcInterval() > 0) {
            logger.debug("Starting SSH GC thread");
            this.gcThread.start();
        }
    }

    public void setSshConfig(SshConfig config) {
        this.config = config;
    }

    public SshTunnelPool(SshConfig config) {
        this();
        this.config = config;
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
                    socket.connect(address, 1000); // this.config.getConnectTimeout());
                    this.tryCache.recordTrySuccess(host, port);
                } catch (IOException ioe) {
                    this.tryCache.recordTryFailure(host, port);
                    socket = null;
                }
            }
        }

        // Try proxy command 
        if (socket == null && config.tryProxyCommand()) {
            String localhost = ProActiveInet.getInstance().getHostname();
            // localhost can't open outgoing connection
            String localhostGW = config.getGateway(localhost);
            String hostGW = config.getGateway(host);
            // if proxyCommand command mechanism is needed
            if ((localhostGW != null) || (hostGW != null)) {
                String gw = localhostGW != null ? localhostGW : hostGW;
                synchronized (this.proxyCommandCache) {
                    SshProxyConnection cnx = null;
                    List<ProxyPair> pairs = this.proxyCommandCache.get(gw);
                    if (pairs == null) {
                        cnx = SshProxyConnection.getInstance(hostGW, localhostGW, (SshConfig) config);
                        pairs = new ArrayList<ProxyPair>();
                        ProxyPair p = new ProxyPair(cnx);
                        pairs.add(p);
                        this.proxyCommandCache.put(gw, pairs);
                    }

                    SshProxySession session = null;
                    // For each connection, try to open a session
                    for (int i = 0; i < pairs.size(); i++) {
                        try {
                            cnx = (SshProxyConnection) pairs.get(i).cnx;
                            // Always create a new session because there are not Thread-Safe                                                                                                 
                            session = cnx.getSession(host, port);
                            pairs.get(i).registerSession(session);
                            break;
                        } catch (IOException channelException) {
                            continue;
                        }
                    }

                    if (session == null) {
                        // No Connections permit to open a new session
                        // Create a new connection
                        cnx = SshProxyConnection.getInstance(hostGW, localhostGW, (SshConfig) config);
                        session = cnx.getSession(host, port);
                        ProxyPair pair = new ProxyPair(cnx);
                        pair.registerSession(session);
                        pairs.add(pair);
                    }

                    // Grab a socket 
                    socket = session.getSocket();
                }
            }
        }

        if (socket == null) {
            // SSH tunnel must be used
            synchronized (this.cache) {
                String username = this.config.getUsername(host);

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
            // port changes a lot, if normal socket works 
            // on one port shouldn't it works on all ?            
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
    private class SshTunnelStatefull extends SshTunnel {
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
                    synchronized (SshTunnelPool.this.cache) {
                        unusedSince.set(System.currentTimeMillis());
                        users.decrementAndGet();
                    }
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

    private static class ProxyPair {
        final private SshProxyConnection cnx;
        final private List<SshProxySession> sessions;

        private ProxyPair(SshProxyConnection cnx) {
            this.cnx = cnx;
            this.sessions = new ArrayList<SshProxySession>();
        }

        public void registerSession(SshProxySession sess) {
            this.sessions.add(sess);
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

                synchronized (SshTunnelPool.this.cache) {
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

                    // Purge unused proxyCommand session 
                    for (List<ProxyPair> lp : proxyCommandCache.values()) {
                        for (ProxyPair p : lp) {
                            for (int i = 0; i < p.sessions.size(); i++) {
                                SshProxySession s = p.sessions.get(i);
                                if (s.isUnused()) {
                                    p.sessions.remove(i);
                                }
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

                    // Purge unused proxyCommand connections (no opened session)                       
                    for (Iterator<List<ProxyPair>> iLP = proxyCommandCache.values().iterator(); iLP.hasNext();) {
                        List<ProxyPair> lP = iLP.next();
                        for (int i = 0; i < lP.size(); i++) {
                            ProxyPair p = lP.get(i);
                            if (p.sessions.isEmpty()) {
                                p.cnx.close();
                                lP.remove(i);
                            }
                        }
                        if (lP.size() == 0) {
                            iLP.remove();
                        }
                    }

                }
            }
        }
    }
}
