/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LinearFog;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.pickfast.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.universe.ConfiguredUniverse;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewerAvatar;
import com.sun.j3d.utils.universe.ViewingPlatform;


/**
 * @author vjuresch
 *
 */
public class GridUniverse {
    private ConfiguredUniverse universe = new ConfiguredUniverse();

    public GridUniverse() {
        universe.setVisible(false);
    }

    // creates a new view
    // the view is accessed through the canvas returned
    public Canvas3D newView() {
        // create viewer and platform
        Viewer viewer = new Viewer(new Canvas3D(
                    SimpleUniverse.getPreferredConfiguration()));
        ViewingPlatform vp = new ViewingPlatform();
        // set the universe of the platform
        vp.setUniverse(universe);
        // set the platform for the viewer
        viewer.setViewingPlatform(vp);
        // add a new branch containing the view group
        universe.getLocale().addBranchGraph(vp);

        // set the behaviour
        OrbitBehavior orbit = new OrbitBehavior(viewer.getCanvas3D());
        // set bounds
        orbit.setSchedulingBounds(new BoundingSphere());
        // inverse mouse is more natural
        orbit.setReverseRotate(true);
        // set the view platform for the orbit
        vp.setViewPlatformBehavior(orbit);
        // move back a bit
        setUpView(vp.getViewPlatformTransform());

        // //set the geometry to use (avatar-interface ? )
        // vp.setPlatformGeometry(new PlatformGeometry());
        return viewer.getCanvas3D();
    }

    // adds the grid to the root branch group
    public void addGrid(Grid3D grid) {
        BranchGroup grp = createObjectBranch();
        grp.addChild(grid.getRootBranch());
        universe.getLocale().addBranchGraph(grp);
    }

    public BranchGroup createObjectBranch() {
        BranchGroup objGrp = new BranchGroup();
        objGrp.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        objGrp.setCapability(BranchGroup.ALLOW_DETACH);
        objGrp.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        objGrp.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        objGrp.setCapability(BranchGroup.ALLOW_PARENT_READ);
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
        transObj.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        objGrp.addChild(transObj);

        // add a wireframe cube
        ColorCube back = new ColorCube(7);
        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        polygonAttributes.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        Appearance backStyle = new Appearance();

        backStyle.setPolygonAttributes(polygonAttributes);
        backStyle.setColoringAttributes(new ColoringAttributes(
                ColorPalette.BLACK, 1));
        back.setAppearance(backStyle);
        transObj.addChild(back);

        Vector3f dirVec = new Vector3f(0f, -0.5f, -1f);
        DirectionalLight dirLight = new DirectionalLight(ColorPalette.ORANGE,
                dirVec);

        BoundingSphere lightBounds = new BoundingSphere(new Point3d(0, 0, 0),
                1000);
        AmbientLight light = new AmbientLight(new Color3f(0.4f, 0.4f, 0.4f));
        light.setInfluencingBounds(lightBounds);
        dirLight.setInfluencingBounds(lightBounds);

        transObj.addChild(dirLight);

        // TODO make class
        // HoverInfo tooltip = new HoverInfo(universe.getCanvas(),objGrp, new
        // BoundingSphere(new Point3d(0,0,0),10000));
        // objGrp.addChild(tooltip);

        // TODO make class
        // PickTranslateBehavior pickRotate = new
        // PickTranslateBehavior(objGrp,universe.getCanvas(), new
        // BoundingSphere());
        // pickRotate.setSchedulingBounds(new BoundingSphere());
        // pickRotate.setBounds(new BoundingSphere());
        // objGrp.addChild(pickRotate);
        // PickTranslateBehavior pickTranslate = new
        // PickTranslateBehavior(objGrp,canvas3D, new BoundingSphere());
        // objGrp.addChild(pickTranslate);
        // // PickZoomBehavior pickZoom = new PickZoomBehavior(objGrp,canvas3D,
        // new BoundingSphere());
        // objGrp.addChild(pickZoom);

        // add the axes
        // CoordinateSystem3D coord = new CoordinateSystem3D(0.07f);
        // objGrp.addChild(coord);
        // grid -> rotTG -> generalTG -> BG -> *BG*
        objGrp.compile();
        return objGrp;
    }

    private void setUpView(TransformGroup trans) {
        Transform3D setUp = new Transform3D();
        setUp.rotX(Math.toRadians(50));

        setUp.setTranslation(new Vector3d(1, -10, 10));

        trans.setTransform(setUp);
    }
}
