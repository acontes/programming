package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.util.HashMap;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Locale;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.ColorPalette;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.CameraBehavior;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.FlatCameraBehavior;


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
//        TransformGroup mapTG = new TransformGroup();
//        Transform3D mapT = new Transform3D();
//        mapT.setTranslation(new Vector3d(0d, -0.05d, 0d));
//        mapTG.setTransform(mapT);
        int xmax = GeometryBasket.TILE_X;
        int ymax = GeometryBasket.TILE_Y;
        //int xmax = 4;
        //int ymax = 2;
      
        for(int y = 0; y < ymax; y++) {
        	double yoffset = ((double)y - (double)ymax/2) * (double)GeometryBasket.MAP_TILE + (double)GeometryBasket.MAP_TILE / 2d;
        	for(int x = 0; x < xmax; x++) {
        		TransformGroup mapTG = new TransformGroup();
                Transform3D mapT = new Transform3D();
                Vector3d trans = new Vector3d(
                		((double)x - (double)xmax/2) * (double)GeometryBasket.MAP_TILE + (double)GeometryBasket.MAP_TILE / 2,
                		-0.05d,
                		yoffset
						);
                mapT.setTranslation(trans);
                mapTG.setTransform(mapT);
                Shape3D map = new Shape3D(GeometryBasket.getSquare());
                map.setAppearance(AppearanceBasket.map(y * xmax + x));
                mapTG.addChild(map);
                scene.addChild(mapTG);
        	}
        }
//        Shape3D map = new Shape3D(GeometryBasket.getFlatMap());
//        map.setAppearance(AppearanceBasket.flatMap);
//        mapTG.addChild(map);
//        scene.addChild(mapTG);

        // create background
        Background bkgrd = new Background();
        bkgrd.setColor(ColorPalette.BLACK);
        bkgrd.setApplicationBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));

        scene.addChild(bkgrd);

        scene.compile();
        locale.addBranchGraph(scene);
        views = new HashMap<String, CustomView>();
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
        customView.setCamera(camera, pickableScene, target);
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
