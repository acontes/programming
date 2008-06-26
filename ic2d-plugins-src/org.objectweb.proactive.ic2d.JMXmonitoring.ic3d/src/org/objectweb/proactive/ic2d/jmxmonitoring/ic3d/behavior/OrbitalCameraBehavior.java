package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior;

import java.awt.MenuItem;
import java.awt.PopupMenu;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.GeometryBasket;

import com.sun.j3d.utils.picking.PickResult;


public class OrbitalCameraBehavior extends CameraBehavior {
    // Vector up is used to make the camera look at some point
    protected final static Vector3d up = new Vector3d(0, 1, 0);
    // Bounds for the phi angle
    protected final static double min_phi = Math.PI / 36;
    protected final static double max_phi = 35 * Math.PI / 36;
    // Bounds for the distance of the camera
    protected final static double min_distance = GeometryBasket.EARTH_RADIUS * 1.1;
    protected final static double max_distance = GeometryBasket.EARTH_RADIUS * 2.5;

    // Theta is the angle of the camera relative to Z-X plane [0-2PI]
    private double theta;
    // Phi is the angle of the angle of the camera related to the Y axis [0-PI]
    private double phi;
    // Distance is the distance from the camera to the target
    private double distance;

    public OrbitalCameraBehavior() {
        super();
        // Initialize the spherical camera settings
        theta = 0;
        phi = Math.PI / 2;
        distance = (min_distance + max_distance) / 2;
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
            shapePosition.sub(new Vector3f(targetPosition));

            /* TODO Earth figure mustn't be pickable then remove this test */
            if (!shapePosition.equals(new Vector3f())) {
                phi = Math.acos(shapePosition.y / shapePosition.length());

                shapePosition.y = 0;

                theta = Math.asin(shapePosition.x / shapePosition.length());
                if (shapePosition.z < 0) {
                    theta = Math.PI - theta;
                }
                refresh();
            }
        }
    }

    @Override
    protected void mouse1Dragged() {
        /* On a figure  drag it */
    	// TODO add test for draggable shapes
        if (selectedShape != null)
            dragSelected(x - x_last, y_last - y);
        /* Outside a figure */
        //else
        cameraRotation(x - x_last, y - y_last);
    }

    @Override
    protected void mouse1Pressed() {
        /* Pick the shape */
        PickResult pickResult = null;
        pickCanvas.setShapeLocation(x, y);
        pickResult = pickCanvas.pickClosest();

        /* Set the selected shape as current selection */
        if (pickResult == null)
            selectedShape = null;
        else {
            selectedShape = ((Shape3D) pickResult.getNode(PickResult.SHAPE3D));
            Transform3D location = new Transform3D();
            selectedShape.getLocalToVworld(location);
            Vector3f shapePosition = new Vector3f();
            location.get(shapePosition);
            if (shapePosition.equals(new Vector3f(targetPosition)))
                selectedShape = null;
        }
    }

    @Override
    protected void mouse1Released() {
    	// TODO Drop properly the active Objects
    }

    @Override
    protected void mouse2DoubleClick() { return; }

    @Override
    protected void mouse2Dragged() { return; }

    @Override
    protected void mouse2Pressed() { return; }

    @Override
    protected void mouse2Released() { return; }

    @Override
    protected void mouse3DoubleClick() { return; }

    @Override
    protected void mouse3Dragged() { return; }

    @Override
    protected void mouse3Pressed() {
        /* Right click > Pop up context menu */
        mouse1Pressed();
//        PopupMenu pop;
//
//        /* We need a context menu */
//        if (selectedShape != null) {
//            /* Checks the type of the figure */
//            /*else */if (selectedShape instanceof ColorCube)
//                pop = pops[1];
//            /* Shan't happen all non listed figures should be marked as not pickable */
//            else
//                pop = pops[0];
//        }
//        /* Default menu */
//        else
//            pop = pops[0];
//        pop.show(canvas3D, x, y);
        popup();
    }

    @Override
    protected void mouse3Released() { return; }

    @Override
    protected void mouseWheel(int amount, int direction) {
    	// Adjust the distance of the camera
        distance += (double) (amount * direction) / 6d;
        // Make it fit the bounds
        if (distance > max_distance)
            distance = max_distance;
        else if (distance < min_distance)
            distance = min_distance;
        // Set changes on screen
        refresh();
    }

    private void dragSelected(float x_diff, float y_diff) {
    	/*
        double selectedPhi, selectedTheta;
        TransformGroup tg = (TransformGroup) selectedShape.getParent().getParent();
        Transform3D transform = new Transform3D();
        Vector3f shapePosition = new Vector3f();

        tg.getTransform(transform);
        transform.get(shapePosition);

        selectedPhi = Math.acos(shapePosition.y / shapePosition.length());

        shapePosition.y = 0;

        selectedTheta = Math.asin(shapePosition.x / shapePosition.length());
        if (shapePosition.z < 0) {
            selectedTheta = Math.PI - selectedTheta;
        }

        selectedTheta += (double) x_diff / 60d;
        selectedTheta %= Math.PI * 2;
        selectedPhi -= (double) y_diff / 60d;
        if (selectedPhi < min_phi)
            selectedPhi = min_phi;
        else if (selectedPhi > max_phi)
            selectedPhi = max_phi;
        shapePosition.z = (float) (Math.cos(selectedTheta) * Math.sin(selectedPhi) *
            GeometryBasket.EARTH_RADIUS * 1.1f);
        shapePosition.x = (float) (Math.sin(selectedTheta) * Math.sin(selectedPhi) *
            GeometryBasket.EARTH_RADIUS * 1.1f);

        //get the z coordinate
        shapePosition.y = (float) (Math.cos(selectedPhi) * GeometryBasket.EARTH_RADIUS * 1.1f);

        TransformGroup rotation = (TransformGroup)selectedShape.getParent();
        Transform3D lookAtTransform = new Transform3D();
        lookAtTransform.lookAt(new Point3d(), new Point3d(shapePosition), new Vector3d(0, 1, 0));
        lookAtTransform.invert(); // Keep it else wont work ( java3d failure )

        // Rotate the lookAt Matrix the right way
        Transform3D rotationMatrix = new Transform3D();
        rotationMatrix.rotX(-Math.PI / 2);
        // Add the second transform
        lookAtTransform.mul(rotationMatrix);
        lookAtTransform.normalize();

        // And commit the changes
        rotation.setTransform(lookAtTransform);
        
        transform.setTranslation(shapePosition);
        tg.setTransform(transform);

        cameraRotation((int) x_diff, (int)-y_diff);
		*/
    }

    /*
     * Applies a camera rotation
     */
    private void cameraRotation(int x_rot, int y_rot) {
        /* Refresh parameter */
        theta += (double) x_rot / 120d;
        theta %= Math.PI * 2;
        phi += (double) y_rot / 120d;
        if (phi < min_phi)
            phi = min_phi;
        else if (phi > max_phi)
            phi = max_phi;
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
        x = Math.sin(theta) * Math.sin(phi) * distance;
        z = Math.cos(theta) * Math.sin(phi) * distance;
        y = Math.cos(phi) * distance;
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

	@Override
	protected void initCameraDefaultMenu() {
		popup = new PopupMenu("Camera");
		popup.add(new MenuItem("Reset"));
		popup.add(new MenuItem("Goto: Nice"));
		canvas3D.add(popup);
	}
}
