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
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.TextStylesBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;


/**
 * @author vjuresch
 * 
 */
public class LoadRuntime3D extends AbstractLoadRuntime3D {
    private static final double LOAD_HIGH_THRESHOLD = 2d/3d;
	private static final double LOAD_AVERAGE_THRESHOLD = 1d/3d;

	public LoadRuntime3D(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
     */
    @Override
    public void arrangeSubFigures() { }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
     */
    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getBarMonitorGeometry();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
     */
    @Override
    protected TransformGroup createTextBranch() {
        return TextStylesBasket.runtimeText(this.getShortenedName(4));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
     */
    @Override
    public void animateCreation() {
    	return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
     */
    @Override
    protected Appearance createAppearance() {
        return AppearanceBasket.monitor;
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

	@Override
	public FigureType getType() {
		// TODO Auto-generated method stub
		return FigureType.RUNTIME;
	}
	
    public void setScale(final double loadScale) {
    	//check that the arguments are in the correct range
    	/*if ((loadScale > 1) || (loadScale < 0)) {
            throw new IllegalArgumentException("The scale  " + loadScale + " should be in the [0,1] range");
        }

    	if (loadScale > LoadRuntime3D.LOAD_HIGH_THRESHOLD) {
            this.setAppearance(AppearanceBasket.monitorFull);
        } else if (loadScale > LoadRuntime3D.LOAD_AVERAGE_THRESHOLD) {
            this.setAppearance(AppearanceBasket.monitor);
        } else {
            this.setAppearance(AppearanceBasket.monitorLow);
        }

        final TransformGroup translateScaleTransform = this.getTranslateScaleTransform();
        final Transform3D translateScaleTransform3D = new Transform3D();
        translateScaleTransform.getTransform(translateScaleTransform3D);
        translateScaleTransform3D.setScale(new Vector3d(1, loadScale, 1));
        translateScaleTransform.setTransform(translateScaleTransform3D);*/
    }
}