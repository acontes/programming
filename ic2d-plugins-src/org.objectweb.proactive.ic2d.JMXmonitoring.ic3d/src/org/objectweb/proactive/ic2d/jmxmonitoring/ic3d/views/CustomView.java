package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PointLight;
import javax.media.j3d.SpotLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.CameraBehavior;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.FlatCameraBehavior;

import com.sun.j3d.utils.geometry.Sphere;
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

        /* Set up camera transform */
        cameraTransform = new TransformGroup();
        cameraTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        cameraTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        viewBranch.addChild(cameraTransform);
        
        /* Set up light */
        TransformGroup lightTransform = new TransformGroup();
        Transform3D light3D = new Transform3D();
        light3D.setTranslation(new Vector3d(-50, 0, -5));
        lightTransform.setTransform(light3D);
        PointLight pointLight = new PointLight();
        pointLight.setColor(new Color3f(0.5f, 0.5f, 0.5f));
        pointLight.setAttenuation(new Point3f(0.5f, 0.01f, 0f));
        pointLight.setInfluencingBounds(new BoundingSphere(new Point3d(), 225));
        lightTransform.addChild(pointLight);
        cameraTransform.addChild(lightTransform);
        
        /* Prepare the view platform */
        ViewPlatform viewPlatform = new ViewPlatform();
        cameraTransform.addChild(viewPlatform);

        View view = new View();
        view.addCanvas3D(canvas3D);
        view.attachViewPlatform(viewPlatform);
        view.setBackClipDistance(50);
        view.setPhysicalBody(CustomView.physicalBody);
        view.setPhysicalEnvironment(CustomView.physicalEnvironment);
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
    
    public void setCamera(CameraBehavior cameraToSet, BranchGroup pickableGroup, Point3d target) {
        /* Destroy the old camera */
        if (camera != null)
            viewBranch.removeChild(camera.getBranchGroup());
        
        /* Set the local camera scene */
        cameraToSet.set(canvas3D, cameraTransform, pickableGroup);
        camera = cameraToSet;
        camera.setTarget(target);
        viewBranch.addChild(camera.getBranchGroup());
    }

	public void setLight(Point3d target) {
		BranchGroup lightBranch = new BranchGroup();
		AmbientLight ambient = new AmbientLight(new Color3f(0f, 1f, 0f));
        ambient.setInfluencingBounds(new BoundingSphere(target, 150));
        lightBranch.addChild(ambient);
        viewBranch.addChild(lightBranch);
	}
}
