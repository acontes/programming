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
package org.objectweb.proactive.core.runtime;

import org.apache.log4j.Logger;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.util.UrlBuilder;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * <p>
 * This class is a utility class allowing to start a ProActiveRuntime with a JVM.
 * </p><p>
 * This class is mainly used with ProActiveDescriptor to start a ProActiveRuntime
 * on a local or remote JVM.
 * </p>
 *
 * @author  ProActive Team
 * @version 1.0,  2002/08/29
 * @since   ProActive 0.9
 *
 */
public class StartRuntime {
    //Name of the runtime that launched this class reading the ProActiveDescriptor
    //private static final String DefaultRuntimeName = "PART_DEFAULT";
    //Name of the runtime's host that launched this class reading the ProActiveDescriptor
    static Logger logger = Logger.getLogger(StartRuntime.class.getName());
    protected String DefaultRuntimeURL;
    protected String nodeURL;
    protected String creatorID;
    protected ProActiveRuntime proActiveRuntime;

    //protected String acquisitionMethod;
    protected String nodeNumber;
    protected String vmName;
    protected int nodenumber; //it is only the int value of nodeNumber
    protected String protocolId;

    protected StartRuntime() {
    }

    private StartRuntime(String[] args) {
        if (args.length != 0) {
            this.nodeURL = args[0];
            this.creatorID = args[0].trim();
            //System.out.println(creatorID);
            this.DefaultRuntimeURL = args[1];
            //this.acquisitionMethod = args[2];
            this.nodeNumber = args[2];
            //   this.portNumber = Integer.parseInt(args[4]);
            this.nodenumber = (new Integer(nodeNumber)).intValue();
            this.protocolId = args[3];
            this.vmName = args[4];
        }
    }

    public static void main(String[] args) {
        //        if (args.length < 3) {
        //           logger.error(
        //             "Usage: java org.objectweb.proactive.core.runtime.StartRuntime <nodeURL> <DefaultRuntimeURL> <acquisitionMethod> <portNumber>");
        //        System.exit(1);
        //  }
        ProActiveConfiguration.load();

        //        System.out.println("StartRunTime.main() " + args[0] + " " + args[1] +
        //            " " + args[2] + " " + args[3]);
        try {
            logger.info("**** Starting jvm on " +
                java.net.InetAddress.getLocalHost().getCanonicalHostName());
            if (logger.isDebugEnabled()) {
                logger.debug("**** Starting jvm with classpath " +
                    System.getProperty("java.class.path"));
                logger.debug("****              with bootclasspath " +
                    System.getProperty("sun.boot.class.path"));
            }
        } catch (java.net.UnknownHostException e) {
            e.printStackTrace();
        }
        new StartRuntime(args).run();
    }

    /**
     * <i><font size="-1" color="#FF0000">**For internal use only** </font></i>
     * Runs the complete creation and registration of a ProActiveRuntime and creates a
     * node once the creation is completed.
     */
    private void run() {
        try {
            //proActiveRuntime = RuntimeFactory.getProtocolSpecificRuntime(acquisitionMethod);
            proActiveRuntime = RuntimeFactory.getProtocolSpecificRuntime(System.getProperty(
                        "proactive.communication.protocol") + ":");
            
            String comProtocol = System.getProperty("proactive.communication.protocol");
            if(comProtocol == "http");
            	comProtocol="";
            
            logger.info("Runtime started at " + comProtocol+ ":" +
                proActiveRuntime.getURL());
            proActiveRuntime.getVMInformation().setCreationProtocolID(protocolId);

            /*
               for (int i = 1; i <= nodenumber; i++) {
                   proActiveRuntime.createLocalNode(nodeURL +
                       Integer.toString(
                           new java.util.Random(System.currentTimeMillis()).nextInt()),
                       false);
                   //Thread.sleep(2000); for windows
               }
               //System.out.println("creation OK");
               //System.out.println(DefaultRuntimeURL);
             */
            if (DefaultRuntimeURL != null) {
                register(DefaultRuntimeURL);
            }
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        //		catch(Exception e){
        //			e.printStackTrace();
        //		}
    }

    /**
     * <i><font size="-1" color="#FF0000">**For internal use only** </font></i>
     * Performs the registration of a ProActiveRuntime on the runtime that initiated the creation
     * of ProActiveDescriptor.
     */
    private void register(String hostName) {
        try {
            ProActiveRuntime PART = RuntimeFactory.getRuntime(DefaultRuntimeURL,
                    UrlBuilder.getProtocol(DefaultRuntimeURL));
            PART.register(proActiveRuntime, proActiveRuntime.getURL(),
                creatorID,
                System.getProperty("proactive.communication.protocol") + ":",
                vmName);
            proActiveRuntime.addParent(DefaultRuntimeURL);
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }
}
