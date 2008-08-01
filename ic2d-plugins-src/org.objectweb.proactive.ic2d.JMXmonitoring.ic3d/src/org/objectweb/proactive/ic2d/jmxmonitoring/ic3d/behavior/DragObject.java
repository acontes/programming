package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

public class DragObject extends Shape3D {
	private BranchGroup camera;
	private BranchGroup localRoot;
	private TransformGroup translate;
	private Vector3d translation;
	
	public DragObject(Geometry geom, BranchGroup tg, Vector3d translation) {
		this.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		this.camera = tg;
		this.setGeometry(geom);
		this.translation = translation;
		localRoot = new BranchGroup();
		localRoot.setCapability(BranchGroup.ALLOW_DETACH);
		localRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		localRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		translate = new TransformGroup();
		translate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		translate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D t3d = new Transform3D();
		t3d.setTranslation(translation);
		translate.setTransform(t3d);
		localRoot.addChild(translate);
		translate.addChild(this);
		camera.addChild(localRoot);
	}
	
	public DragObject(TransformGroup tg) {
		localRoot = new BranchGroup();
		localRoot.setCapability(BranchGroup.ALLOW_DETACH);
		localRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		localRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		tg.addChild(localRoot);
		tg.removeChild(localRoot);
	}
	
	public void detachDragObject() {
		camera.removeChild(localRoot);
	}
	
	public void move(float x, float y, float z) {
		translation.x += x;
		translation.y += y;
		translation.z += z;
		Transform3D t3d = new Transform3D();
		translate.getTransform(t3d);
		t3d.setTranslation(translation);
		translate.setTransform(t3d);
	}
	
	public void setScale(Vector3d objectScale) {
		Transform3D t3d = new Transform3D();
		translate.getTransform(t3d);
		t3d.setScale(objectScale);
		translate.setTransform(t3d);
	}

}
