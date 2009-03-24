package org.objectweb.proactive.ic2d.replay.data;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import org.objectweb.proactive.core.body.ft.protocols.cic.servers.CheckpointServerCIC;
import org.objectweb.proactive.core.body.ft.protocols.cic.servers.RecoveryProcessCIC;
import org.objectweb.proactive.core.body.ft.servers.FTServer;
import org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector;
import org.objectweb.proactive.core.body.ft.servers.location.LocationServer;
import org.objectweb.proactive.core.body.ft.servers.location.LocationServerImpl;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.ft.servers.resource.ResourceServer;
import org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer;
import org.objectweb.proactive.core.util.ProActiveInet;


public class ReplayServer {

    public static final int DEFAULT_PORT = FTServer.DEFAULT_PORT;
    public static final String DEFAULT_SERVER_NAME = "ReplayServer";

    private FTServer ftServer;

    public ReplayServer() {
        this(DEFAULT_PORT, DEFAULT_SERVER_NAME);
    }

    public ReplayServer(int port, String name) {
        try {
            FTServer server = new FTServer();
            LocationServer ls = new LocationServerImpl(server); // save to file
            FaultDetector fd = null;
            CheckpointServer cs = new CheckpointServerCIC(server);
            RecoveryProcess rp = new RecoveryProcessCIC(server);
            ResourceServer rs = null;

            // init
            server.init(fd, ls, rp, rs, cs);

            String host = ProActiveInet.getInstance().getInetAddress().getHostName();
            Naming.rebind("rmi://" + host + ":" + port + "/" + name, server);
            System.out.println("Replay: Fault-tolerance server is bound on rmi://" + host + ":" + port + "/" +
                name);
            this.ftServer = server;
        } catch (RemoteException e) {
            System.err.println("Replay: ** ERROR ** Unable to launch FT server : ");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.err.println("Replay: ** ERROR ** Unable to launch FT server : ");
            e.printStackTrace();
        }
    }

    public FTServer getFTServer() {
        return ftServer;
    }

}
