/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.load;

import java.util.Observable;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Grid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.MonitorGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;


/**
 * @author vjuresch
 * 
 */
public class LoadGrid3DController extends AbstractLoadGrid3DController {
    public static final Point3d GRID_POSITION = new Point3d(0, 300, 0);

    public LoadGrid3DController(final AbstractData modelObject, final Figure3D parentFigure3D,
            final Figure3DController parent) {

        super(modelObject, parentFigure3D, parent);

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
        MonitorGrid3D grid = new MonitorGrid3D();
        ((MonitorGrid3D) grid).setTranslation(new Vector3d(GRID_POSITION));
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
}
