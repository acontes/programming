/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.gui;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector2d;

import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;

/**
 * @author vjuresch
 * 
 */
public abstract class AbstractWidget extends Shape3D implements
		MouseBehaviorCallback {
	// beh
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback#transformChanged(int,
	 *      javax.media.j3d.Transform3D)
	 */
	public void transformChanged(final int type, final Transform3D transform) {
		// TODO Auto-generated method stub
	}

	public abstract void setVisible(boolean visible);

	public abstract void setEnable(boolean enabled);

	public abstract void setPosition(Vector2d position);

	public abstract void setSize(Vector2d size);

	public abstract void setTransparencyLevel(double transparency);

	protected abstract void setGeometry();

	protected abstract void setAppearance();
}
