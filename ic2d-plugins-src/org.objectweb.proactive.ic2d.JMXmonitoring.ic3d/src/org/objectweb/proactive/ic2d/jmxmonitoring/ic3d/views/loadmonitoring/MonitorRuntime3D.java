package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;


public class MonitorRuntime3D extends AbstractFigure3D implements Figure3D {
    private static final double LOAD_AVERAGE_THRESHOLD = 0.2;
    private static final double LOAD_HIGH_THRESHOLD = 0.8;
    
    private static final float MAX_THREADS_NUMBER = 1000;
    private float maxHeap = 64;

    public MonitorRuntime3D(final String name) {
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
     * (non-Javadoc) ((TransformGroup)host.getParent()).addChild(aNew);
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
     */
    @Override
    public void animateCreation() {
        // new AnimationBasket().fadeInto(this, 2000);
    }

    @Override
    protected Figure3D setArrow(final String name, final Vector3f start, final Vector3f stop) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Visually update the number of threads of the object according to
     * the number of threads on the runtime we are currently monitoring.
     * 
     * @param threads
     */
    public void setThreads(final int threads) {
    	if (threads < 0) {
            throw new IllegalArgumentException("The number of threads  " 
            		+ threads + " cannot be smaller than 0.");
        }
    	setScale(threads * 1/MAX_THREADS_NUMBER);
    }
    
    
    /**
     * Visually update the heap memory used according to
     * the heap memory on the runtime we are currently monitoring.
     * 
     * @param heapUsed
     */
    public void setHeapUsed(final double heapUsed) {
    	if (heapUsed < 0) {
            throw new IllegalArgumentException("The heap used  " 
            		+ heapUsed + " cannot be smaller than 0.");
        }
    	setScale(heapUsed * 1/maxHeap);
    }
    
    
    private void setScale(final double loadScale){
    	//check that the arguments are in the correct range
    	if ((loadScale > 1) || (loadScale < 0)) {
            throw new IllegalArgumentException("The scale  " + loadScale + " should be in the [0,1] range");
        }

    	if (loadScale > MonitorRuntime3D.LOAD_HIGH_THRESHOLD) {
            this.setAppearance(AppearanceBasket.monitorFull);
        } else if (loadScale > MonitorRuntime3D.LOAD_AVERAGE_THRESHOLD) {
            this.setAppearance(AppearanceBasket.monitor);
        } else {
            this.setAppearance(AppearanceBasket.monitorLow);
        }

        final TransformGroup translateScaleTransform = this.getTranslateScaleTransform();
        final Transform3D translateScaleTransform3D = new Transform3D();
        translateScaleTransform.getTransform(translateScaleTransform3D);
        translateScaleTransform3D.setScale(new Vector3d(1, loadScale, 1));
        translateScaleTransform.setTransform(translateScaleTransform3D);
    }
    public void setMaxHeap(float maxHeap){
    	this.maxHeap = maxHeap;
    }
    public void setMonitor(MonitoringTypes monitor) {
		switch (monitor) {
		case RUNTIME_THREADS: {
			break;
		}
		case RUNTIME_HEAP_MEMORY_USED: {
			break;
		}
		}
	}
}
