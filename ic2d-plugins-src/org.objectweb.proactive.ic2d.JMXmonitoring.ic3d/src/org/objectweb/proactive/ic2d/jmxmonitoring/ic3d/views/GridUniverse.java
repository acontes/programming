/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.LinearFog;
import javax.media.j3d.Node;
import javax.media.j3d.RotPosPathInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.universe.ConfiguredUniverse;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;


/**
 * This class is responsible with creating the universe and the views. The
 * sequence for displaying a universe is:
 * <ol>
 * <li> create a GridUniverse object </li>
 * <li> create a new grid figure and add it using addGrid(..)</li>
 * <li> get a Canvas3D using newView(..) and display it in a Frame </li>
 * </ol>
 * 
 * Trying to display the same Canvas3D in several Frames does not seem to work.
 * 
 * @author vasile
 * 
 */
public class GridUniverse {
    private ConfiguredUniverse universe = new ConfiguredUniverse();
    private BranchGroup objGrp;

    public GridUniverse() {
        // TODO maybe remove the default view branch since it is unused
        universe.setVisible(false);// makes the default view invisible
    }

    /**
     * Creates a new view branch used for view the scene from a different point
     * a view.
     * 
     * @return a Canvas3D view that can be used for displaying the universe
     */
    public Canvas3D newView() {
        return newView(new Point3d(), new Vector3d(1, -10, 10));
    }

