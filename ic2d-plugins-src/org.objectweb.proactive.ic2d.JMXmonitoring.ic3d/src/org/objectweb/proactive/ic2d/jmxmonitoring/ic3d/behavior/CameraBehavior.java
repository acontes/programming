package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior;

import java.awt.AWTEvent;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickTool;


public abstract class CameraBehavior extends Behavior {
    /* Must be set in every case */
    protected Canvas3D canvas3D;
    protected PickCanvas pickCanvas;
    protected TransformGroup transformGroup;
    protected int x, y, y_last, x_last;
    protected WakeupOr mouseCriterion;

    /* Don't know if this is right place */
    protected Shape3D selectedShape;
    protected Vector3f selectedShapeTranslation;

    /* The point the camera does look At */
    Point3d targetPosition;

    /* Shouldn't be there but testing */
    protected PopupMenu[] pops = new PopupMenu[4];
    private BranchGroup cameraBranch;

    public CameraBehavior() {
        super();
        cameraBranch = new BranchGroup();
        targetPosition = new Point3d();
        this.setSchedulingBounds(new BoundingSphere(new Point3d(), 150));
    }

    /*
     * Init Graphical components associated to our camera
     * Aka pop up menus
     * 
     */
    private void initComponents() {
        MenuItem mit;
        ActionListener ali;

        pops[1] = new PopupMenu("ColorCube");
        pops[2] = new PopupMenu("Host");
        pops[2].add(new MenuItem("Stop Monitoring"));
        mit = new MenuItem("Reset translation");
        ali = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetCurentTranslation();
            }
        };
        mit.addActionListener(ali);
        pops[2].add(mit);

        pops[3] = new PopupMenu("Other");

        pops[0] = new PopupMenu("Goto");
        mit = new MenuItem("Origin");
        ali = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                targetPosition.set(new Point3d());
                refresh();
            }
        };
        mit.addActionListener(ali);
        pops[0].add(mit);
        canvas3D.add(pops[0]);
        canvas3D.add(pops[1]);
        canvas3D.add(pops[2]);
        canvas3D.add(pops[3]);
    }

    /* Reset the translation of the selected figure */
    protected void resetCurentTranslation() {
        if (selectedShape != null && selectedShapeTranslation != null) {
            TransformGroup tg = (TransformGroup) selectedShape.getParent();
            Transform3D t3d = new Transform3D();
            tg.getTransform(t3d);
            t3d.setTranslation(new Vector3f());
            tg.setTransform(t3d);
            selectedShapeTranslation = null;
        }
    }


    public void initialize() {
        WakeupCriterion[] mouseEvents = new WakeupCriterion[4];
        mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
        mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_WHEEL);
        mouseEvents[3] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        mouseCriterion = new WakeupOr(mouseEvents);
        wakeupOn(mouseCriterion);
    }

    @SuppressWarnings("unchecked")
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] event;
        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                for (int i = 0; i < event.length; i++) {
                    if (event[i] instanceof MouseEvent) {
                        MouseEvent me = (MouseEvent) event[i];
                        if (me.getComponent() == canvas3D) {
                            if (me.getID() == MouseEvent.MOUSE_DRAGGED) {
                                /* Maintain mouse position */
                                x_last = x;
                                y_last = y;
                                x = ((MouseEvent) event[i]).getX();
                                y = ((MouseEvent) event[i]).getY();
                                if (!me.isAltDown() && !me.isMetaDown())
                                    mouse1Dragged();
                                else if (me.isAltDown() && !me.isMetaDown())
                                    mouse2Dragged();
                                else if (me.isAltDown() && me.isMetaDown())
                                    mouse3Dragged();
                            }
                            /* Mouse released */
                            else if (me.getID() == MouseEvent.MOUSE_RELEASED) {
                                /* Button 1 > Drop feature */
                                if (me.getButton() == MouseEvent.BUTTON1)
                                    mouse1Released();
                                if (me.getButton() == MouseEvent.BUTTON2)
                                    mouse2Released();
                                if (me.getButton() == MouseEvent.BUTTON3)
                                    mouse3Released();
                            }
                            /* Zoom functions */
                            else if (me.getID() == MouseEvent.MOUSE_WHEEL)
                                mouseWheel(me.getClickCount(), ((MouseWheelEvent) me).getWheelRotation());
                            else if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                                /* Reset the x and y value ( useful for dragging ) */
                                x = ((MouseEvent) event[i]).getX();
                                y = ((MouseEvent) event[i]).getY();
                                if (me.getButton() == MouseEvent.BUTTON1) {
                                    if (me.getClickCount() == 1)
                                        mouse1Pressed();
                                    else if (me.getClickCount() == 2)
                                        mouse1DoubleClick();
                                } else if (me.getButton() == MouseEvent.BUTTON2) {
                                    if (me.getClickCount() == 1)
                                        mouse2Pressed();
                                    else if (me.getClickCount() == 2)
                                        mouse2DoubleClick();
                                } else if (me.getButton() == MouseEvent.BUTTON3) {
                                    if (me.getClickCount() == 1)
                                        mouse3Pressed();
                                    else if (me.getClickCount() == 2)
                                        mouse3DoubleClick();
                                }
                            }
                        }
                    }
                }
            }
        }
        wakeupOn(mouseCriterion);
    }

    protected abstract void mouse1Pressed();

    protected abstract void mouse2Pressed();

    protected abstract void mouse3Pressed();

    protected abstract void mouse1DoubleClick();

    protected abstract void mouse2DoubleClick();

    protected abstract void mouse3DoubleClick();

    protected abstract void mouse1Released();

    protected abstract void mouse2Released();

    protected abstract void mouse3Released();

    protected abstract void mouse1Dragged();

    protected abstract void mouse2Dragged();

    protected abstract void mouse3Dragged();

    protected abstract void mouseWheel(int amount, int direction);

    protected abstract void refresh();

    public abstract void setTarget(Point3d target);

    public BranchGroup getBranchGroup() {
        return cameraBranch;
    }

    public void set(Canvas3D c3D, TransformGroup cameraTransform, BranchGroup localScene) {
        canvas3D = c3D;
        transformGroup = cameraTransform;
        pickCanvas = new PickCanvas(canvas3D, localScene);
        pickCanvas.setMode(PickTool.GEOMETRY);
        pickCanvas.setTolerance(0f);
        cameraBranch.addChild(this);
        initComponents();
        refresh();
    }
}