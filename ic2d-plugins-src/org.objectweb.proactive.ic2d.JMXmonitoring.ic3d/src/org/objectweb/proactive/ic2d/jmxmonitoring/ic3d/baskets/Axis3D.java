package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;


public class Axis3D extends BranchGroup {
    public Axis3D() {
        TransformGroup transformGroup = new TransformGroup();
        transformGroup.addChild(axis(10));
        this.addChild(transformGroup);
        this.compile();
    }

    public Axis3D(int size) {
        TransformGroup transformGroup = new TransformGroup();
        transformGroup.addChild(axis(size));
        this.addChild(transformGroup);
        this.compile();
    }

    private Shape3D axis(float i) {
        LineArray axis3D = new LineArray(6, LineArray.COLOR_3 | LineArray.COORDINATES);
        axis3D.setCoordinate(0, new Point3f(-i, 0f, 0f));
        axis3D.setCoordinate(1, new Point3f(i, 0f, 0f));
        axis3D.setCoordinate(2, new Point3f(0f, -i, 0f));
        axis3D.setCoordinate(3, new Point3f(0f, i, 0f));
        axis3D.setCoordinate(4, new Point3f(0f, 0f, -i));
        axis3D.setCoordinate(5, new Point3f(0f, 0f, i));
        axis3D.setColor(0, new Color3f(1f, 0f, 0f));
        axis3D.setColor(1, new Color3f(1f, 0f, 0f));
        axis3D.setColor(2, new Color3f(0f, 1f, 0f));
        axis3D.setColor(3, new Color3f(0f, 1f, 0f));
        axis3D.setColor(4, new Color3f(0f, 0f, 1f));
        axis3D.setColor(5, new Color3f(0f, 0f, 1f));
        return new Shape3D(axis3D);
    }

    /*private Shape3D cube(Color3f color, float length) {
    	QuadArray geometry = new QuadArray(24, QuadArray.COLOR_3| QuadArray.COORDINATES);
    	length /= 2f;
    	Point3f A = new Point3f(-length, -length, -length);
    	Point3f B = new Point3f(-length, -length,  length);
    	Point3f C = new Point3f( length, -length,  length);
    	Point3f D = new Point3f( length, -length, -length);
    	Point3f E = new Point3f(-length,  length, -length);
    	Point3f F = new Point3f(-length,  length,  length);
    	Point3f G = new Point3f( length,  length,  length);
    	Point3f H = new Point3f( length,  length, -length);
    	Point3f[] cube = {
    			E, F, G, H,
    			F, B, C, G,
    			G, C, D, H,
    			H, D, A, E,
    			E, A, B, F,
    			B, A, D, C
    	};
    	for ( int i = 0; i < 24; i++)
    		geometry.setColor(i, color);
    	geometry.setCoordinates(0, cube);
    	return new Shape3D(geometry);
    }*/
}
