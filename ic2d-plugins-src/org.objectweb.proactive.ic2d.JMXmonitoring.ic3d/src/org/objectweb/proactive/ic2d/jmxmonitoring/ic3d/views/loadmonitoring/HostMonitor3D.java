package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.GeometryBasket;

public class HostMonitor3D extends AbstractFigure3D {
	public HostMonitor3D(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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
    		return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
     */
    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getBarMonitorGeometry();
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
        return null;
    }

    /*
     * (non-Javadoc)
    	((TransformGroup)host.getParent()).addChild(aNew);
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
     */
    @Override
    protected void animateCreation() {
        // new AnimationBasket().fadeInto(this, 2000);
    }

	@Override
	protected AbstractFigure3D setArrow(String name, Vector3f start,
			Vector3f stop) {
		// TODO Auto-generated method stub
		return null;
	}

    /*
     * This is used to visually update the charge loading of the object
     * According the charge we are currently monitoring
     * 
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(java.lang.String,
     *      javax.vecmath.Vector3f, javax.vecmath.Vector3f)
     */
	
	public void setChargeLoading(double myScale) {
		if(myScale > 1 || myScale < 0)
			throw new IllegalArgumentException("Argument: myScale: " + myScale + " should be in [0,1]");
		
		if(myScale > 0.8)
			this.setAppearance(AppearanceBasket.monitorFull);
		else if(myScale > 0.2)
			this.setAppearance(AppearanceBasket.monitor);
		else
			this.setAppearance(AppearanceBasket.monitorLow);
		
		TransformGroup translateScaleTransform = getTranslateScaleTransform();
		Transform3D translateScaleTransform3D = new Transform3D();
		translateScaleTransform.getTransform(translateScaleTransform3D);
		translateScaleTransform3D.setScale(new Vector3d(1, 1, myScale));
		translateScaleTransform.setTransform(translateScaleTransform3D);
		
	}
	
	private void changeTextureCoordinates(double myScale) {
		Geometry myGeometry = this.getGeometry(0);
		//
		
	}
}
