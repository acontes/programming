package org.objectweb.proactive.examples.structuredp2p.can;

import java.util.Random;
import java.util.Scanner;


public class StressTestThread implements Runnable {

    private PeerLauncher peerLauncher;
    private boolean running = true;

    public StressTestThread(PeerLauncher peerLauncher) {
        this.peerLauncher = peerLauncher;
    }

    public void run() {
        new Thread(new KeyListenerThread(this)).start();

        while (this.running) {
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

    public void setRunning(boolean b) {
        this.running = b;
    }

    public class KeyListenerThread implements Runnable {

        private boolean running = true;
        private StressTestThread stressTestThread;

        public KeyListenerThread(StressTestThread stressTestThread) {
            this.stressTestThread = stressTestThread;
        }

        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (this.running) {
                if (scanner.next().equals("q")) {
                    this.stressTestThread.setRunning(false);
                    this.running = false;
                    scanner.close();
                }
            }
        }
    }
}
