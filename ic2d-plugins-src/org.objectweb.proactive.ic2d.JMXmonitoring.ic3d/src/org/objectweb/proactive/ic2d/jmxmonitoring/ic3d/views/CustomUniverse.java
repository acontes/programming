package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.util.HashMap;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Locale;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Point3d;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.CameraBehavior;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.FlatCameraBehavior;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.ColorPalette;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;


public class CustomUniverse {
    /* Universe itself */
    private VirtualUniverse universe;
    /* Main locale */
    private Locale locale;
    /* Main universe scene */
    private BranchGroup scene;

    /* Map of views */
    private HashMap<String, CustomView> views;

    public CustomUniverse() {
        universe = new VirtualUniverse();
        locale = new Locale(universe);
        scene = new BranchGroup();
        scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        TransformGroup mapTG = new TransformGroup();
        Shape3D map = new Shape3D(GeometryBasket.getFlatMap());
        map.setAppearance(AppearanceBasket.flatMap);
        mapTG.addChild(map);
        scene.addChild(mapTG);

        // create background
        Background bkgrd = new Background();
        bkgrd.setColor(ColorPalette.BLACK);
        bkgrd.setApplicationBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));

        scene.addChild(bkgrd);

        scene.compile();
        locale.addBranchGraph(scene);
        views = new HashMap<String, CustomView>();
    }

    private void createUniverse() {
        /* Create the universe */
        universe = new VirtualUniverse();
        locale = new Locale(universe);
    }

    public Canvas3D newView(String name) {
        return newView(name, new Point3d(), scene);
    }

    public Canvas3D newView(String name, Point3d target, BranchGroup pickableScene) {
        FlatCameraBehavior FCB = new FlatCameraBehavior();
        return newView(name, target, pickableScene, FCB);
    }

    public Canvas3D newView(String name, Point3d target, BranchGroup pickableScene, CameraBehavior camera) {
        CustomView customView = new CustomView();
        views.put(name, customView);
        locale.addBranchGraph(customView.getView());
        customView.setCamera(camera, pickableScene);
        customView.getCamera().setTarget(target);
        return customView.getCanvas3D();
    }

    public void addGrid(BranchGroup branchGroup) {
        scene.addChild(branchGroup);
    }

    public void addGrid(Figure3D figure) {
        scene.addChild(figure.getRootBranch());
    }

    public CustomView getView(String name) {
        return (views.get(name));
    }
}
