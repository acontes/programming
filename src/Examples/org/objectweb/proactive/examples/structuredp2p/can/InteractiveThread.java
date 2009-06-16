package org.objectweb.proactive.examples.structuredp2p.can;

import java.util.Random;
import java.util.Scanner;

import org.objectweb.proactive.examples.structuredp2p.util.Deployment;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


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
            } else if (inputLine.equalsIgnoreCase("add")) {
                this.peerLauncher.addPeer();
            } else if (inputLine.equalsIgnoreCase("leave")) {
                this.peerLauncher.removePeer();
            } else if (inputLine.equalsIgnoreCase("lookup")) {
                this.peerLauncher.lookupMessage();
            } else if (inputLine.equalsIgnoreCase("nop")) {
                this.peerLauncher.printInformation(this.peerLauncher.getRemotePeers().size() +
                    " peer(s) on the network.");
            } else if (inputLine.startsWith("random")) {
                int nbOperations = Integer.parseInt(inputLine.split(" ")[1]);

                for (int i = 0; i < nbOperations; i++) {
                    Random rand = new Random();
                    int res = rand.nextInt(3);
                    switch (res) {
                        case 0:
                            this.peerLauncher.addPeer();
                            break;
                        case 1:
                            this.peerLauncher.lookupMessage();
                            break;
                        case 2:
                            this.peerLauncher.removePeer();
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
        System.out.println("  > Type in 'add' : add a new peer at a random position");
        System.out.println("  > Type in 'lookup' : send a lookup message to a random peer");
        System.out.println("  > Type in 'leave' : force a random peer to quit the network");
        System.out.println("  > Type in 'random x' : perform x random operations (lookup, join, leave)");
        System.out.println("  > Type in 'nop' : give the number of peers on the network");
        System.out.println("  > Type in 'quit' : quit the application");
    }
}
