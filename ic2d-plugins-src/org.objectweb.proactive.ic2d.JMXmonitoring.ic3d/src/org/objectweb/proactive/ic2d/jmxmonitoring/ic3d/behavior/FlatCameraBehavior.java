package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior;

import java.awt.PopupMenu;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.ActiveObject3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Host3D;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.picking.PickResult;


public class FlatCameraBehavior extends CameraBehavior {
    protected final static Vector3d up = new Vector3d(0, 1, 0);

    private double phi;
    private double theta;
    private double distance;

    public FlatCameraBehavior() {
        super();
        phi = 0;
        theta = Math.PI / 4;
        distance = 8;
    }

    /*
     * Focus on the selected shape
     * \todo zoom to make the shape fit to the screen
     */
    @Override
    protected void mouse1DoubleClick() {
        /* Left double click > Focus on selected shape */
        if (selectedShape != null) {
            Transform3D location = new Transform3D();
            selectedShape.getLocalToVworld(location);
            Vector3f shapePosition = new Vector3f();
            location.get(shapePosition);
            targetPosition.set(shapePosition);
            refresh();
        }
    }

    @Override
    protected void mouse1Dragged() {
    	/* The only objects you can drag are activeObjects */
        /* On a figure */
        if ( selectedShape != null && selectedShape instanceof ActiveObject3D)
        	dragSelected(x - x_last, y_last - y);
        /* Outside a figure */
        else
        	cameraRotation(x - x_last, y - y_last);
    }

    @Override
    protected void mouse1Pressed() {
        /* Left click -> Selects a shape */
        PickResult pickResult = null;
        if (pickCanvas == null) {
        	throw new NullPointerException("Pick Canvas not initialized");
        }
        pickCanvas.setShapeLocation(x, y);
        pickResult = pickCanvas.pickClosest();
        if (pickResult == null)
            selectedShape = null;
        else
            selectedShape = ((Shape3D) pickResult.getNode(PickResult.SHAPE3D));
    }

    @Override
    protected void mouse1Released() {
        if (selectedShape != null && selectedShapeTranslation != null) {
            TransformGroup tg = (TransformGroup) selectedShape.getParent();
            Transform3D t3d = new Transform3D();
            tg.getTransform(t3d);
            t3d.setTranslation(selectedShapeTranslation);
            tg.setTransform(t3d);
            selectedShapeTranslation = null;
        }
    }

    @Override
    protected void mouse2DoubleClick() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void mouse2Dragged() {
        /* Move on the x-z plane ( mouse 2 ) */
        double x_diff, y_diff;
        x_diff = (double) (x - x_last) / 960d * distance;
        y_diff = (double) (y_last - y) / 960d * distance;
        targetPosition.z += Math.cos(phi) * y_diff + Math.sin(phi) * x_diff;
        targetPosition.x += Math.sin(phi) * y_diff - Math.cos(phi) * x_diff;
        refresh();
    }

    @Override
    protected void mouse2Pressed() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void mouse2Released() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void mouse3DoubleClick() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void mouse3Dragged() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void mouse3Pressed() {
        /* Right click > Pop up context menu */
        mouse1Pressed();
        PopupMenu pop;

        /* We need a context menu */
        if (selectedShape != null) {
            /* Checks the type of the figure */
            //if( selectedShape instanceof FractalKoch3D )
            //	pop = pops[2];
            /*else */if (selectedShape instanceof ColorCube)
                pop = pops[1];
            else if(selectedShape instanceof Host3D)
            	pop = pops[2];
            /* Shan't happen all non listed figures should be marked as not pickable */
            else
                pop = pops[3];
        }
        /* Default menu */
        else
            pop = pops[0];
        pop.show(canvas3D, x, y);
    }

    @Override
    protected void mouse3Released() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void mouseWheel(int amount, int direction) {
        distance += (double) (amount * direction) / 6d;
        if (distance < 1.5)
            distance = 1.5;
        else if (distance > 200)
            distance = 200;
        refresh();
    }

    private void dragSelected(float x_diff, float y_diff) {
        /* \\todo if camera in figure bounds cancel displace -> rotate */
        TransformGroup tg = (TransformGroup) selectedShape.getParent();
        Transform3D transform = new Transform3D();
        Vector3f translation = new Vector3f();

        tg.getTransform(transform);
        transform.get(translation);

        if (selectedShapeTranslation == null) {
            selectedShapeTranslation = new Vector3f();
            selectedShapeTranslation.set(translation);
            //translation.y += 0.5f;
        }
        
        // TODO move on the 2D Plate
        translation.x += (Math.cos(phi) * x_diff + Math.sin(phi) * y_diff) / 10f;
        translation.z += (Math.sin(phi) * x_diff - Math.cos(phi) * y_diff) / 10f;

        transform.set(translation);
        tg.setTransform(transform);
    }

    /*
     * Applies a camera rotation
     */
    private void cameraRotation(int x_rot, int y_rot) {
        /* Refresh parameter */
        phi += (double) x_rot / 120d;
        phi %= Math.PI * 2;
        theta += (double) y_rot / 120d;
        if (theta < Math.PI / 12)
            theta = Math.PI / 12;
        else if (theta > 2 * Math.PI / 5)
            theta = 2 * Math.PI / 5;
        refresh();
    }

    @Override
    public void setTarget(Point3d target) {
        this.targetPosition = target;
        refresh();
    }

    /*
     * Generic method used each time a parameter of the camera has moved
     * Shouldn't appear in the call graph more than once per mouse event
     */
    @Override
    protected void refresh() {
        double x, y, z;
        Point3d position;

        /* Compute the coordinates */
        x = Math.sin(phi) * Math.sin(theta) * distance;
        z = Math.cos(phi) * Math.sin(theta) * distance;
        y = Math.cos(theta) * distance;
        position = new Point3d(x, y, z);

        /* Set rotation */
        Transform3D transform = new Transform3D();
        transform.lookAt(position, new Point3d(), up);
        transform.invert();

        /* Set Translation */
        position.add(targetPosition);
        transform.setTranslation(new Vector3f(position)); //transform.setTranslation(position); // Strange break, bug ???;
        transformGroup.setTransform(transform);
    }

    public void setDistance(float f) {
        this.distance = f;
        refresh();
    }
}
