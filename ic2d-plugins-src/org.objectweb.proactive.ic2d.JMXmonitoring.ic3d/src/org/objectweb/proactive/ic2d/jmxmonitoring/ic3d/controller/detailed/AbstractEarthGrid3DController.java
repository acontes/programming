package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;


/**
* @author sbauer
* 
*/
public abstract class AbstractEarthGrid3DController extends AbstractFigure3DController {
   public AbstractEarthGrid3DController(final AbstractData modelObject, final Figure3D figure3D,
           final Figure3DController parent) {
       super(modelObject, figure3D, parent);
       // TODO Auto-generated constructor stub
   }
}
