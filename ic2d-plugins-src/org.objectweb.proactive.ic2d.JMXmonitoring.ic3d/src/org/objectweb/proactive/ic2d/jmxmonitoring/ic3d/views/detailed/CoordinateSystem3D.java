package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;


public class CoordinateSystem3D extends BranchGroup {
    // field to scale the size of the object on the axes
    public CoordinateSystem3D(final float scale) {
        // create and add origin
        final Sphere origin = new Sphere(scale);
        origin.setCapability(Primitive.GENERATE_NORMALS);
        // create and add the axes
        this.addChild(origin);
        Box box;
        Cone cone;
        Cylinder cyl;
        Transform3D translate;
        TransformGroup trans;

        for (int i = 3; i < 500; i = i + 3) {
            // x axis
            // positive
            box = new Box(scale, scale, scale, null);
            translate = new Transform3D();
            translate.setTranslation(new Vector3d(i * scale, 0, 0));
            trans = new TransformGroup(translate);
            trans.addChild(box);
            this.addChild(trans);
            // negative
            box = new Box(scale, scale, scale, null);
            translate = new Transform3D();
            translate.setTranslation(new Vector3d(-i * scale, 0, 0));
            trans = new TransformGroup(translate);
            trans.addChild(box);
            this.addChild(trans);
            // y axis
            // positive
            cone = new Cone(scale / 2, scale);
            translate = new Transform3D();
            translate.setTranslation(new Vector3d(0, i * scale, 0));
            trans = new TransformGroup(translate);
            trans.addChild(cone);
            this.addChild(trans);
            // negative
            cone = new Cone(scale / 2, scale);
            translate = new Transform3D();
            translate.setTranslation(new Vector3d(0, -i * scale, 0));
            trans = new TransformGroup(translate);
            trans.addChild(cone);
            this.addChild(trans);
            // z axis
            cyl = new Cylinder(scale / 2, scale);
            translate = new Transform3D();
            translate.setTranslation(new Vector3d(0, 0, i * scale));
            trans = new TransformGroup(translate);
            trans.addChild(cyl);
            this.addChild(trans);
            // negative
            cyl = new Cylinder(scale / 2, scale);
            translate = new Transform3D();
            translate.setTranslation(new Vector3d(0, 0, -i * scale));
            trans = new TransformGroup(translate);
            trans.addChild(cyl);
            this.addChild(trans);
        }
        // green light
        this.compile();
    }
}
