/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractRuntime3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Node3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Runtime3D;


/**
 * @author esalagea
 *
 */
public class Runtime3DController extends AbstractRuntime3DController {
    public Runtime3DController(AbstractData modelObject,
        AbstractFigure3D figure3D, AbstractFigure3DController parent) {
        super(modelObject, figure3D, parent);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param figureModel
     * @param parentFigure
     */

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createChildController(org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData)
     */
    @Override
    protected AbstractFigure3DController createChildController(
        AbstractData modelObject) {
        return new Node3DController(modelObject, getFigure(), this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
     */
    @Override
    protected AbstractFigure3D createFigure(String name) {
        return new Runtime3D(name);
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
}
