/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.FigureType;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.PlacementBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;

import com.sun.j3d.utils.geometry.Sphere;


/**
 * @author vjuresch
 * 
 */
public class EmptyLoadHost3D extends AbstractLoadHost3D {

	public EmptyLoadHost3D(String name) {
        super(name);
    }

	/*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
     */
    
    public void arrangeSubFigures() {
        int i = 1;
        for (final Figure3D runtime : this.getSubFigures().values()) {
            PlacementBasket.matrixArrangement2(i, runtime, this.getSubFigures().size(), new Vector3f(0f, 1f, 0f));
            i++;
            runtime.arrangeSubFigures();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
     */
    
    protected Geometry createGeometry() {
   	return GeometryBasket.getSmoothHostGeometry();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
     */
    
    protected TransformGroup createTextBranch() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
     */
    
    public void animateCreation() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
     */
    
    protected Appearance createAppearance() {
        return AppearanceBasket.defaultHostAppearance;
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
		return FigureType.HOST;
	}
	
	public void setScale(final double loadScale) {
    }
}
