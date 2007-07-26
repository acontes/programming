/*
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2006 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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

/**
 * this is how we would run the scheduler
 *
 * @author walzouab
 *
 */
package org.objectweb.proactive.examples.scheduler;

import java.io.File;
import java.net.URI;
import org.apache.log4j.Logger;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.infrastructuremanager.IMFactory;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMAdmin;
import org.objectweb.proactive.extra.scheduler.core.AdminScheduler;
import org.objectweb.proactive.extra.scheduler.resourcemanager.InfrastructureManagerProxy;
import org.objectweb.proactive.extra.scheduler.resourcemanager.SimpleResourceManager;


public class LocalSchedulerExample {
    //shows how to run the scheduler
    private static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);

    public static void main(String[] args) {
        //get the path of the file
        InfrastructureManagerProxy imp = null;

        try {
        	

            if (args.length > 0) {
                try {
                	
                	imp = InfrastructureManagerProxy.getProxy(new URI(args[0]));
           
                    logger.info("Connect to ResourceManager on " + args[0]);
                } catch (Exception e) {
                    throw new Exception("ResourceManager doesn't exist on " + args[0]);
                }
            } else {
            	IMFactory.startLocal();
            	IMAdmin admin = IMFactory.getAdmin();
            	
            	String xmlURL = SimpleResourceManager.class.getResource("/org/objectweb/proactive/examples/scheduler/test.xml").getPath();
            	admin.deployAllVirtualNodes(new File(xmlURL), null);
            	
            	imp = InfrastructureManagerProxy.getProxy(new URI("rmi://localhost:1099/"));
            	
            	
                logger.info("ResourceManager created on " + ProActive.getActiveObjectNodeUrl(imp));
            }

            AdminScheduler adminAPI = AdminScheduler.createScheduler(
            		LocalSchedulerExample.class.getResource("login.cfg").getFile(),
            		LocalSchedulerExample.class.getResource("groups.cfg").getFile(),
            		"admin",
            		"admin",
            		imp,
            		"org.objectweb.proactive.extra.scheduler.policy.PriorityPolicy");
            
            adminAPI.start();
            
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("Error creating Scheduler " + e.toString());
            System.exit(1);
        }
    }
}
