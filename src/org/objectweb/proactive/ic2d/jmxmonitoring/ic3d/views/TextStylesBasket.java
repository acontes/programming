/**
 * 
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.Font;

import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Text2D;

/**
 * Contains different text styles for the 
 * figures. 
 * @author vasile
 *
 */
public class TextStylesBasket {

	public static TransformGroup hostText(String text){
		return figureText(text,
              ColorPalette.BLACK, "Arial", 25, Font.PLAIN,
              new Vector3d(0d, -0.1d, 0.1d),
              new AxisAngle4d(1,0,0,Math.PI / 4),1);	
	}
	public static TransformGroup runtimeText(String text){
		return figureText(text,
                ColorPalette.BLACK, Font.SERIF, 20, Font.BOLD, 
                new Vector3d(1.1d, 0.1d, 0.5d),
                new AxisAngle4d(1,0,0,Math.PI/3),1.5);	
	}

	public static TransformGroup activeObjectText(String text){
		return figureText(text,
                ColorPalette.BLACK, Font.MONOSPACED, 30, Font.BOLD,
                new Vector3d(1.5d, -1d, 0d), 
                new AxisAngle4d(1,0,0,Math.PI/2),8);
		
	}
	public static TransformGroup nodeText(String text){
		return figureText(text,
                ColorPalette.BLACK, "Arial", 35, Font.BOLD,
                new Vector3d(0.1d, 0.1d, 1.1d), 
                new AxisAngle4d(0,0,0,0),1); //no rotation or scaling		
	}

	private static TransformGroup figureText(
					String text,
					Color3f color, String fontName,
					int fontSize, int fontStyle,
					Vector3d translation, AxisAngle4d rotation,
					double scale){
        Text2D figureName = new Text2D(text, color,
                fontName, fontSize,fontStyle);

        TransformGroup fontRotate = createTransform();
        Transform3D rotate = new Transform3D();
        rotate.setRotation(rotation);
        fontRotate.setTransform(rotate);
        TransformGroup fontTrans = createTransform();
        Transform3D translate = new Transform3D();
        translate.setTranslation(translation);
        translate.setScale(scale);
        fontTrans.setTransform(translate);
        fontRotate.addChild(figureName);
        fontTrans.addChild(fontRotate);
        //add the transform the the branch group
        return fontTrans;

	}
	private static TransformGroup createTransform() {
        TransformGroup trans = new TransformGroup();
        //set the transform group capabilities
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans.setCapability(Group.ALLOW_CHILDREN_WRITE);
        trans.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
        trans.setCapability(Node.ENABLE_PICK_REPORTING);
        trans.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        return trans;
    }

}
