package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.PlacementBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.TextStylesBasket;


/**
 * An implementation of a host figure. The geometry used is a flat 1x1 square
 * 
 * @author vasile
 * 
 */
public class Host3D extends AbstractHost3D {
    /**
     * @param name
     *            the host name to be displayed
     */
    public Host3D(final String name) {
        super(name);
        System.out.println(name);
    }

    /*
     * The subfigure is attached to the transform of the figure itself,
     * therefore any placement takes place in the local coordinate system of the
     * figure.
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
     */
    @Override
    public void arrangeSubFigures() {
        int i = 1; // to count the runtimes
        for (final Figure3D runtime : this.getSubFigures().values()) {
        	PlacementBasket.matrixSubArrangement(i, runtime, this.getSubFigures().size(), new Vector3f(0f, 0.1f, 0f));
        	//PlacementBasket.xArrangement(0.1, 0.1, 0.1, i, this.getSubFigures().size(), runtime, this);
            i++;
            runtime.arrangeSubFigures();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
     */
    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getSmoothHostGeometry();
    }

    // code to create default appearance of visual object
    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
     */
    @Override
    protected Appearance createAppearance() {
        return AppearanceBasket.defaultHostAppearance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
     */
    @Override
    protected TransformGroup createTextBranch() {
        return TextStylesBasket.hostText(this.getShortenedName(15));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
     */
    @Override
    public void animateCreation() {
        // new AnimationBasket().fadeInto(this, 2000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(java.lang.String,
     *      javax.vecmath.Vector3f, javax.vecmath.Vector3f)
     */
    @Override
    protected Figure3D setArrow(final String name, final Vector3f start, final Vector3f stop) {
        // TODO Auto-generated method stub
        return new Arrow3D("", start, stop);
    }
}