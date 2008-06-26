package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.deprecated;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;


public class TemporaryMouseListener implements MouseListener {
    JPanel destination;
    JPanel source;

    public TemporaryMouseListener(final JPanel s, final JPanel d) {
        this.destination = d;
        this.source = s;
    }

    public void mouseClicked(final MouseEvent e) {
        // if not clicked in the main view
        if (e.getComponent() != this.destination.getComponent(0)) {
            System.out.println(e.getComponent());
            final Component dest = this.destination.getComponent(0);
            // remove from source
            this.source.remove(e.getComponent());
            // get the destination component and put it in the source
            this.source.add(dest);
            this.source.validate();
            this.destination.removeAll();
            this.destination.add(e.getComponent());
            // move and resize to container size
            e.getComponent().setBounds(this.destination.getBounds());
        }
    }

    public void mouseEntered(final MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(final MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mousePressed(final MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseReleased(final MouseEvent e) {
        // TODO Auto-generated method stub
    }
}
