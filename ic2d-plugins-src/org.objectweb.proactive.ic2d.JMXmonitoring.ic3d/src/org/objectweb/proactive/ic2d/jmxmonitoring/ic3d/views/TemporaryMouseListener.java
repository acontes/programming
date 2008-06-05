package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;


public class TemporaryMouseListener implements MouseListener {
    JPanel destination;
    JPanel source;

    public TemporaryMouseListener(JPanel s, JPanel d) {
        destination = d;
        source = s;
    }

    public void mouseClicked(MouseEvent e) {
        // if not clicked in the main view
        if (e.getComponent() != destination.getComponent(0)) {
            System.out.println(e.getComponent());
            Component dest = destination.getComponent(0);
            // remove from source
            source.remove(e.getComponent());
            // get the destination component and put it in the source
            source.add(dest);
            source.validate();
            destination.removeAll();
            destination.add(e.getComponent());
            // move and resize to container size
            e.getComponent().setBounds(destination.getBounds());
        }
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }
}
