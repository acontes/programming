package org.objectweb.proactive.examples.structuredp2p.can;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public enum LauncherType {
        INTERACTIVE, STRESS_TEST
    };

    private LauncherType launcherType;

    public PeerLauncher(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage : java " + PeerLauncher.class.getCanonicalName() + " " +
                "descriptor nbPeers [trackerURI] ");
            System.exit(1);
        }

        if (args.length > 3) {
            this.uri = args[3];
        }

        if (args[1].equals("ST")) {
            this.launcherType = LauncherType.STRESS_TEST;
        } else {
            this.launcherType = LauncherType.INTERACTIVE;
        }

        try {
            Deployment.deploy(args[0]);
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        this.avaibleNodes = Deployment.getVirtualNode("Peer").getCurrentNodes();

        for (int i = 0; i < Integer.parseInt(args[2]); i++) {
            this.addPeer();
        }

        Runnable threadToExecute;

        if (this.getLauncherType() == LauncherType.INTERACTIVE) {
            threadToExecute = new InteractiveThread(this);
        } else {
            threadToExecute = new StressTestThread(this);
        }

        new Thread(threadToExecute).start();
    }

    public void addPeer() {
        Random rand = new Random();

        try {
            Peer peer = (Peer) PAActiveObject.newActive(Peer.class.getCanonicalName(),
                    new Object[] { OverlayType.CAN }, this.avaibleNodes.get(rand.nextInt(this.avaibleNodes
                            .size())));
            this.remotePeers.add(peer);

            TrackerLauncher.trackers.get(this.trackersIndex % TrackerLauncher.trackers.size()).addOnNetwork(
                    peer);
            this
                    .printInformation("Add peer managing " +
                        ((CANOverlay) peer.getStructuredOverlay()).getZone());
            this.trackersIndex++;

        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }

    public void removePeer() {
        Random rand = new Random();
        Peer peer = this.remotePeers.remove(rand.nextInt(this.remotePeers.size()));
        this.printInformation("Remove peer managing " + ((CANOverlay) peer.getStructuredOverlay()).getZone());
        this.remotePeers.remove(peer);
        peer.leave();
    }

    public void lookupMessage() {
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

    public void printInformation(String mess) {
        System.out.println("*** " + mess);
        System.out.println();
    }

    public List<Node> getAvaibleNodes() {
        return this.avaibleNodes;
    }

    public List<Peer> getRemotePeers() {
        return this.remotePeers;
    }

    public int getTrackersIndex() {
        return this.trackersIndex;
    }

    public String getUri() {
        return this.uri;
    }

    public LauncherType getLauncherType() {
        return this.launcherType;
    }
}
