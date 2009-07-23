package org.objectweb.proactive.examples.structuredp2p.can;

import java.util.Random;
import java.util.Scanner;


public class StressTestThread implements Runnable {

    private PeerLauncher peerLauncher;
    private boolean running = true;

    private boolean performJoin = false;
    private boolean performLeave = false;
    private boolean performSearch = false;

    public StressTestThread(PeerLauncher peerLauncher, boolean performJoin, boolean performLeave,
            boolean performSearch) {
        this.peerLauncher = peerLauncher;
        this.performJoin = performJoin;
        this.performLeave = performLeave;
        this.performSearch = performSearch;
    }

    public void run() {
        new Thread(new KeyListenerThread(this)).start();

        while (this.running) {
            Random rand = new Random();
            int res = rand.nextInt(3);
            switch (res) {
                case 0:
                    if (this.performJoin) {
                        this.peerLauncher.performJoin();
                    }
                    break;
                case 1:
                    if (this.performLeave) {
                        this.peerLauncher.performLeave();
                    }
                    break;
                case 2:
                    if (this.performSearch) {
                        this.peerLauncher.performSearch();
                    }
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
