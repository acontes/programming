/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import java.util.Map;
import java.util.Observable;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.editpart.AbstractMonitoringEditPart;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractGrid3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Host3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Grid3D;


/**
 * @author vjuresch
 *
 */
public class Grid3DController extends AbstractGrid3DController {
    public Grid3DController(AbstractData modelObject,
        AbstractFigure3D figure3D, AbstractFigure3DController parent) {
        super(modelObject, figure3D, parent);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
        super.update(o, arg);
    }

    @Override
    protected AbstractFigure3D createFigure(String name) {
        // TODO Auto-generated method stub
        // the grid has no name in the current implementation
        return new Grid3D();
    }

    @Override
    public void removeFigure(String key) {
        // TODO Auto-generated method stub
    }

    @Override
    protected Host3DController createChildController(AbstractData figure) {
        // TODO Auto-generated method stub
        return new Host3DController(figure, getFigure(), this);
    }
}
