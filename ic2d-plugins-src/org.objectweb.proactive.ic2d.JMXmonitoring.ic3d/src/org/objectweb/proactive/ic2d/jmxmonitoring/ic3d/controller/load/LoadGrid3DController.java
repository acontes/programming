/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.load;

import java.util.Observable;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.LoadGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.MonitoringTypes;


/**
 * @author vjuresch
 * 
 */
public class LoadGrid3DController extends AbstractLoadGrid3DController {
    
	private MonitoringTypes gridMode;
	
    public LoadGrid3DController(final AbstractData modelObject, final Figure3D parentFigure3D,
            final Figure3DController parent) {

        super(modelObject, parentFigure3D, parent);
        gridMode = MonitoringTypes.RUNTIME_HEAP_MEMORY_USED;
        // TODO Auto-generated constructor stub
    }

    public LoadGrid3DController(final AbstractData modelObject, final Figure3D parentFigure3D,
            final Figure3DController parent, final MonitoringTypes type) {

        super(modelObject, parentFigure3D, parent);
        this.gridMode = type;
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void update(final Observable o, final Object arg) {
    	if ( o != null) {
    		super.update(o, arg);
    	}
    }

    @Override
    protected AbstractFigure3D createFigure(final String name) {
        // TODO Auto-generated method stub
        // the grid has no name in the current implementation
        //FIXME assumes there is only one grid created
        LoadGrid3D grid = new LoadGrid3D();
        return grid;
    }

    @Override
    public void removeFigure(final String key) {
        // TODO Auto-generated method stub
    }

    @Override
    protected AbstractFigure3DController createChildController(final AbstractData figure) {
        // TODO Auto-generated method stub
        return new LoadHost3DController(figure, this.getFigure(), this);
    }
    
    public void setGridMode(MonitoringTypes type) {
    	this.gridMode = type;
    	// TODO reset all sub shapes
    }
    
    public MonitoringTypes getGridMode() {
    	return this.gridMode;
    }
}
