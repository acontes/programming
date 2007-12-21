/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;


/**
 * @author vjuresch
 *
 */
public abstract class AbstractHost3DController
    extends AbstractFigure3DController {
    public AbstractHost3DController(AbstractData modelObject,
        AbstractFigure3D figure3D, AbstractFigure3DController parent) {
        super(modelObject, figure3D, parent);
        // TODO Auto-generated constructor stub
    }
}
