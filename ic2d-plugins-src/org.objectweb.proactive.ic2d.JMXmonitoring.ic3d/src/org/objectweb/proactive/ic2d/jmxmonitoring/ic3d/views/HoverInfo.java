/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PickInfo;
import javax.media.j3d.PickRay;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.pickfast.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.picking.PickTool;


/**
 * @author vjuresch
 *
 */
public class HoverInfo extends PickMouseBehavior {
    public HoverInfo(Canvas3D canvas, BranchGroup root, Bounds bounds) {
        super(canvas, root, bounds);
        // TODO Auto-generated constructor stub
        this.setSchedulingBounds(bounds);
        this.setMode(PickInfo.PICK_BOUNDS);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sun.j3d.utils.pickfast.behaviors.PickMouseBehavior#updateScene(int,
     *      int)
     */
    @Override
    public void updateScene(int xpos, int ypos) {
        // TODO Auto-generated method stub
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        super.initialize();
    }

    @Override
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] evt = null;
        int xpos = 0;
        int ypos = 0;

        pickCanvas.setFlags(PickInfo.PICK_GEOMETRY);

        System.out.println(pickCanvas.pickAny());

        // PickRay ray = createPickRay(pickCanvas.getCanvas(),
        // mevent.getPoint().x,
        // mevent.getPoint().y);
        //
        // SceneGraphPath path = pickCanvas.getLocale().pickClosest(ray);
        //        
        // System.out.println("obiect:" + path.getObject());

        // switch(select_mode)
        // {
        // case SELECT_ALL:
        // path = (group == null) ?
        // locale.pickAll(ray) : group.pickAll(ray);
        // break;
        // ...
        //        

        //        
        // while (criteria.hasMoreElements()) {
        // wakeup = (WakeupCriterion) criteria.nextElement();
        // if (wakeup instanceof WakeupOnAWTEvent) {
        // evt = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
        // }
        // }
        //
        // if (evt[0] instanceof MouseEvent) {
        // mevent = (MouseEvent) evt[0];
        //
        // System.out.println("got mouse event");
        // xpos = mevent.getPoint().x;
        // ypos = mevent.getPoint().y;
        //
        // System.out.println("mouse position " + xpos + " " + ypos);
        // }
        // if (buttonPress) {
        // updateScene(xpos, ypos);
        // }
        wakeupOn(wakeupCondition);
    }

    private PickRay createPickRay(Canvas3D canvas, int x, int y) {
        Point3d eye_pos = new Point3d();
        Point3d mouse_pos = new Point3d();

        canvas.getCenterEyeInImagePlate(eye_pos);
        canvas.getPixelLocationInImagePlate(x, y, mouse_pos);

        Transform3D motion = new Transform3D();
        canvas.getImagePlateToVworld(motion);
        motion.transform(eye_pos);
        motion.transform(mouse_pos);

        Vector3d direction = new Vector3d(mouse_pos);
        direction.sub(eye_pos);

        return new PickRay(eye_pos, direction);
    }
}
