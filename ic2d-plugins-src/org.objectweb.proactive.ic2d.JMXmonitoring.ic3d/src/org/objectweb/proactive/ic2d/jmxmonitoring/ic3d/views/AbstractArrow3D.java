/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

/**
 * All arrows implementations should extend this class
 * @author vjuresch
 *
 */
public abstract class AbstractArrow3D extends AbstractFigure3D {
    /**
     * @param name  arrow name, usually not displayed
     */
    public AbstractArrow3D(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor that doesn't take any name for the arrow.
     * The name is an empty string.
     */
    public AbstractArrow3D() {
        super("");
    }
}