/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed;

import java.util.Observable;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.ic2d.chartit.data.resource.IResourceDescriptor;
import org.objectweb.proactive.ic2d.chartit.editor.ChartItDataEditor;
import org.objectweb.proactive.ic2d.jmxmonitoring.action.ChartItAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.NodeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu.MenuAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;
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
    
    protected AbstractFigure3DController createChildController(final AbstractData modelObject) {
        return new ActiveObject3DController(modelObject, this.getFigure(), this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
     */
    
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
    
    public void removeFigure(final String key) {
    }
    
    
    public void update(final Observable o, final Object arg) {
    	if ( o != null) {
    		super.update(o, arg);
    	}
    	else {
    		if( arg instanceof MenuAction) {
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
						try {
							final IResourceDescriptor descriptor = new AbstractDataDescriptor(node);
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									try {
										ChartItDataEditor.openNewFromResourceDescriptor(descriptor,ChartItAction.PARUNTIME_CHARTIT_CONFIG_FILENAME);
									} catch (PartInitException e) {									
										e.printStackTrace();
									}
								}
							});											
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
    			}
    		}
    		else if ( arg instanceof ActiveObject) {
    			ActiveObject ao = (ActiveObject)arg;
    			NodeObject no = (NodeObject)this.getModelObject();
    			try {
    				ao.migrateTo(no.getUrl());
    			} catch (Exception migrationException) {
    				migrationException
    				.printStackTrace();
    			}
    		}
    	}
    }
}
