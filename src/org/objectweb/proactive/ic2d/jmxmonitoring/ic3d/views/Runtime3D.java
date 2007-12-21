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
public class Runtime3D extends AbstractRuntime3D {
    private static float NODE_SPACING = 0.05f;

    public Runtime3D(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    // create a flat box
    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getDefaultRuntimeGeometry();
    }

    // code to create default appearance of visual object
    @Override
    protected Appearance createAppearance() {
        return AppearanceBasket.defaultRuntimeAppearance;
    }

    @Override
    public void arrangeSubFigures() {
        Vector3d oldTransl = new Vector3d();
        Vector3d oldScale = new Vector3d();
        Vector3d newScale = new Vector3d(1, 1, 1);
        Transform3D runtimePosition = new Transform3D();
        // get host Transform3d(scale and translation)
        this.getLocalToVworld(runtimePosition);
        // get the number of nodes,
        int noOfNodes = this.getSubFigures().size();
        runtimePosition.getScale(oldScale);
        runtimePosition.get(oldTransl);

        // TODO remove constant
        // get the runtime width by subtracting
        // the amount of space (noOfNodes + 1)*RUN_SPACE
        // from the total (1) and dividing by the nr of runtimes
        double nodeWidth = ((oldScale.y * 1) -
            ((noOfNodes + 1) * NODE_SPACING)) / noOfNodes;

        // TODO remove constant
        int i = 1; // to count the runtimes
        newScale.x = oldScale.x * 0.8; // length - slightly smaller
        newScale.y = nodeWidth;
        newScale.z = oldScale.z * 1.7; // height - slightly bigger so it's
                                       // visible

        runtimePosition.setScale(newScale);

        TransformGroup moveOld;

        for (AbstractFigure3D oldRuntime : this.getSubFigures().values()) {
            // translate
            // TODO remove constant
            // get the host translation
            // translate the runtime
            runtimePosition.setTranslation(new Vector3d(oldTransl.x +
                    (0.1 * newScale.x),
                    oldTransl.y + (NODE_SPACING * i) + (nodeWidth * (i - 1)),
                    oldTransl.z));

            moveOld = (TransformGroup) oldRuntime.getParent().getParent();
            moveOld.setTransform(runtimePosition);

            i++;
            oldRuntime.arrangeSubFigures();
        }
    }

    @Override
    protected TransformGroup createTextBranch() {
        // add the text
        Text2D figureName = new Text2D(this.getShortenedName(10),
                ColorPalette.BLACK, Font.SERIF, 20, Font.BOLD);

        TransformGroup fontRotate = createTransform();
        Transform3D rotate = new Transform3D();
        rotate.rotX(Math.PI / 3);
        fontRotate.setTransform(rotate);

        // move a little the font shape
        TransformGroup fontTrans = createTransform();
        Transform3D translate = new Transform3D();
        // TODO remove constants
        translate.setTranslation(new Vector3d(0.d, -0.1d, 0.5d));
        translate.setScale(1.5);
        // TODO make text slanted on object
        fontTrans.setTransform(translate);

        fontRotate.addChild(figureName);
        fontTrans.addChild(fontRotate);
        // add the transform the the branch group
        return fontTrans;
    }
}
