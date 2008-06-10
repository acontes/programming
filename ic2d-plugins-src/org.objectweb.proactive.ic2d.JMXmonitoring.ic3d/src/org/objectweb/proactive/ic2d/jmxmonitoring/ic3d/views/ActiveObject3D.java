package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;


/**
 * An implementation for an active
 * object figure. This implementation uses
 * a sphere geometry. 
 * @author vjuresch
 *
 */
public class ActiveObject3D extends AbstractActiveObject3D {
    RotationInterpolator rot;
    private Queue3D queue = new Queue3D(""); //an active object has only one queue
    private boolean noQueue = true;

    public ActiveObject3D(String name) {
        super(name);
        // //add a rotation interpolator for
        // //animating the active object (rotation)
        // TransformGroup transRotate = (TransformGroup) this.getParent();
        // Alpha alpha = new Alpha(-1, 9000);
        // rot = new RotationInterpolator(alpha, transRotate);
        // rot.setSchedulingBounds(new BoundingSphere());
        // //the branch group is needed in order to be able
        // //to add to the compiled scene
        // BranchGroup bg = createBranch();
        // bg.addChild(rot);
        // transRotate.addChild(bg);
        // rot.setEnable(false);
    }

    @Override
    protected Appearance createAppearance() {
        return AppearanceBasket.defaultActiveObjectAppearance;
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractActiveObject3D#setQueueSize(int)
     */
    @Override
    public void setQueueSize(int size) {
        //TODO hacky, change
        if (noQueue) {
            addSubFigure(new Double(Math.random()).toString(), queue);
            noQueue = false;

        }
        assert size > 0;
        TransformGroup trans = (TransformGroup) queue.getParent().getParent();
        Transform3D resize = new Transform3D();
        trans.getTransform(resize);
        Vector3d oldScale = new Vector3d();
        resize.getScale(oldScale);
        //TODO remove constant
        resize.setScale(new Vector3d((double) size / 2, oldScale.y, oldScale.z));
        trans.setTransform(resize);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
     */
    @Override
    public void arrangeSubFigures() {
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
     */
    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getDefaultActiveObjectGeometry();
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
     */
    @Override
    protected TransformGroup createTextBranch() {
        return TextStylesBasket.activeObjectText(this.getShortenedName(10));
    } /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractActiveObject3D#setStateMigrating()
     */

    @Override
    public void setStateMigrating() {

        this.setAppearance(AppearanceBasket.objectMigratingAppearance);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractActiveObject3D#setStateServingRequest()
     */
    @Override
    public void setStateServingRequest() {
        //		Transform3D t = new Transform3D();
        //		t.set(new AxisAngle4d(0d, 1d, 1d, Math.PI));
        //		rot.setTransformAxis(t);
        //		rot.setEnable(true);
        this.setAppearance(AppearanceBasket.servingRequestAppearance);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractActiveObject3D#setStateWaitingForRequest()
     */
    @Override
    public void setStateWaitingForRequest() {
        //      rot.setEnable(false);
        this.setAppearance(AppearanceBasket.waitingForRequestAppearance);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractActiveObject3D#setStateActive()
     */
    @Override
    public void setStateActive() {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
     */
    @Override
    protected void animateCreation() {
        new AnimationBasket().fadeInto(this, 2000);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(javax.vecmath.Vector3f, javax.vecmath.Vector3f)
     */
    @Override
    protected AbstractFigure3D setArrow(String name, Vector3f start, Vector3f stop) {
        return null;
    }

}
