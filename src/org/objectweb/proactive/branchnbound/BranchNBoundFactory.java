/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.branchnbound;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.branchnbound.core.BnBManagerImpl;
import org.objectweb.proactive.branchnbound.exception.BnBManagerException;
import org.objectweb.proactive.branchnbound.user.BnBManager;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 *
 * The Factory for getting instance of BnBManager.
 *
 * @see or.objectweb.proactive.branchandbound.user.BnBManager
 *
 * @author Alexandre di Costanzo
 *
 * Created on Feb 27, 2007
 */
public final class BranchNBoundFactory {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.BNB_FACTORY);

    static {
        // Loading the ProActive's configuration
        ProActiveConfiguration.load();
    }

    /**
     * Instatiate a new <code>BnBManager</code>.
     *
     * @param enableCommunication set communication between tasks, i.e. sharing
     * current best results.
     * @param node the <code>Node</code> where to create the
     * <code>BnBManager</code>.
     * @return a remote reference on the <code>BnBManager</code> created.
     * @throws NodeException problem with the <code>node</code>.
     * @throws BnBManagerException problem with the <code>BnBManager</code>
     * creation.
     */
    public static BnBManager getBnBManager(boolean enableCommunication,
        Node node) throws NodeException, BnBManagerException {
        assert node != null : node;
        if (logger.isInfoEnabled()) {
            logger.info("Starting a new BnB Manager on " +
                node.getNodeInformation().getURL() + " with communications " +
                ((enableCommunication) ? "enable" : "disable"));
        }
        BnBManager manager = null;
        try {
            manager = (BnBManager) ProActive.newActive(BnBManagerImpl.class.getName(),
                    new Object[] { enableCommunication }, node);
        } catch (ActiveObjectCreationException e) {
            logger.fatal("Cannot activating BnBManager", e);
            throw new BnBManagerException("Cannot activating BnBManager", e);
        }
        assert manager != null : manager;
        return manager;
    }

    /**
     * Instatiate a new <code>BnBManager</code> in the current JVM with
     * communications between tasks enable.
     * @return a remote reference on the <code>BnBManager</code> created.
     * @throws NodeException problem with the <code>node</code>.
     * @throws BnBManagerException problem with the <code>BnBManager</code>
     * creation.
     */
    public static BnBManager getBnBManager()
        throws NodeException, BnBManagerException {
        Node localNode = NodeFactory.getDefaultNode();
        return BranchNBoundFactory.getBnBManager(true, localNode);
    }

    /**
     * Instatiate a new <code>BnBManager</code> in the <code>nodeManager</code>
     * given with commuincations between tasks enable.
     * @param nodeManager the <code>Node</code> where to create the
     * <code>BnBManager</code>.
     * @return a remote reference on the <code>BnBManager</code> created.
     * @throws NodeException problem with the <code>node</code>.
     * @throws BnBManagerException problem with the <code>BnBManager</code>
     * creation.
     */
    public static BnBManager getBnBManager(Node nodeManager)
        throws NodeException, BnBManagerException {
        return BranchNBoundFactory.getBnBManager(true, nodeManager);
    }
}
