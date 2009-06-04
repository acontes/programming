package org.objectweb.proactive.examples.structuredp2p.can;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.examples.structuredp2p.util.Deployment;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANLookupResponseMessage;


public class PeerLauncher {

    private List<Node> avaibleNodes;
    private List<Peer> remotePeers = new ArrayList<Peer>();
    private int trackersIndex = 0;

    private String uri = "localhost";
    private boolean running = true;

    public PeerLauncher(String[] args) {
        int nbPeers = Integer.parseInt(args[1]);

        if (args.length < 1) {
            System.err.println("Usage : java " + PeerLauncher.class.getCanonicalName() + " " +
                "descriptor nbPeers [trackerURI] ");
            System.exit(1);
        }

        if (args.length > 2) {
            this.uri = args[2];
        }

        try {
            Deployment.deploy(args[0]);
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        this.avaibleNodes = Deployment.getVirtualNode("Peer").getCurrentNodes();

        for (int i = 0; i < nbPeers; i++) {
            this.addPeer();
        }

        Thread inputThread = new Thread(new Runnable() {
            public void run() {

                Scanner scanner = new Scanner(System.in);
                String inputLine;

                PeerLauncher.this.printOptions();
                while (PeerLauncher.this.running) {
                    inputLine = scanner.nextLine();

                    if (inputLine.equalsIgnoreCase("quit")) {
                        try {
                            for (Peer p : PeerLauncher.this.remotePeers) {
                                p.leave();
                            }

                            Deployment.kill();
                            PeerLauncher.this.running = false;
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (inputLine.equalsIgnoreCase("add")) {
                        PeerLauncher.this.addPeer();
                    } else if (inputLine.equalsIgnoreCase("leave")) {
                        PeerLauncher.this.removePeer();
                    } else if (inputLine.equalsIgnoreCase("lookup")) {
                        PeerLauncher.this.lookupMessage();
                    } else if (inputLine.startsWith("random")) {
                        int nbOperations = Integer.parseInt(inputLine.split(" ")[1]);

                        for (int i = 0; i < nbOperations; i++) {
                            Random rand = new Random();
                            int res = rand.nextInt(3);
                            switch (res) {
                                case 0:
                                    PeerLauncher.this.addPeer();
                                    break;
                                case 1:
                                    PeerLauncher.this.lookupMessage();
                                    break;
                                case 2:
                                    PeerLauncher.this.removePeer();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    PeerLauncher.this.printOptions();
                }
            }
        });
        inputThread.start();
    }

    /**
     * Print app menu option on the standard output.
     */
    private void printOptions() {
        System.out.println("[ Select an action to perform ]");
        System.out.println("  > Type in 'add' : add a new peer at a random position");
        System.out.println("  > Type in 'lookup' : send a lookup message to a random peer");
        System.out.println("  > Type in 'leave' : force a random peer to quit the network");
        System.out.println("  > Type in 'random x' : perform x random operations (lookup, join, leave)");
        System.out.println("  > Type in 'quit' : quit the application");
    }

    private void addPeer() {
        Random rand = new Random();

        try {
            Peer peer = (Peer) PAActiveObject.newActive(Peer.class.getCanonicalName(),
                    new Object[] { OverlayType.CAN }, this.avaibleNodes.get(rand.nextInt(this.avaibleNodes
                            .size())));
            this.remotePeers.add(peer);

            TrackerLauncher.trackers.get(this.trackersIndex % TrackerLauncher.trackers.size()).addOnNetwork(
                    peer);
            this.trackersIndex++;

            this
                    .printInformation("Add peer managing " +
                        ((CANOverlay) peer.getStructuredOverlay()).getZone());
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }

    private void removePeer() {
        Random rand = new Random();
        Peer p = this.remotePeers.remove(rand.nextInt(this.remotePeers.size()));
        p.leave();

        this.printInformation("Remove peer managing " + ((CANOverlay) p.getStructuredOverlay()).getZone());
        // this.printInformation("Leave is not yet fully implemented !");
    }

    private void lookupMessage() {
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

        Peer sender = this.remotePeers.get(rand.nextInt(this.remotePeers.size()));

        CANLookupResponseMessage response = (CANLookupResponseMessage) sender
                .sendMessage(new CANLookupMessage(searchedPosition));

        this.printInformation("Lookup for peer managing " + buf + ".\n    Lookup start from peer managing " +
            ((CANOverlay) sender.getStructuredOverlay()).getZone() + ".\n    Peer found in " +
            response.getLatency() + "ms with " + response.getNbSteps() + " steps.");
    }

    private void printInformation(String mess) {
        System.out.println("*** " + mess);
        System.out.println();
    }
}