    public Canvas3D newView(Point3d myCenter, Vector3d viewPoint) {
        // create viewer and platform
        Viewer viewer = new Viewer(new Canvas3D(SimpleUniverse.getPreferredConfiguration()));
        ViewingPlatform vp = new ViewingPlatform();

        // set the universe of the platform
        vp.setUniverse(universe);
        // set the platform for the viewer
        viewer.setViewingPlatform(vp);

        // add a new branch containing the view group
        universe.getLocale().addBranchGraph(vp);

        // \todo change this part to plug camera in
        
        // set the default behaviour
        OrbitBehavior orbit = new OrbitBehavior(viewer.getCanvas3D());
        // set bounds
        orbit.setSchedulingBounds(new BoundingSphere());
        // inverse mouse is more natural
        orbit.setReverseRotate(true);
        // set the orbit center 
        orbit.setRotationCenter(myCenter);
        // set the view platform for the orbit
        vp.setViewPlatformBehavior(orbit);

        // move back a bit
        setUpView(vp.getViewPlatformTransform(), viewPoint);

        // add a Behaviour to the viewplatform transform
        // so we can start an automatic rotation
        // setUpAutomaticOrbit(vp.getViewPlatformTransform());

        // //set the geometry to use (avatar-interface ? )
        // vp.setPlatformGeometry(new PlatformGeometry());

        viewer.getCanvas3D().getView().setBackClipDistance(200);

        //add interaction for this view
        BranchGroup bpick = new BranchGroup();
        bpick.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
        //TODO make class
        PickTranslateBehavior pickRotate = new PickTranslateBehavior(bpick, viewer.getCanvas3D(),
            new BoundingSphere());
        pickRotate.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));

        bpick.addChild(pickRotate);
        objGrp.addChild(bpick);
        //		 com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior
        //		 pickTranslate = new
        //		 com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior(objGrp,viewer.getCanvas3D(),
        //		 new BoundingSphere());
        //		 pickTranslate.setSchedulingBounds(new BoundingSphere());
        //		 objGrp.addChild(pickTranslate);
        // PickZoomBehavior pickZoom = new PickZoomBehavior(objGrp,canvas3D,
        //new BoundingSphere());
        // objGrp.addChild(pickZoom);

     //   viewer.getCanvas3D().getView().setSceneAntialiasingEnable(true);

        return viewer.getCanvas3D();
    }

    private Quat4f eulerToQuat(double x, double y, double z, double angle) {
        /*
         * since quaternions are impossible to visualize euler to quaternion
         * transform is
         * 
         * q = [sin(angle/2)*axis, cos(angle/2)]
         */
        return new Quat4f((float) (Math.sin(angle / 2) * x), (float) (Math.sin(angle / 2) * y), (float) (Math
                .sin(angle / 2) * z), (float) Math.cos(angle / 2));
    }

    /**
     * Rotates the view platform around the universe origin
     * 
     * @param viewPlatformTransform
     */
    private void setUpAutomaticOrbit(TransformGroup viewPlatformTransform) {
        /*
         * alpha - the alpha object for this interpolator target - the
         * TransformGroup node affected by this translator axisOfTransform - the
         * transform that defines the local coordinate system in which this
         * interpolator operates knots - an array of knot values that specify
         * interpolation points. quats - an array of quaternion values at the
         * knots. positions - an array of position values at the knots.
         */
        final int DISTANCE = 10;
        Alpha alpha = new Alpha(-1, 30000);
        Transform3D axisOfTransform = new Transform3D();
        float[] knots = new float[] { 0f, .25f, .50f, .75f, 1f }; // for
        // equally
        // distanced
        // knots
        /*
         * since quaternions are impossible to visualize euler to quaternion
         * transform is
         * 
         * q = [sin(angle/2)*axis, cos(angle/2)]
         * 
         * In the following we need the view to be oriented towards the center
         * of the universe.
         */
        double angle = 0;
        Quat4f[] quats = new Quat4f[] { eulerToQuat(0, DISTANCE, -DISTANCE, Math.PI / 100),
                eulerToQuat(-DISTANCE, 0, -DISTANCE, Math.PI / 100),
                eulerToQuat(0, -DISTANCE, -DISTANCE, Math.PI / 100),
                eulerToQuat(DISTANCE, 0, -DISTANCE, -Math.PI / 100),
                eulerToQuat(0, DISTANCE, -DISTANCE, Math.PI / 100) }; // axes
        // and
        // amount
        // of
        // rotation

        Point3f[] positions = new Point3f[] { new Point3f(0, -DISTANCE, DISTANCE),
                new Point3f(DISTANCE, 0, DISTANCE), new Point3f(0, DISTANCE, DISTANCE),
                new Point3f(-DISTANCE, 0, DISTANCE), new Point3f(0, -DISTANCE, DISTANCE) }; // position in space

        RotPosPathInterpolator interp = new RotPosPathInterpolator(alpha, viewPlatformTransform,
            axisOfTransform, knots, quats, positions);
        interp.setSchedulingBounds(new BoundingSphere());
        BranchGroup bg = new BranchGroup();
        bg.addChild(interp);

        viewPlatformTransform.addChild(bg);

    }

    /**
     * This method adds a grid figure to the locale of the universe.
     * 
     * @param grid
     *            the grid figure to be added to the universe
     */
    // TODO add the posibilities to add grids at different locales
    public void addGrid(AbstractGrid3D grid) {
        BranchGroup grp = createObjectBranch();
        grp.addChild(grid.getRootBranch());
        universe.getLocale().addBranchGraph(grp);
    }

    /**
     * This method is used by the addGrid method to create a BranchGroup to
     * which to connect the grid figure.
     * 
     * @return an BranchGroup to which a grid is connected
     */
    private BranchGroup createObjectBranch() {
        objGrp = new BranchGroup();
        objGrp.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        objGrp.setCapability(BranchGroup.ALLOW_DETACH);
        objGrp.setCapability(Group.ALLOW_CHILDREN_READ);
        objGrp.setCapability(Group.ALLOW_CHILDREN_WRITE);
        objGrp.setCapability(Node.ALLOW_PARENT_READ);
        objGrp.setCapability(Node.ENABLE_PICK_REPORTING);
        // create background
        Background bkgrd = new Background();
        bkgrd.setColor(ColorPalette.WHITE);
        bkgrd.setApplicationBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));

        objGrp.addChild(bkgrd);
        // set fog
        LinearFog fog = new LinearFog();
        fog.setColor(ColorPalette.WHITE);
        fog.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));
        fog.setFrontDistance(25f);
        fog.setBackDistance(45f);
        objGrp.addChild(fog);

        TransformGroup transObj = new TransformGroup();
        transObj.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transObj.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transObj.setCapability(Node.ENABLE_PICK_REPORTING);
        objGrp.addChild(transObj);

        // //add a wireframe cube
        // ColorCube back = new ColorCube(7);
        // PolygonAttributes polygonAttributes = new PolygonAttributes();
        // polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        // polygonAttributes.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        // Appearance backStyle = new Appearance();

        // backStyle.setPolygonAttributes(polygonAttributes);
        // backStyle.setColoringAttributes(new ColoringAttributes(
        // ColorPalette.BLACK, 1));
        // back.setAppearance(backStyle);
        // transObj.addChild(back);

        Vector3f dirVec = new Vector3f(0f, -0.5f, -1f);
        DirectionalLight dirLight = new DirectionalLight(ColorPalette.ORANGE, dirVec);

        BoundingSphere lightBounds = new BoundingSphere(new Point3d(0, 0, 0), 1000);
        AmbientLight light = new AmbientLight(new Color3f(0.4f, 0.4f, 0.4f));
        light.setInfluencingBounds(lightBounds);
        dirLight.setInfluencingBounds(lightBounds);

        transObj.addChild(dirLight);

        // //TODO make class
        // HoverInfo tooltip = new HoverInfo(universe.getCanvas(),objGrp, new
        // BoundingSphere(new Point3d(0,0,0),10000));
        // objGrp.addChild(tooltip);
        // add the axes
        /*CoordinateSystem3D coord = new CoordinateSystem3D(0.07f);
        TransformGroup coordTrans = new TransformGroup();
         coordTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         coordTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
         coordTrans.addChild(coord);
         setUpAutomaticOrbit(coordTrans);
         objGrp.addChild(coordTrans);*/
        CoordinateSystem3D coord1 = new CoordinateSystem3D(0.07f);
        objGrp.addChild(coord1);
        objGrp.compile();
        return objGrp;
    }

    /**
     * Utility method for moving the view platform so the figures are visible
     * 
     * @param trans
     *            the view platform's TransformGroup
     */
    private void setUpView(TransformGroup trans, Vector3d viewPoint) {
        Transform3D setUp = new Transform3D();
        setUp.rotX(Math.toRadians(50));

        setUp.setTranslation(viewPoint);
        //setUp.setTranslation(new Vector3d(1, -10, 10));

        trans.setTransform(setUp);
    }
}
