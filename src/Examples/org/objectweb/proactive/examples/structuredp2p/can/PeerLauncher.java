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

    private static final int MIN_OP = 5000;
    private static final int MAX_OP = 10000;
    private static Random rand = new Random();
    private static ArrayList<Peer> remotePeers = new ArrayList<Peer>();
    private static Tracker tracker;
    private static boolean running = true;
    private static List<Node> avaibleNodes;

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
            PeerLauncher.tracker = (Tracker) PAActiveObject.lookupActive(Tracker.class.getName(), "//" + uri +
                "/CANTracker");

            PeerLauncher.avaibleNodes = Deployment.getVirtualNode("CANOverlay").getCurrentNodes();

            int i;
            for (i = 0; i < nbPeers; i++) {
                PeerLauncher.addPeer(avaibleNodes.get(i % avaibleNodes.size()));
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
                    } else if (inputLine.equalsIgnoreCase("add")) {
                        PeerLauncher.addPeer();
                    } else if (inputLine.equalsIgnoreCase("leave")) {
                        PeerLauncher.removePeer();
                    } else if (inputLine.equalsIgnoreCase("lookup")) {
                        PeerLauncher.lookupMessage();
                    } else if (inputLine.equalsIgnoreCase("random")) {
                        int nbOp = Math.max(PeerLauncher.MIN_OP, rand.nextInt(PeerLauncher.MAX_OP));
                        for (int i = 0; i < nbOp; i++) {
                            int r = rand.nextInt(3);

                            switch (r) {
                                case 0:
                                    PeerLauncher.addPeer();
                                    break;
                                case 1:
                                    PeerLauncher.lookupMessage();
                                    break;
                                case 2:
                                    // TODO remove comments when leave request queue problem is
                                    // fixed.
                                    // PeerLauncher.removePeer();
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
        System.out.println("* What you can do :");
        System.out.println("  > Type in 'add' to add a new peer");
        System.out.println("  > Type in 'lookup' to send a lookup message");
        System.out.println("  > Type in 'leave' to a random peer to quit the network");
        System.out.println("  > Type in 'random' to make maximum " + PeerLauncher.MAX_OP +
            " random actions on the network");
        System.out.println("  > Type in 'quit' keyword in order to quit the application");
    }

    private static void addPeer() {
        PeerLauncher.addPeer(avaibleNodes.get(rand.nextInt(avaibleNodes.size())));
    }

    private static void addPeer(Node n) {
        try {
            Peer peer = (Peer) PAActiveObject.newActive(Peer.class.getCanonicalName(),
                    new Object[] { OverlayType.CAN }, n);
            remotePeers.add(peer);
            PeerLauncher.tracker.addOnNetwork(peer);

            System.out.println("Add peer at zone " + ((CANOverlay) peer.getStructuredOverlay()).getZone());
        } catch (ActiveObjectCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void removePeer() {
        Peer p = remotePeers.remove(rand.nextInt(remotePeers.size()));
        System.out.println("Remove peer at zone " + ((CANOverlay) p.getStructuredOverlay()).getZone());
        p.leave();
    }

    private static void lookupMessage() {
        int pos = rand.nextInt(remotePeers.size());
        Peer toFind = remotePeers.get(pos);
        Coordinate[] location = ((CANOverlay) toFind.getStructuredOverlay()).getZone().getCoordinatesMin();

        String loc = "(";
        for (int i = 0; i < location.length; i++) {
            if (i != 0)
                loc += ",";
            loc += location[i];
        }
        loc += ")";

        System.out.println("Random coordinates to lookup : " + loc);

        Peer sender = remotePeers.get(rand.nextInt(remotePeers.size()));

        System.out.println("Lookup from peer with zone " +
            ((CANOverlay) sender.getStructuredOverlay()).getZone());

        CANLookupResponseMessage response = (CANLookupResponseMessage) sender
                .sendMessage(new CANLookupMessage(location));
        System.out.println(" had find the peer with zone " +
            ((CANOverlay) response.getPeer().getStructuredOverlay()).getZone());
    }
}
