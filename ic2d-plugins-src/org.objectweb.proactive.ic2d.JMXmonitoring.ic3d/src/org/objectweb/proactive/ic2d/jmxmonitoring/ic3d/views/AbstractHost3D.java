package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

/**
 * All the implementations of host figures
 * should extend this class.
 * @author vjuresch
 *
 */
public abstract class AbstractHost3D extends AbstractFigure3D {
    /**
     * @param name figure name to be displayed
     */
    public AbstractHost3D(String name) {
        super(name);
    }
}
