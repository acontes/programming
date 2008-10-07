package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.FigureType;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.SiteBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu.TownActionListener;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.ActiveObject3D;

import com.sun.j3d.utils.picking.PickResult;


public class FlatCameraBehavior extends CameraBehavior {
	
    protected final static Vector3d up = new Vector3d(0, 1, 0);
    
    /* Attributes for spherical camera */
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

    
    protected void mouse1Dragged() {
    	/* The only objects you can drag are activeObjects */
        if ( selectedShape != null && selectedShape instanceof ActiveObject3D) {
        	if(dragObject == null) {
        		/* Fetching camera translation */
        		Transform3D cameraT3D = new Transform3D();
        		transformGroup.getTransform(cameraT3D);
        		Vector3d cameraTranslation = new Vector3d();
        		cameraT3D.get(cameraTranslation);
        		
        		/* Fetching object Translation */
        		Transform3D objectT3D = new Transform3D();
        		selectedShape.getLocalToVworld(objectT3D);
        		Vector3d objectTranslation = new Vector3d();
        		objectT3D.get(objectTranslation);
        		Vector3d objectScale = new Vector3d();
        		objectT3D.getScale(objectScale);
        		
        		/* Computing the offset from the camera to the object */
        		/* Axis changing */
        		objectTranslation.sub(cameraTranslation);
        		cameraT3D.invert();
        		cameraT3D.setTranslation(new Vector3d());
        		cameraT3D.transform(objectTranslation);
        		
        		dragObject = new DragObject(selectedShape.getGeometry(), dragBranch, objectTranslation);
        		dragObject.setScale(objectScale);
        		
        		dragObject.setAppearance(selectedShape.getAppearance());
        		
        		// TODO Send a message to the active object to hide
        	}
        	
        	// TODO Find the ratio pixel meter (+distance to camera) and use it as divider
        	// Else the object doesn't follow the mouse
        	float ratio_x,ratio_y;
        	ratio_y = (float)(y_last - y) / canvas3D.getHeight();
        	ratio_x = (float)(x - x_last) / canvas3D.getWidth();
        	ratio_x *= canvas3D.getPhysicalWidth();
        	ratio_y *= canvas3D.getPhysicalHeight();
        	
        	dragObject.move(ratio_x * 10f, ratio_y * 10f, 0);

        	//dragObject.move((float)(x - x_last)/(float)ratio_x, (float)(y_last - y)/(float)ratio_y, 0);
        }
        /* Outside a figure */
        else
        	cameraRotation(x - x_last, y - y_last);
    }

    
    protected void mouse1Pressed() {
        /* Left click -> Selects a shape */
        PickResult pickResult = null;
        if (pickCanvas == null) {
        	throw new NullPointerException("Pick Canvas not initialized");
        }
        pickCanvas.setShapeLocation(x, y);
        
        try {
        	/* Sometimes the matrix is not an affine Transform */
        	/* But it doesn't depend on the shape just the time you access it */
        	/* Really weird behavior */
        	pickResult = pickCanvas.pickClosest();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        if (pickResult == null)
            selectedShape = null;
        else
            selectedShape = ((Shape3D) pickResult.getNode(PickResult.SHAPE3D));
    }

    
    protected void mouse1Released() {
        if (selectedShape != null && selectedShapeTranslation != null) {
        	// TODO
        	// Remove the shape from the camera branch
        	// set the old shape visible
        	// Send the message
            TransformGroup tg = (TransformGroup) selectedShape.getParent();
            Transform3D t3d = new Transform3D();
            tg.getTransform(t3d);
            t3d.setTranslation(selectedShapeTranslation);
            tg.setTransform(t3d);
            selectedShapeTranslation = null;
        }
        
        /* try to drag the object */
        if(dragObject != null) {
            dragObject.detachDragObject();
            dragObject = null;
            
            /* Left click -> Selects a shape */
            PickResult pickResult = null;
            if (pickCanvas == null) {
            	throw new NullPointerException("Pick Canvas not initialized");
            }
            pickCanvas.setShapeLocation(x, y);
            
            try {
            	/* Sometimes the matrix is not an affine Transform */
            	/* But it doesn't depend on the shape just the time you access it */
            	/* Really weird behavior ( can happen between 2 frames even if nothing has been moved )*/
            	pickResult = pickCanvas.pickClosest();
            }
            catch (Exception e) {
            	/* Avoid the current thread to stop */
            	e.printStackTrace();
            }
            
            /* Select the nearest shape */
            Shape3D nearest = ((Shape3D) pickResult.getNode(PickResult.SHAPE3D));
            /* Drag it if the nearest object is a node */
            if(nearest instanceof AbstractFigure3D) {
            	AbstractFigure3D figure = (AbstractFigure3D)nearest;
				if( figure.getType() == FigureType.NODE ) {
					// Launch the drop procedure if we are on a node
					((ActiveObject3D)selectedShape).notifyObservers(figure);
				}
			}
        }
    }

    
    protected void mouse2DoubleClick() {
        // TODO Auto-generated method stub

    }

    
    protected void mouse2Dragged() {
        /* Move on the x-z plane ( mouse 2 ) */
        double x_diff, y_diff;
        x_diff = (double) (x - x_last) / 960d * distance;
        y_diff = (double) (y_last - y) / 960d * distance;
        targetPosition.z += Math.cos(phi) * y_diff + Math.sin(phi) * x_diff;
        targetPosition.x += Math.sin(phi) * y_diff - Math.cos(phi) * x_diff;
        refresh();
    }

    
    protected void mouse2Pressed() {
        // TODO Auto-generated method stub

    }

    
    protected void mouse2Released() {
        // TODO Auto-generated method stub

    }

    
    protected void mouse3DoubleClick() {
        // TODO Auto-generated method stub

    }

    
    protected void mouse3Dragged() {
        // TODO Auto-generated method stub

    }

    
    protected void mouse3Pressed() {
        /* Right click > Pop up context menu */
        mouse1Pressed();
        popup();
//        /* We need a context menu */
//        if (selectedShape != null) {
//            /* Checks the type of the figure */
//            //if( selectedShape instanceof FractalKoch3D )
//            //	pop = pops[2];
//            /*else */if (selectedShape instanceof ColorCube)
//                pop = pops[1];
//            else if(selectedShape instanceof Host3D)
//            	pop = pops[2];
//            /* Shan't happen all non listed figures should be marked as not pickable */
//            else
//                pop = pops[3];
//        }
//        /* Default menu */
//        else
//            pop = pops[0];
//        pop.show(canvas3D, x, y);
    }

    
    protected void mouse3Released() {
        // TODO Auto-generated method stub

    }

    
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

    
    public void setTarget(Point3d target) {
        this.targetPosition = target;
        refresh();
    }

    /*
     * Generic method used each time a parameter of the camera has moved
     * Shouldn't appear in the call graph more than once per mouse event
     */
    
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

	
	protected void initCameraDefaultMenu() {
		ActionListener al;
		MenuItem mit;
		popup = new PopupMenu("Camera");
		
		mit = new MenuItem("Reset");
		al = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				targetPosition = new Point3d();
				refresh();
			}
			
		};
		mit.addActionListener(al);
		popup.add(mit);
		
		String[] towns = { "sophia", "bordeaux", "grenoble", "lille", "nancy", "lyon", "paris", "orsay", "rennes", "toulouse" };
		
		for(int i = 0; i < towns.length; i++) {
			mit = new MenuItem("Goto: " + towns[i]);
			al = new TownActionListener(this, towns[i]);
			mit.addActionListener(al);
			popup.add(mit);
		}
		
		canvas3D.add(popup);
	}
}
