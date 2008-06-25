/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed;

import java.util.Observable;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.NodeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu.MenuAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Node3D;


/**
 * @author esalagea
 * 
 */
public class Node3DController extends AbstractNode3DController {
    public Node3DController(final AbstractData modelObject, final Figure3D figure3D,
            final Figure3DController parent) {
        super(modelObject, figure3D, parent);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createChildController(org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData)
     */
    @Override
    protected AbstractFigure3DController createChildController(final AbstractData modelObject) {
        return new ActiveObject3DController(modelObject, this.getFigure(), this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
     */
    @Override
    protected AbstractFigure3D createFigure(final String name) {
    	Node3D node = new Node3D(name);
    	node.addObserver(this);
        return node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#removeFigure(java.lang.String)
     */
    @Override
    public void removeFigure(final String key) {
    }
    
    @Override
    public void update(final Observable o, final Object arg) {
    	if ( o != null) {
    		super.update(o, arg);
    	}
    	else {
    		MenuAction menuAction = (MenuAction)arg;
    		NodeObject node = (NodeObject)this.getModelObject();
    		switch (menuAction) {
				case NODE_REFRESH:
					node.explore();
					break;
				case NODE_STOP_MONITORING:
					node.stopMonitoring(true);
					break;
				case NODE_CHARTIT:
					break;
    		}
    	}
    }
}
