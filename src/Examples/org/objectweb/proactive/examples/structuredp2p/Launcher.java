package org.objectweb.proactive.examples.structuredp2p;

import org.objectweb.proactive.examples.structuredp2p.launchers.PeerLauncher;
import org.objectweb.proactive.examples.structuredp2p.launchers.TrackerLauncher;


public class Launcher {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage : java " + PeerLauncher.class.getCanonicalName() + " " +
                "pathToDescriptorFileForPeers pathToDescriptorFileForTrackers nbPeersToCreate mode modeArg");
            System.exit(1);
        }

        TrackerLauncher trackerLauncher = new TrackerLauncher(args[0]);

        int nbPeersToCreate = 0;
        try {
            nbPeersToCreate = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String modeArg = "";
        if (args.length > 3) {
            modeArg = args[3];
        }

        try {

            new PeerLauncher(trackerLauncher, args[0], args[2], modeArg, nbPeersToCreate);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
