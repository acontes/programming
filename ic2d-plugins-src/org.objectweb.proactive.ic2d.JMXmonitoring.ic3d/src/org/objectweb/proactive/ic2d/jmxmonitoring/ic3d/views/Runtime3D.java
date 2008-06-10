/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;


/**
 * An implementation for a runtime figure. It is a 1x1x1 cube that gets resized
 * on placed on a host.
 * 
 * @author vjuresch
 * 
 */
public class Runtime3D extends AbstractRuntime3D {

    /**
     * @param name
     *            name of the figure to be displayed
     */
    public Runtime3D(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
     */
    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getDefaultRuntimeGeometry();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
     */
    @Override
    protected Appearance createAppearance() {
        return AppearanceBasket.defaultRuntimeAppearance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
     */
    @Override
    public void arrangeSubFigures() {
        int i = 1; // to count the nodes
        for (AbstractFigure3D node : this.getSubFigures().values()) {
            PlacementBasket.yArrangement(0.05, 0.05, 2, i, this.getSubFigures().size(), node, this);
            i++;
            node.arrangeSubFigures();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
     */
    @Override
    protected TransformGroup createTextBranch() {
        return TextStylesBasket.runtimeText(getShortenedName(3));
    }

    @Override
    protected void animateCreation() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(javax.vecmath.Vector3f,
     *      javax.vecmath.Vector3f)
     */
    @Override
    protected AbstractFigure3D setArrow(String name, Vector3f start, Vector3f stop) {
        // TODO Auto-generated method stub
        return null;
    }
}
