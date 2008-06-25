package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed;
import java.util.Observable;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Grid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.proearth.EarthGrid3D;


/*
 * 
 */
public class EarthGrid3DController extends AbstractEarthGrid3DController {
    public EarthGrid3DController(final AbstractData modelObject, final Figure3D figure3D,
            final Figure3DController parent) {
        super(modelObject, figure3D, parent);
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
        return new EarthGrid3D(name);
    }

    @Override
    public void removeFigure(final String key) {
        // TODO Auto-generated method stub
    }

    @Override
    protected AbstractFigure3DController createChildController(final AbstractData figure) {
        // TODO Auto-generated method stub
        return new Host3DController(figure, this.getFigure(), this);
    }
}
