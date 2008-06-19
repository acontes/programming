/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import java.util.UUID;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.PlacementBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.TextStylesBasket;


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
    public Runtime3D(final String name) {
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
        for (final Figure3D node : this.getSubFigures().values()) {
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
        return TextStylesBasket.runtimeText(this.getShortenedName(3));
    }

    @Override
    public void animateCreation() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(javax.vecmath.Vector3f,
     *      javax.vecmath.Vector3f)
     */
    @Override
    protected Figure3D setArrow(final String name, final Vector3f start, final Vector3f stop) {
        // TODO Auto-generated method stub
        return null;
    }

    /** ************************* */
    /* Threads usage indicator */
    /* FOR TESTING */
    /** ************************ */

    private final Queue3D queue = new Queue3D("");
    private boolean noQueue = true;

    public void setThreads(final int size) {
        // TODO hacky, change
        if (this.noQueue) {
            this.addSubFigure(UUID.randomUUID().toString(), this.queue);
            this.noQueue = false;

        }
        assert size > 0;
        final TransformGroup trans = (TransformGroup) this.queue.getParent().getParent();
        final Transform3D resize = new Transform3D();
        trans.getTransform(resize);
        final Vector3d oldScale = new Vector3d();
        resize.getScale(oldScale);
        // TODO remove constant
        resize.setScale(new Vector3d((double) size / 2, oldScale.y, oldScale.z));
        trans.setTransform(resize);
    }
}
