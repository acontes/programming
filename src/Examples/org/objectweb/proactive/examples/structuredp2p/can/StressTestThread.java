package org.objectweb.proactive.examples.structuredp2p.can;

import java.util.Random;


public class StressTestThread implements Runnable {

    PeerLauncher peerLauncher;

    public StressTestThread(PeerLauncher peerLauncher) {
        this.peerLauncher = peerLauncher;
    }

    public void run() {
        for (;;) {
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
}
