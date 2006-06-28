package org.objectweb.proactive.ic2d.p2PMonitoring.views;

import java.awt.GridLayout;

import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.p2p.jung.JungGUI;


public class P2PView extends ViewPart {
    javax.swing.JPanel panel = new javax.swing.JPanel();
    static JungGUI gui = new JungGUI();

    public static JungGUI getGUI() {
        return gui;
    }

    @Override
    public void createPartControl(Composite parent) {
        Composite swtAwtComponent = new Composite(parent, SWT.EMBEDDED);
        java.awt.Frame frame = SWT_AWT.new_Frame(swtAwtComponent);

        panel.setLayout(new GridLayout(1, 1));
        panel.add(gui.getPanel());
        frame.add(panel);
    }

    @Override
    public void setFocus() {
        
    }

    public static void loadDumpFile(final String filename) {
        new Thread() {
                public void run() {
                    gui.createGraphFromFile2(filename);
                }
            }.start();
    }
}
