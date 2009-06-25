package org.objectweb.proactive.examples.structuredp2p.can;

public class Launcher {

    public static void main(String[] args) {
        new TrackerLauncher(args);
        PeerLauncher peerLauncher = new PeerLauncher(args);
        /*
         * if (CANOverlay.NB_DIMENSIONS == 2) { ContentAddressableNetworkGUI gui = new
         * ContentAddressableNetworkGUI(peerLauncher); peerLauncher.addObserver(gui);
         * gui.setVisible(true); }
         */
    }
}
