/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.body.ft.servers;

import java.net.URI;

import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.body.ft.protocols.FTManagerFactory;
import org.objectweb.proactive.core.body.ft.protocols.cic.servers.CheckpointServerCIC;
import org.objectweb.proactive.core.body.ft.protocols.cic.servers.RecoveryProcessCIC;
import org.objectweb.proactive.core.body.ft.protocols.pmlrb.servers.CheckpointServerPMLRB;
import org.objectweb.proactive.core.body.ft.protocols.pmlrb.servers.RecoveryProcessPMLRB;
import org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector;
import org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetectorImpl;
import org.objectweb.proactive.core.body.ft.servers.location.LocationServer;
import org.objectweb.proactive.core.body.ft.servers.location.LocationServerImpl;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.ft.servers.resource.ResourceServer;
import org.objectweb.proactive.core.body.ft.servers.resource.ResourceServerImpl;
import org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.URIBuilder;


/**
 * This class is a main that creates and starts a ft.util.FTServer.
 * Usage : ~>startGlobalFTServer [-proto {cic|pml}] [-name name] [-fdperiod faultDetectionPeriod (sec)]
 * @author The ProActive Team
 * @since ProActive 2.2
 */
public class StartFTServer {
    public static void main(String[] args) {
        URI uri = null;
        try {
            int fdPeriod = 0;
            String name = "";
            String proto = FTManagerFactory.PROTO_CIC;
            String host = ProActiveInet.getInstance().getInetAddress().getHostName();

            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-fdperiod")) {
                    fdPeriod = Integer.parseInt(args[i + 1]);
                } else if (args[i].equals("-name")) {
                    name = args[i + 1];
                } else if (args[i].equals("-proto")) {
                    proto = args[i + 1];
                }
            }
            //            }

            if ("".equals(name)) {
                name = FTServer.DEFAULT_SERVER_NAME;
            }
            uri = URIBuilder.buildURI(host, name);
            if (fdPeriod == 0) {
                fdPeriod = FTServer.DEFAULT_FDETECT_SCAN_PERIOD;
            }

            // server init

            FTServer server = new FTServer();
            LocationServer ls = new LocationServerImpl(server);
            FaultDetector fd = new FaultDetectorImpl(server, fdPeriod);
            ResourceServer rs;

            // protocol specific
            CheckpointServer cs = null;
            RecoveryProcess rp = null;
            if (proto.equals(FTManagerFactory.PROTO_CIC)) {
                cs = new CheckpointServerCIC(server);
                rp = new RecoveryProcessCIC(server);
            } else if (proto.equals(FTManagerFactory.PROTO_PML)) {
                cs = new CheckpointServerPMLRB(server);
                rp = new RecoveryProcessPMLRB(server);
            } else {
                System.err.println("ERROR: " + proto + " is not a valid protocol. Aborting.");
                System.exit(1);
            }
            rs = new ResourceServerImpl(server);

            // init
            server.init(fd, ls, rp, rs, cs);
            server.startFailureDetector();

            RemoteObjectExposer<FTServer> remoteServerExposer;
            remoteServerExposer = new RemoteObjectExposer<FTServer>(FTServer.class.getName(), server);
            FTServer remoteServer = PARemoteObject.bind(remoteServerExposer, uri);

            System.out.println("FT: Server is bound on " + uri);
            System.out.println("FT: Server started");

        } catch (Exception e) {
            System.err.println("FT: ** ERROR ** Unable to launch server on " + uri);
            e.printStackTrace();
        }
    }
}
