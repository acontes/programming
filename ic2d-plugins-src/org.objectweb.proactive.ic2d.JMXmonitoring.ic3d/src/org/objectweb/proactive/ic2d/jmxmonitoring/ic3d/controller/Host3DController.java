/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractHost3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Runtime3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Host3D;


/**
 * @author vjuresch
 *
 */
public class Host3DController extends AbstractHost3DController {
    public Host3DController(AbstractData modelObject, AbstractFigure3D figure3D,
            AbstractFigure3DController parent) {
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
    protected AbstractFigure3D createFigure(String name) {
        //    	Logger.getRootLogger().log(Priority.INFO_INT, "Creating figure for host controller:"+name);
        return new Host3D(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#removeFigure(java.lang.String)
     */
    @Override
    public void removeFigure(String key) {
        // TODO Auto-generated method stub
    }

    @Override
    protected AbstractFigure3DController createChildController(AbstractData modelObject) {
        return new Runtime3DController(modelObject, getFigure(), this);
    }
}
