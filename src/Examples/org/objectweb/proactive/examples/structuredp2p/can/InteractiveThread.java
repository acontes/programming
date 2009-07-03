package org.objectweb.proactive.examples.structuredp2p.can;

import java.util.Random;
import java.util.Scanner;

import org.objectweb.proactive.examples.structuredp2p.util.Deployment;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;


public class InteractiveThread implements Runnable {

    private PeerLauncher peerLauncher;
    private boolean running = true;

    public InteractiveThread(PeerLauncher peerLauncher) {
        this.peerLauncher = peerLauncher;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String inputLine;

        this.printOptions();
        while (this.running) {
            inputLine = scanner.nextLine();

            if (inputLine.equalsIgnoreCase("quit")) {
                try {
                    for (Peer p : this.peerLauncher.getRemotePeers()) {
                        p.leave();
                    }

                    Deployment.kill();
                    this.running = false;
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (inputLine.equalsIgnoreCase("join") || inputLine.equals("j")) {
                this.peerLauncher.performJoin();
            } else if (inputLine.equalsIgnoreCase("leave") || inputLine.equals("l")) {
                if (this.peerLauncher.getRemotePeers().size() == 0) {
                    this.peerLauncher
                            .printInformation("Impossible to perform leave action, the network has no peer.");
                } else {
                    this.peerLauncher.performLeave();
                }
            } else if (inputLine.equalsIgnoreCase("search") || inputLine.equals("s")) {
                this.peerLauncher.performSearch();
            } else if (inputLine.equalsIgnoreCase("n")) {
                this.peerLauncher.printInformation(this.peerLauncher.getRemotePeers().size() +
                    " peer(s) on the network.");
            } else if (inputLine.equalsIgnoreCase("ll")) {
                if (this.peerLauncher.getRemotePeers().size() == 0) {
                    this.peerLauncher.printInformation("There is no peer on the network.");
                } else {
                    StringBuffer buf = new StringBuffer();
                    int i = 1;
                    for (Peer peer : this.peerLauncher.getRemotePeers()) {
                        buf.append("    " + i + ". ");
                        buf.append(((CANOverlay) peer.getStructuredOverlay()).getZone());
                        buf.append("\n");
                        i++;
                    }

                    this.peerLauncher.printInformation("The following peer(s) are on the network :\n\n" +
                        buf.toString());
                }
            } else if (inputLine.startsWith("random")) {
                int nbOperations = Integer.parseInt(inputLine.split(" ")[1]);

                for (int i = 0; i < nbOperations; i++) {
                    Random rand = new Random();
                    int res = rand.nextInt(3);
                    switch (res) {
                        case 0:
                            this.peerLauncher.performJoin();
                            break;
                        case 1:
                            this.peerLauncher.performSearch();
                            break;
                        case 2:
                            this.peerLauncher.performLeave();
                            break;
                        default:
                            break;
                    }
                }
            }

            this.printOptions();
        }
    }

    /**
     * Print app menu option on the standard output.
     */
    public void printOptions() {
        System.out.println("[ Select an action to perform ]");
        System.out.println("  > Type in 'join' : add a new peer at a random position");
        System.out.println("  > Type in 'leave' : force a random peer to quit the network");
        System.out.println("  > Type in 'search' : send a lookup message to a random peer");
        System.out.println("  > Type in 'random x' : perform x random operations (join, leave, search)");
        System.out.println("  > Type in 'n' : give the number of peers on the network");
        System.out.println("  > Type in 'll' : list all peers on the network");
        System.out.println("  > Type in 'quit' : quit the application");
    }
}
