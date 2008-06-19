package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.CameraBehavior;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.FlatCameraBehavior;

import com.sun.j3d.utils.universe.SimpleUniverse;


public class CustomView {
    /* Every view share the same body and environment */
    protected final static PhysicalBody physicalBody = new PhysicalBody();
    protected final static PhysicalEnvironment physicalEnvironment = new PhysicalEnvironment();

    /* The view port */
    protected Canvas3D canvas3D;

    /* View camera @nullable */
    protected CameraBehavior camera;

    /* View branch */
    protected BranchGroup viewBranch;

    protected TransformGroup cameraTransform;

    public CustomView() {
        canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        buildView();
    }

    private void buildView() {
        /* Set up the tree */
        viewBranch = new BranchGroup();
        viewBranch.setCapability(BranchGroup.ALLOW_DETACH);
        viewBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        cameraTransform = new TransformGroup();
        cameraTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        cameraTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        viewBranch.addChild(cameraTransform);

        /* Set up Camera */
        //camera = new FlatCameraBehavior(canvas3D, cameraTransform);
        //camera.setSchedulingBounds(new BoundingSphere(new BoundingSphere(new Point3d(), 150)));
        //viewBranch.addChild(camera);

        /* Prepare the view platform */
        ViewPlatform viewPlatform = new ViewPlatform();
        cameraTransform.addChild(viewPlatform);

        View view = new View();
        view.addCanvas3D(canvas3D);
        view.attachViewPlatform(viewPlatform);
        view.setBackClipDistance(50);
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());
        viewBranch.compile();
    }

    public CustomView(Locale locale) {
        canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        buildView();
        locale.addBranchGraph(viewBranch);
    }

    protected void setView(BranchGroup view) {
        this.viewBranch = view;
    }

    public BranchGroup getView() {
        return viewBranch;
    }

    protected void setCanvas3D(Canvas3D canvas3D) {
        this.canvas3D = canvas3D;
    }

    public Canvas3D getCanvas3D() {
        return canvas3D;
    }

    public CameraBehavior getCamera() {
        return camera;
    }

    public void setCamera(CameraBehavior cameraToSet, BranchGroup pickableGroup) {
        /* Destroy the old camera */
        if (camera != null)
            viewBranch.removeChild(camera.getBranchGroup());
        /* Set the local camera scene */
        cameraToSet.set(canvas3D, cameraTransform, pickableGroup);
        camera = cameraToSet;
        viewBranch.addChild(camera.getBranchGroup());
    }
}
