/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Text2D;


/**
 * @author vjuresch
 *
 */
public class Node3D extends AbstractNode3D {
    public Node3D(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    private double ACTIVE_OBJECTS_SPACING = 0.03;

    // modify the already added active objects (scale and move)
    @Override
    public void arrangeSubFigures() {
        Vector3d oldTransl = new Vector3d();
        Vector3d oldScale = new Vector3d();

        // TODO scale is constant, change
        Transform3D nodePosition = new Transform3D();
        // get node Transform3d(scale and translation)
        this.getLocalToVworld(nodePosition);
        // get the number of active objects
        nodePosition.getScale(oldScale);
        nodePosition.setScale(new Vector3d(.05, .05, .05));
        int i = 1; // to count the runtimes
        double nodeWidth = oldScale.x;
        TransformGroup moveOld;
        nodePosition.get(oldTransl);
        for (AbstractFigure3D oldActiveO : this.getSubFigures().values()) {
            // scale
            moveOld = (TransformGroup) oldActiveO.getParent().getParent();

            // translate
            // TODO remove constant
            // get the node translation
            // translate the active object
            nodePosition.setTranslation(new Vector3d(oldTransl.x +
                    (nodeWidth / 2), oldTransl.y + (oldScale.y / 2),
                    oldTransl.z + (i / 6f) + 0.1));
            moveOld = (TransformGroup) oldActiveO.getParent().getParent();
            moveOld.setTransform(nodePosition);
            i++;
        }

        // change the height of the node to fit the number of active objects
        TransformGroup scaleHeight = (TransformGroup) this.getParent()
                                                          .getParent();
        Transform3D height = new Transform3D();
        scaleHeight.getTransform(height);
        height.setScale(new Vector3d(oldScale.x, oldScale.y, (i / 6f) + 0.1));

        scaleHeight.setTransform(height);
    }

    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getDefaultNodeGeometry();
    }

    // code to create default appearance of visual object
    @Override
    protected Appearance createAppearance() {
        return AppearanceBasket.defaultNodeAppearance;
    }

    @Override
    protected TransformGroup createTextBranch() {
        // add the text
        Text2D figureName = new Text2D(this.getShortenedName(10),
                ColorPalette.BLACK, "Arial", 35, Font.BOLD);

        // move a little the font shape
        TransformGroup fontTrans = createTransform();
        Transform3D translate = new Transform3D();
        translate.setTranslation(new Vector3d(0.1d, 0.1d, 1.1d));
        // translate.setScale(new Vector3d(1,25,25));
        // TODO make text perpendicular on host
        fontTrans.setTransform(translate);

        fontTrans.addChild(figureName);
        // add the transform the the branch group
        return fontTrans;
    }
}
