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
	private final ConfiguredUniverse universe = new ConfiguredUniverse();
	private BranchGroup objGrp;

	public GridUniverse() {
		// TODO maybe remove the default view branch since it is unused
		this.universe.setVisible(false);// makes the default view invisible
	}

	/**
	 * Creates a new view branch used for view the scene from a different point
	 * a view.
	 * 
	 * @return a Canvas3D view that can be used for displaying the universe
	 */
	public Canvas3D newView() {
		return this.newView(new Point3d(), new Vector3d(1, -10, 10));
	}

	public Canvas3D newView(final Point3d myCenter, final Vector3d viewPoint) {
		// create viewer and platform
		final Viewer viewer = new Viewer(new Canvas3D(SimpleUniverse
				.getPreferredConfiguration()));
		final ViewingPlatform vp = new ViewingPlatform();

		// set the universe of the platform
		vp.setUniverse(this.universe);
		// set the platform for the viewer
		viewer.setViewingPlatform(vp);

		// add a new branch containing the view group
		this.universe.getLocale().addBranchGraph(vp);

		// \todo change this part to plug camera in

		// set the default behaviour
		final OrbitBehavior orbit = new OrbitBehavior(viewer.getCanvas3D());
		// set bounds
		orbit.setSchedulingBounds(new BoundingSphere());
		// inverse mouse is more natural
		orbit.setReverseRotate(true);
		// set the orbit center
		orbit.setRotationCenter(myCenter);
		// set the view platform for the orbit
		vp.setViewPlatformBehavior(orbit);

		// move back a bit
		this.setUpView(vp.getViewPlatformTransform(), viewPoint);

		// add a Behaviour to the viewplatform transform
		// so we can start an automatic rotation
		// setUpAutomaticOrbit(vp.getViewPlatformTransform());

		// //set the geometry to use (avatar-interface ? )
		// vp.setPlatformGeometry(new PlatformGeometry());

		viewer.getCanvas3D().getView().setBackClipDistance(200);

		// add interaction for this view
		final BranchGroup bpick = new BranchGroup();
		bpick.setCapability(Node.ENABLE_PICK_REPORTING);
		// TODO make class
		final PickTranslateBehavior pickRotate = new PickTranslateBehavior(
				bpick, viewer.getCanvas3D(), new BoundingSphere());
		pickRotate.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0),
				1000));

		bpick.addChild(pickRotate);
		this.objGrp.addChild(bpick);
		// com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior
		// pickTranslate = new
		// com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior(objGrp,viewer.getCanvas3D(),
		// new BoundingSphere());
		// pickTranslate.setSchedulingBounds(new BoundingSphere());
		// objGrp.addChild(pickTranslate);
		// PickZoomBehavior pickZoom = new PickZoomBehavior(objGrp,canvas3D,
		// new BoundingSphere());
		// objGrp.addChild(pickZoom);

		// viewer.getCanvas3D().getView().setSceneAntialiasingEnable(true);

		return viewer.getCanvas3D();
	}

	private Quat4f eulerToQuat(final double x, final double y, final double z,
			final double angle) {
		/*
		 * since quaternions are impossible to visualize euler to quaternion
		 * transform is
		 * 
		 * q = [sin(angle/2)*axis, cos(angle/2)]
		 */
		return new Quat4f((float) (Math.sin(angle / 2) * x), (float) (Math
				.sin(angle / 2) * y), (float) (Math.sin(angle / 2) * z),
				(float) Math.cos(angle / 2));
	}

	/**
	 * Rotates the view platform around the universe origin
	 * 
	 * @param viewPlatformTransform
	 */
	private void setUpAutomaticOrbit(final TransformGroup viewPlatformTransform) {
		/*
		 * alpha - the alpha object for this interpolator target - the
		 * TransformGroup node affected by this translator axisOfTransform - the
		 * transform that defines the local coordinate system in which this
		 * interpolator operates knots - an array of knot values that specify
		 * interpolation points. quats - an array of quaternion values at the
		 * knots. positions - an array of position values at the knots.
		 */
		final int DISTANCE = 10;
		final Alpha alpha = new Alpha(-1, 30000);
		final Transform3D axisOfTransform = new Transform3D();
		final float[] knots = new float[] { 0f, .25f, .50f, .75f, 1f }; // for
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
		final double angle = 0;
		final Quat4f[] quats = new Quat4f[] {
				this.eulerToQuat(0, DISTANCE, -DISTANCE, Math.PI / 100),
				this.eulerToQuat(-DISTANCE, 0, -DISTANCE, Math.PI / 100),
				this.eulerToQuat(0, -DISTANCE, -DISTANCE, Math.PI / 100),
				this.eulerToQuat(DISTANCE, 0, -DISTANCE, -Math.PI / 100),
				this.eulerToQuat(0, DISTANCE, -DISTANCE, Math.PI / 100) }; // axes
		// and
		// amount
		// of
		// rotation

		final Point3f[] positions = new Point3f[] {
				new Point3f(0, -DISTANCE, DISTANCE),
				new Point3f(DISTANCE, 0, DISTANCE),
				new Point3f(0, DISTANCE, DISTANCE),
				new Point3f(-DISTANCE, 0, DISTANCE),
				new Point3f(0, -DISTANCE, DISTANCE) }; // position in space

		final RotPosPathInterpolator interp = new RotPosPathInterpolator(alpha,
				viewPlatformTransform, axisOfTransform, knots, quats, positions);
		interp.setSchedulingBounds(new BoundingSphere());
		final BranchGroup bg = new BranchGroup();
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
	public void addGrid(final AbstractGrid3D grid) {
		final BranchGroup grp = this.createObjectBranch();
		grp.addChild(grid.getRootBranch());
		this.universe.getLocale().addBranchGraph(grp);
	}

	/**
	 * This method is used by the addGrid method to create a BranchGroup to
	 * which to connect the grid figure.
	 * 
	 * @return an BranchGroup to which a grid is connected
	 */
	private BranchGroup createObjectBranch() {
		this.objGrp = new BranchGroup();
		this.objGrp.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		this.objGrp.setCapability(BranchGroup.ALLOW_DETACH);
		this.objGrp.setCapability(Group.ALLOW_CHILDREN_READ);
		this.objGrp.setCapability(Group.ALLOW_CHILDREN_WRITE);
		this.objGrp.setCapability(Node.ALLOW_PARENT_READ);
		this.objGrp.setCapability(Node.ENABLE_PICK_REPORTING);
		// create background
		final Background bkgrd = new Background();
		bkgrd.setColor(ColorPalette.WHITE);
		bkgrd.setApplicationBounds(new BoundingSphere(new Point3d(0, 0, 0),
				1000));

		this.objGrp.addChild(bkgrd);
		// set fog
		final LinearFog fog = new LinearFog();
		fog.setColor(ColorPalette.WHITE);
		fog
				.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0),
						1000));
		fog.setFrontDistance(25f);
		fog.setBackDistance(45f);
		this.objGrp.addChild(fog);

		final TransformGroup transObj = new TransformGroup();
		transObj.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transObj.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		transObj.setCapability(Node.ENABLE_PICK_REPORTING);
		this.objGrp.addChild(transObj);

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

		final Vector3f dirVec = new Vector3f(0f, -0.5f, -1f);
		final DirectionalLight dirLight = new DirectionalLight(
				ColorPalette.ORANGE, dirVec);

		final BoundingSphere lightBounds = new BoundingSphere(new Point3d(0, 0,
				0), 1000);
		final AmbientLight light = new AmbientLight(new Color3f(0.4f, 0.4f,
				0.4f));
		light.setInfluencingBounds(lightBounds);
		dirLight.setInfluencingBounds(lightBounds);

		transObj.addChild(dirLight);

		// //TODO make class
		// HoverInfo tooltip = new HoverInfo(universe.getCanvas(),objGrp, new
		// BoundingSphere(new Point3d(0,0,0),10000));
		// objGrp.addChild(tooltip);
		// add the axes
		/*
		 * CoordinateSystem3D coord = new CoordinateSystem3D(0.07f);
		 * TransformGroup coordTrans = new TransformGroup();
		 * coordTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		 * coordTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		 * coordTrans.addChild(coord); setUpAutomaticOrbit(coordTrans);
		 * objGrp.addChild(coordTrans);
		 */
		final CoordinateSystem3D coord1 = new CoordinateSystem3D(0.07f);
		this.objGrp.addChild(coord1);
		this.objGrp.compile();
		return this.objGrp;
	}

	/**
	 * Utility method for moving the view platform so the figures are visible
	 * 
	 * @param trans
	 *            the view platform's TransformGroup
	 */
	private void setUpView(final TransformGroup trans, final Vector3d viewPoint) {
		final Transform3D setUp = new Transform3D();
		setUp.rotX(Math.toRadians(50));

		setUp.setTranslation(viewPoint);
		// setUp.setTranslation(new Vector3d(1, -10, 10));

		trans.setTransform(setUp);
	}
}
