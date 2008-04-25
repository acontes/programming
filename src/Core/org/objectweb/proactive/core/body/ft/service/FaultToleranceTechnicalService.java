/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
 */
package org.objectweb.proactive.core.body.ft.service;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.ft.servers.resource.ResourceServer;
import org.objectweb.proactive.core.descriptor.services.TechnicalService;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class FaultToleranceTechnicalService implements TechnicalService {

    // logger
    final protected static Logger logger = ProActiveLogger.getLogger(Loggers.FAULT_TOLERANCE);

    public static final String FT_ENABLED_PROP = "ftenabled";

    public static final String GLOBAL_SERVER_PROP = "global";
    public static final String CKPT_SERVER_PROP = "checkpoint";
    public static final String LOCATION_SERVER_PROP = "location";
    public static final String RESOURCE_SERVER_PROP = "resource";
    public static final String RECOVERY_SERVER_PROP = "recovery";
    public static final String TTC_PROP = "ttc";
    public static final String PROTOCOL_PROP = "protocol";

    // protocol Type
    private String protocolType;

    // fault tolerance servers
    private String recoveryProcessURL;
    private String checkpointServerURL;
    private String locationServerURL;
    private String globalServerURL; // priority on the 3 upper values

    // fault tolerance values
    private String ttcValue;

    // attached resource server
    private String attachedResourceServer; // null if not registered has a resource node

    public void apply(Node node) {
        try {
            node.setProperty(FT_ENABLED_PROP, "true");
            if (this.globalServerURL != null) {
                node.setProperty(GLOBAL_SERVER_PROP, this.globalServerURL);
            } else {
                node.setProperty(CKPT_SERVER_PROP, this.checkpointServerURL);
                node.setProperty(LOCATION_SERVER_PROP, this.locationServerURL);
                node.setProperty(RECOVERY_SERVER_PROP, this.recoveryProcessURL);
            }

            if (this.ttcValue != null) {
                node.setProperty(TTC_PROP, this.ttcValue);
            }

            if (this.protocolType != null) {
                node.setProperty(PROTOCOL_PROP, this.protocolType);
            }

            if (this.attachedResourceServer != null) {
                this.registerResource(node);
            }
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    public void init(Map<String, String> argValues) {

        this.globalServerURL = argValues.get(GLOBAL_SERVER_PROP);
        this.checkpointServerURL = argValues.get(CKPT_SERVER_PROP);
        this.recoveryProcessURL = argValues.get(RECOVERY_SERVER_PROP);
        this.locationServerURL = argValues.get(LOCATION_SERVER_PROP);

        if (this.globalServerURL != null &&
            (this.checkpointServerURL != null || this.recoveryProcessURL != null || this.locationServerURL != null)) {
            logger.warn("A global server is set : other servers are ignored !!");
            this.checkpointServerURL = null;
            this.recoveryProcessURL = null;
            this.locationServerURL = null;
        }

        // TODO : CHECK VALUES

        this.ttcValue = argValues.get(TTC_PROP);
        this.attachedResourceServer = argValues.get(RESOURCE_SERVER_PROP);
        this.protocolType = argValues.get(PROTOCOL_PROP);
    }

    public boolean registerResource(Node node) {
        try {
            ResourceServer rs = (ResourceServer) (Naming.lookup(this.attachedResourceServer));
            rs.addFreeNode(node);
            return true;
        } catch (MalformedURLException e) {
            logger.error("**ERROR** RessourceServer unreachable : ressource is not registred." + e);
        } catch (RemoteException e) {
            logger.error("**ERROR** RessourceServer unreachable : ressource is not registred" + e);
        } catch (NotBoundException e) {
            logger.error("**ERROR** RessourceServer unreachable : ressource is not registred" + e);
        }
        return false;
    }

}
