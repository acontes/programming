package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;

/**
 * All the implementations of host figures should extend this class.
 * 
 * @author vjuresch
 * 
 */
public abstract class AbstractHost3D extends AbstractFigure3D {
    /**
     * @param name
     *            figure name to be displayed
     */
    public AbstractHost3D(final String name) {
        super(name);
    }
}
