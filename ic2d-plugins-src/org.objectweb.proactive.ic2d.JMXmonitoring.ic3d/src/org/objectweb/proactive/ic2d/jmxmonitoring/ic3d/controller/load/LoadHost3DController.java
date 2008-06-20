/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.load;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Grid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Host3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.MonitorHost3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;


/**
 * @author vjuresch
 * 
 */
public class LoadHost3DController extends AbstractLoadHost3DController {
    public LoadHost3DController(final AbstractData modelObject, final Figure3D figure3D,
            final Figure3DController parent) {
        super(modelObject, figure3D, parent);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#addFigureByKey(java.lang.String,
     *      java.lang.String)
     */
    @Override
    protected AbstractFigure3D createFigure(final String name) {
        MonitorHost3D monitorHost3D = new MonitorHost3D(name);
        monitorHost3D.setLoad(0);
    	return monitorHost3D;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#removeFigure(java.lang.String)
     */
    @Override
    public void removeFigure(final String key) {
        // TODO Auto-generated method stub
    }

    @Override
    protected Figure3DController createChildController(AbstractData figure) {
        return new LoadRuntime3DController(figure, this.getFigure(), this);

    }
}
