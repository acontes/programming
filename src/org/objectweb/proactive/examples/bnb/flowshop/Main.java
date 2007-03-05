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
package org.objectweb.proactive.examples.bnb.flowshop;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.branchnbound.BranchNBoundFactory;
import org.objectweb.proactive.branchnbound.user.BnBManager;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public final class Main {
    public static final Logger logger = ProActiveLogger.getLogger(
    "proactive.examples.flowshop");
    private static final String USAGE = "java " + Main.class.getName() +
        " taillard_file_path descriptor_file";
    private BnBManager manager;
    private String taillarsFilePath;

    private Main(String taillardFilePath) {
        this.taillarsFilePath = taillardFilePath;
        try {
            this.manager = BranchNBoundFactory.getBnBManager();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void terminate() {
        this.manager.terminate();
    }

    private void deploy(String xmlDescriptorFilePath) throws ProActiveException {
        assert xmlDescriptorFilePath != null : xmlDescriptorFilePath;
        ProActiveDescriptor pad = ProActive.getProactiveDescriptor(xmlDescriptorFilePath);
        this.manager.deployAndAddResources(pad.getVirtualNodes());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Wrong number of arguments");
            System.err.println("Usage :" + USAGE);
            System.exit(1);
        }
        System.out.println("Hello");

        Arguments arguments = Main.parseArgs(args);
        System.out.println(arguments);

        Main main = new Main(arguments.getTaillardFilePath());

        try {
            main.deploy(arguments.getXmlDescriptorFilePath());
        } catch (ProActiveException e) {
            System.err.println("Problem with deployment of file " +
                arguments.getXmlDescriptorFilePath());
            e.printStackTrace();
            System.exit(1);
        }

        main.terminate();
        System.out.println("Good bye");
        System.exit(0);
    }

    private static Arguments parseArgs(String[] args) {
        return new Arguments(args);
    }

    private final static class Arguments {
        private String taillardFile;
        private String descriptorFile;

        public Arguments(String[] args) {
            assert (args != null) && (args.length == 2) : args;
            this.taillardFile = args[0];
            this.descriptorFile = args[1];
        }

        public String getTaillardFilePath() {
            return this.taillardFile;
        }

        public String getXmlDescriptorFilePath() {
            return this.descriptorFile;
        }

        @Override
        public String toString() {
            return "Taillard file path: " + this.taillardFile + "\n" +
            "XML Descriptor path: " + this.descriptorFile;
        }
    }
}
