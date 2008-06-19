/**
 * 
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets;

import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.ScaleInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TransparencyInterpolator;
import javax.vecmath.Vector3d;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;


/**
 * This class is a container for all the animations. An object that wants to use
 * the animation has to create a new instance of this class and use it to
 * animate itself.
 * 
 * @author vasile
 * 
 */
public class AnimationBasket {
    /**
     * This method makes the figure grow from a small point. It uses the
     * original coordinates of the figure while it does a scale from 0 to the
     * original figure size.
     * 
     * @param figure
     *            the figure to be animated
     * @param runTime
     *            animation time in milliseconds
     */
    public void growFromNothing(final AbstractFigure3D figure, final int runTime) {
        final TransformGroup trans = (TransformGroup) figure.getParent();
        final Transform3D t3d = new Transform3D();
        trans.getTransform(t3d);
        final Alpha alpha = new Alpha(1, runTime);
        final ScaleInterpolator scale = new ScaleInterpolator(alpha, trans, t3d, 0, (float) t3d.getScale());
        scale.setSchedulingBounds(new BoundingSphere());
        // the branch group is needed in order to be able
        // to add to the compiled scene
        final BranchGroup bg = new BranchGroup();
        bg.addChild(scale);
        trans.addChild(bg);
    }

    /**
     * Fades the figure from zero transparency to the default transparency of
     * the figure.
     * 
     * @param figure
     *            figure to be animated
     * @param runTime
     *            time until object has it's original transparency
     */
    public void fadeInto(final AbstractFigure3D figure, final int runTime) {
        final TransparencyAttributes attrib = figure.getAppearance().getTransparencyAttributes();
        final Alpha alpha = new Alpha(1, runTime);
        final TransparencyInterpolator transp = new TransparencyInterpolator(alpha, attrib, 1, attrib
                .getTransparency());
        transp.setSchedulingBounds(new BoundingSphere());

        // the branch group is needed in order to be able
        // to add to the compiled scene

        final BranchGroup bg = new BranchGroup();
        bg.addChild(transp);
        figure.getFigureBranch().addChild(bg);
    }

    public void skyDrop(final AbstractFigure3D figure, final int runTime) {
        final TransformGroup trans = (TransformGroup) figure.getParent();
        final Transform3D t3d = new Transform3D();
        final Vector3d translation = new Vector3d();
        trans.getTransform(t3d);
        t3d.get(translation);
        final Alpha alpha = new Alpha(1, runTime);
        final PositionInterpolator scale = new PositionInterpolator(alpha, trans, t3d, -100,
            (float) translation.x);
        scale.setSchedulingBounds(new BoundingSphere());
        // the branch group is needed in order to be able
        // to add to the compiled scene
        final BranchGroup bg = new BranchGroup();
        bg.addChild(scale);
        figure.getFigureBranch().addChild(bg);

    }
}
