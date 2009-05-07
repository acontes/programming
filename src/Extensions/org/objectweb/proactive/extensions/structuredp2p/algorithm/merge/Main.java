package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import javax.swing.SwingUtilities;


public class Main {
    public static void main(String arv[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GraphicalUserInterface g = new GraphicalUserInterface(new CANPeer());
                g.setVisible(true);
            }
        });

    }
}
