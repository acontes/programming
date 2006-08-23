package org.objectweb.proactive.p2p.monitoring.jung;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import edu.uci.ics.jung.visualization.PickSupport;
import edu.uci.ics.jung.visualization.SettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;


public class PeerPopupMenuPlugin extends AbstractPopupGraphMousePlugin {
    SettableVertexLocationFunction vertexLocations;

    public PeerPopupMenuPlugin(SettableVertexLocationFunction vertexLocations) {
        this.vertexLocations = vertexLocations;
    }

    @Override
    protected void handlePopup(MouseEvent e) {
        final VisualizationViewer vv = (VisualizationViewer) e.getSource();
        final Point2D ivp = vv.inverseViewTransform(e.getPoint());
        PickSupport pickSupport = vv.getPickSupport();
        if (pickSupport != null) {
            final P2PUndirectedSparseVertex vertex = (P2PUndirectedSparseVertex) pickSupport.getVertex(ivp.getX(),
                    ivp.getY());
            JPopupMenu popup = new JPopupMenu();
            if (vertex != null) {
                JMenu submenu = new JMenu(vertex.getName());
                JMenu noaSubmenu = new JMenu("Noa : " + vertex.getNoa());
                JMenu maxNoaSubmenu = new JMenu("Max Noa : " + vertex.getMaxNoa());
         
                
                
                
                submenu.add(noaSubmenu);
                submenu.add(maxNoaSubmenu);
                popup.add(submenu);

               noaSubmenu.add(new AbstractAction("Set Noa") {
                    public void actionPerformed(ActionEvent e) {
                    	SetValueDialog v = new SetValueDialog("Set Value", "Noa",vertex.getNoa());
                    	v.setVisible(true);
                        System.out.println(v.getValue());
                    }
                });
                
                
                maxNoaSubmenu.add(new AbstractAction("Set Max Noa") {
                        public void actionPerformed(ActionEvent e) {
                        	SetValueDialog v = new SetValueDialog("Set Value", "Max Noa",vertex.getMaxNoa());
                        	v.setVisible(true);
                            System.out.println(v.getValue());
                        }
                    });
                popup.add(new AbstractAction("Update") {
                    public void actionPerformed(ActionEvent e) {
                    	//SetValueDialog v = new SetValueDialog("Set Value", "Max Noa",vertex.getMaxNoa());
                    //	v.setVisible(true);
                        System.out.println("Update!");
                    }
                });
                
                
            }
            popup.show(vv, e.getX(), e.getY());
        }
    }
}
