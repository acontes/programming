package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

/**
 * All implementations of node figures should extend this class.
 * 
 * @author vasile
 * 
 */
public abstract class AbstractNode3D extends AbstractFigure3D {
    /**
     * @param name
     *            figure name to be displayed
     */
    public AbstractNode3D(final String name) {
        super(name);
    }
}
