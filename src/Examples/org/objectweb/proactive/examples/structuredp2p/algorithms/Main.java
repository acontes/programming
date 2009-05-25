package org.objectweb.proactive.examples.structuredp2p.algorithms;

import javax.swing.SwingUtilities;


public class Main {
    public static void main(String arv[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Zone zone = new Zone();
                GraphicalUserInterface g = new GraphicalUserInterface(zone);
                g.setVisible(true);
            }
        });

    }
}
