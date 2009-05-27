package org.objectweb.proactive.examples.structuredp2p.can;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.Tracker;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.util.Deployment;


public class PeerLauncher {

    public static ArrayList<Peer> remotePeers = new ArrayList<Peer>();
    private static boolean running = true;

    /**
     * @param args
     */
    public static void main(String[] args) {
        int nbPeers = 1;
        String uri = "localhost";

        if (args.length == 0) {
            System.err.println("Usage : java " + PeerLauncher.class.getCanonicalName() + " " +
                "descriptor [nbPeers] [trackerURI] ");
            System.exit(1);
        }
        if (args.length > 1) {
            nbPeers = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            uri = args[2];
        }

        try {
            Deployment.deploy(args[0]);
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        try {
            Tracker tracker = (Tracker) PAActiveObject.lookupActive(Tracker.class.getName(), "//" + uri +
                "/CANTracker");

            List<Node> avaibleNodes = Deployment.getVirtualNode("CANOverlay").getNewNodes();

            int i;
            for (i = 0; i < nbPeers; i++) {
                Peer peer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                        new Object[] { OverlayType.CAN }, avaibleNodes.get(i % avaibleNodes.size()));

                remotePeers.add(peer);
                tracker.addOnNetwork(peer);
            }
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Thread inputThread = new Thread(new Runnable() {
            public void run() {
                Random rand = new Random();
                Scanner scanner = new Scanner(System.in);
                String inputLine;

                PeerLauncher.printOptions();
                while (PeerLauncher.running) {
                    inputLine = scanner.nextLine();

                    if (inputLine.equalsIgnoreCase("quit")) {
                        try {
                            for (Peer p : PeerLauncher.remotePeers)
                                p.leave();

                            Deployment.kill();
                            PeerLauncher.running = false;
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (inputLine.equalsIgnoreCase("leave")) {
                        remotePeers.remove(rand.nextInt(remotePeers.size())).leave();
                    } else if (inputLine.equalsIgnoreCase("lookup")) {
                        int pos = rand.nextInt(remotePeers.size());
                        Peer toFind = remotePeers.get(pos);
                        Coordinate[] location = ((CANOverlay) toFind.getStructuredOverlay()).getZone()
                                .getCoordinatesMin();

                        String loc = "(";
                        for (int i = 0; i < location.length; i++) {
                            if (i != 0)
                                loc += ",";
                            loc += location[i];
                        }
                        loc += ")";

                        System.out.println("Random coordinates to lookup : " + loc);

                        Peer sender = remotePeers.get(rand.nextInt(remotePeers.size()));
                        CANLookupResponseMessage response = (CANLookupResponseMessage) sender
                                .sendMessage(new CANLookupMessage(location));

                        System.out.println("Lookup from peer with area (" +
                            ((CANOverlay) sender.getStructuredOverlay()).getZone() +
                            ") had find the peer with area (" +
                            ((CANOverlay) response.getPeer().getStructuredOverlay()).getZone() + ")");
                    }
                    PeerLauncher.printOptions();
                }
            }
        });
        inputThread.start();
    }

    /**
     * Print app menu option on the standard output.
     */
    private static void printOptions() {
        System.out.println("* What you can do :");
        System.out.println("  > Type in 'lookup' to send a lookup message");
        System.out.println("  > Type in 'leave' to a random peer to quit the network");
        System.out.println("  > Type in 'quit' keyword in order to quit the application");
    }
}
