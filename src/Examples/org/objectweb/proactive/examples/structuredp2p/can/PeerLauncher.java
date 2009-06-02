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

    private static List<Node> avaibleNodes;
    private static ArrayList<Peer> remotePeers = new ArrayList<Peer>();
    private static Tracker tracker;

    private static boolean running = true;

    /**
     * @param args
     */
    public static void main(String[] args) {
        int nbPeers = Integer.parseInt(args[1]);
        ;
        String uri = "localhost";

        if (args.length < 1) {
            System.err.println("Usage : java " + PeerLauncher.class.getCanonicalName() + " " +
                "descriptor nbPeers [trackerURI] ");
            System.exit(1);
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
            PeerLauncher.tracker = (Tracker) PAActiveObject.lookupActive(Tracker.class.getName(), "//" + uri +
                "/CANTracker");
            PeerLauncher.avaibleNodes = Deployment.getVirtualNode("CANOverlay").getCurrentNodes();

            for (int i = 0; i < nbPeers; i++) {
                PeerLauncher.addPeer(PeerLauncher.avaibleNodes.get(i % PeerLauncher.avaibleNodes.size()));
            }
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread inputThread = new Thread(new Runnable() {
            public void run() {

                Scanner scanner = new Scanner(System.in);
                String inputLine;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                PeerLauncher.printOptions();
                while (PeerLauncher.running) {
                    inputLine = scanner.nextLine();

                    if (inputLine.equalsIgnoreCase("quit")) {
                        try {
                            for (Peer p : PeerLauncher.remotePeers) {
                                p.leave();
                            }

                            Deployment.kill();
                            PeerLauncher.running = false;
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (inputLine.equalsIgnoreCase("add")) {
                        PeerLauncher.addPeer();
                    } else if (inputLine.equalsIgnoreCase("leave")) {
                        PeerLauncher.removePeer();
                    } else if (inputLine.equalsIgnoreCase("lookup")) {
                        PeerLauncher.lookupMessage();
                    } else if (inputLine.startsWith("random")) {
                        int nbOperations = Integer.parseInt(inputLine.split(" ")[1]);

                        for (int i = 0; i < nbOperations; i++) {
                            Random rand = new Random();
                            int res = rand.nextInt(3);
                            switch (res) {
                                case 0:
                                    PeerLauncher.addPeer();
                                    break;
                                case 1:
                                    PeerLauncher.lookupMessage();
                                    break;
                                case 2:
                                    PeerLauncher.removePeer();
                                    break;
                                default:
                                    break;
                            }
                        }
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
        System.out.println("[ Select an action to perform ]");
        System.out.println("  > Type in 'add' : add a new peer at a random position");
        System.out.println("  > Type in 'lookup' : send a lookup message to a random peer");
        System.out.println("  > Type in 'leave' : force a random peer to quit the network");
        System.out.println("  > Type in 'random x' : perform x random operations (lookup, join, leave)");
        System.out.println("  > Type in 'quit' : quit the application");
    }

    private static void addPeer() {
        Random rand = new Random();
        PeerLauncher.addPeer(PeerLauncher.avaibleNodes.get(rand.nextInt(PeerLauncher.avaibleNodes.size())));
    }

    private static void addPeer(Node n) {
        try {
            Peer peer = (Peer) PAActiveObject.newActive(Peer.class.getCanonicalName(),
                    new Object[] { OverlayType.CAN }, n);
            PeerLauncher.remotePeers.add(peer);
            PeerLauncher.tracker.addOnNetwork(peer);

            PeerLauncher.printInformation("Add peer managing " +
                ((CANOverlay) peer.getStructuredOverlay()).getZone());
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }

    private static void removePeer() {
        /*
         * Random rand = new Random(); Peer p =
         * PeerLauncher.remotePeers.remove(rand.nextInt(PeerLauncher.remotePeers.size()));
         * p.leave();
         * 
         * PeerLauncher.printInformation("Remove peer managing " + ((CANOverlay)
         * p.getStructuredOverlay()).getZone());
         */

        PeerLauncher.printInformation("Leave is not yet fully implemented !");
    }

    private static void lookupMessage() {
        Random rand = new Random();

        Coordinate[] searchedPosition = new Coordinate[CANOverlay.NB_DIMENSIONS];

        String buf = "(";
        for (int i = 0; i < searchedPosition.length; i++) {
            searchedPosition[i] = new Coordinate("" + rand.nextDouble());
            if (i != 0) {
                buf += ",";
            }
            buf += searchedPosition[i];
        }
        buf += ")";

        Peer sender = PeerLauncher.remotePeers.get(rand.nextInt(PeerLauncher.remotePeers.size()));

        CANLookupResponseMessage response = (CANLookupResponseMessage) sender
                .sendMessage(new CANLookupMessage(searchedPosition));

        PeerLauncher.printInformation("Lookup for peer managing " + buf +
            ".\n    Lookup start from peer managing " +
            ((CANOverlay) sender.getStructuredOverlay()).getZone() + ".\n    Peer found in " +
            response.getLatency() + "ms with " + response.getNbSteps() + " steps.");
    }

    private static void printInformation(String mess) {
        System.out.println("*** " + mess);
        System.out.println();
    }
}
