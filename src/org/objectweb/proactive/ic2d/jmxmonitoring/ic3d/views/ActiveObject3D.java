package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.Font;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Text2D;


/**
 * @author vjuresch
 *
 */
public class ActiveObject3D extends AbstractActiveObject3D {
    RotationInterpolator rot;

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

    protected Appearance createAppearance() {
        return AppearanceBasket.defaultActiveObjectAppearance;
    }

    @Override
    public void setQueueSize(int size) {
        // TODO Auto-generated method stub
    }

    @Override
    public void arrangeSubFigures() {
        // TODO Auto-generated method stub
    }

    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getDefaultActiveObjectGeometry();
    }

    @Override
    protected TransformGroup createTextBranch() {
        // add the text
        Text2D figureName = new Text2D(this.getShortenedName(10),
                ColorPalette.BLACK, Font.MONOSPACED, 30, Font.BOLD);

        TransformGroup fontRotate = createTransform();
        Transform3D rotate = new Transform3D();
        rotate.rotX(Math.PI / 2);
        fontRotate.setTransform(rotate);

        // move a little the font shape
        TransformGroup fontTrans = createTransform();
        Transform3D translate = new Transform3D();
        // TODO remove constants
        translate.setTranslation(new Vector3d(1.5d, -1d, 0d));
        translate.setScale(6);
        // TODO make text slanted on object
        fontTrans.setTransform(translate);

        fontRotate.addChild(figureName);
        fontTrans.addChild(fontRotate);
        // add the transform the the branch group
        return fontTrans;
    }

    public void setMigrating() {
        this.setAppearance(AppearanceBasket.objectMigratingAppearance);
    }

    public void setServingRequest() {
        // Transform3D t = new Transform3D();
        // t.set(new AxisAngle4d(0d, 1d, 1d, Math.PI));
        // rot.setTransformAxis(t);
        // rot.setEnable(true);
        this.setAppearance(AppearanceBasket.servingRequestAppearance);
    }

    public void setWaitingForRequest() {
        // rot.setEnable(false);
        this.setAppearance(AppearanceBasket.waitingForRequest);
    }

    @Override
    public void setActive() {
        // TODO Auto-generated method stub
    }

    @Override
    public void setUnknown() {
        // rot.setEnable(false);
        this.setAppearance(AppearanceBasket.unkown);
    }
}
