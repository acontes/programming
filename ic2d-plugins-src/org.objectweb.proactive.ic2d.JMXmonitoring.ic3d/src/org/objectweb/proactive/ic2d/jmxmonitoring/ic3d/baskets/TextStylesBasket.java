/**
 * 
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Line2D;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Text2D;


/**
 * Contains different text styles for the figures.
 * 
 * @author vasile
 * 
 */
public class TextStylesBasket {

    public static TransformGroup hostText(final String text) {
        return TextStylesBasket.figureText3D(text, ColorPalette.BLACK, "Arial", 25, Font.PLAIN, new Vector3d(
            0d, 0d, 0.7d), new Vector3d(-Math.PI / 4, 0, 0), 1);
    }

    public static TransformGroup runtimeText(final String text) {
        return TextStylesBasket.figureText(text, ColorPalette.LIGHT_GRAY, Font.SERIF, 20, Font.BOLD, new Vector3d(
            0d, 1d, 0.475d), new Vector3d(-Math.PI / 3, 0, 0), 1.5);
    }

    public static TransformGroup activeObjectText(final String text) {
        return TextStylesBasket.figureText(text, ColorPalette.YELLOW, Font.MONOSPACED, 30, Font.BOLD,
                new Vector3d(1.5d, -0.1d, 0d), new Vector3d(0, 0, 0), 8);

    }

    public static TransformGroup nodeText(final String text) {
        return TextStylesBasket.figureText(text, ColorPalette.LIGHT_GRAY, "Arial", 35, Font.BOLD, new Vector3d(
            0d, 0.75d, -0.3d), new Vector3d(-Math.PI/2, 0, 0), 1); // no rotation or scaling
    }

    private static TransformGroup figureText3D(final String text, final Color3f color, final String fontName,
            final int fontSize, final int fontStyle, final Vector3d translation, final Vector3d rotation,
            final double scale) {
    	Font3D font3d = new Font3D(new Font(fontName, fontStyle, fontSize), new FontExtrusion(new Line2D.Double(0d,0d,5d,5d)));
    	Text3D textGeom = new Text3D(font3d, text, new Point3f(), Text3D.ALIGN_CENTER, Text3D.PATH_RIGHT);

        //final Text2D figureName = new Text2D(text, color, fontName, fontSize, fontStyle);
        
        /*final Point3d point = new Point3d();
        final BoundingBox box = (BoundingBox)figureName.getBounds();
        box.getUpper(point);
        translation.x -= point.x / 2d;*/
        
        final TransformGroup fontRotate = TextStylesBasket.createTransform();
        final Transform3D rotate = new Transform3D();
        rotate.setEuler(rotation);
        fontRotate.setTransform(rotate);
        final TransformGroup fontTrans = TextStylesBasket.createTransform();
        final Transform3D translate = new Transform3D();
        translate.setTranslation(translation);
        translate.setScale(scale/200d);
        fontTrans.setTransform(translate);
        
        Shape3D textShape = new Shape3D(textGeom);
        textShape.setAppearance(AppearanceBasket.red);
        fontRotate.addChild(textShape);
        //fontRotate.addChild(figureName);
        fontTrans.addChild(fontRotate);
        // add the transform the the branch group
        return fontTrans;

    }

    private static TransformGroup figureText(final String text, final Color3f color, final String fontName,
            final int fontSize, final int fontStyle, final Vector3d translation, final Vector3d rotation,
            final double scale) {
    	final Text2D figureName = new Text2D(text, color, fontName, fontSize, fontStyle);
        
        final Point3d point = new Point3d();
        final BoundingBox box = (BoundingBox)figureName.getBounds();
        box.getUpper(point);
        translation.x -= point.x / 2d;
        
        final TransformGroup fontRotate = TextStylesBasket.createTransform();
        final Transform3D rotate = new Transform3D();
        rotate.setEuler(rotation);
        fontRotate.setTransform(rotate);
        final TransformGroup fontTrans = TextStylesBasket.createTransform();
        final Transform3D translate = new Transform3D();
        translate.setTranslation(translation);
        translate.setScale(scale);
        fontTrans.setTransform(translate);
        fontRotate.addChild(figureName);
        fontTrans.addChild(fontRotate);
        // add the transform the the branch group
        return fontTrans;

    }

    private static TransformGroup createTransform() {
        final TransformGroup trans = new TransformGroup();
        // set the transform group capabilities
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans.setCapability(Group.ALLOW_CHILDREN_WRITE);
        trans.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
        trans.setCapability(Node.ENABLE_PICK_REPORTING);
        trans.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        return trans;
    }

	public static TransformGroup hostText(String text, Color3f color) {
		return TextStylesBasket.figureText(text, color, "Arial", 25, Font.PLAIN, new Vector3d(
	            0d, 0d, 0.7d), new Vector3d(-Math.PI / 4, 0, 0), 1);
	    
	}

	public static TransformGroup runtimeLoadText(String text) {
		return TextStylesBasket.figureText(text, ColorPalette.RED, Font.SERIF, 20, Font.BOLD, new Vector3d(
	            0d, 0d, 0d), new Vector3d(-Math.PI/2, 0, 0), 1.5);
	}

}
