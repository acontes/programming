/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.FigureType;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.PlacementBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.TextStylesBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;


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
    
    protected Geometry createGeometry() {
        return GeometryBasket.getSmoothRuntimeGeometry();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
     */
    
    protected Appearance createAppearance() {
        return AppearanceBasket.defaultRuntimeAppearance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
     */
    
    public void arrangeSubFigures() {
        int i = 1; // to count the nodes
        for (final Figure3D node : this.getSubFigures().values()) {
            PlacementBasket.matrixArrangement(i, node, this.getSubFigures().size(), new Vector3f(0f, 2f, 0f));
        	//PlacementBasket.yArrangement(0.05, 0.05, 2, i, this.getSubFigures().size(), node, this);
            i++;
            node.arrangeSubFigures();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
     */
    
    protected TransformGroup createTextBranch() {
        return TextStylesBasket.runtimeText(this.getShortenedName(3));
    }

    
    public void animateCreation() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(javax.vecmath.Vector3f,
     *      javax.vecmath.Vector3f)
     */
    
    protected Figure3D setArrow(final String name, final Vector3f start, final Vector3f stop) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    public FigureType getType() {
    	// TODO Auto-generated method stub
    	return FigureType.RUNTIME;
    }
}
