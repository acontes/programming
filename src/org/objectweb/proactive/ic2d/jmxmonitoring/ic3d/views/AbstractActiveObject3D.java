package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;

import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;


public abstract class AbstractActiveObject3D extends AbstractFigure3D {
    public AbstractActiveObject3D(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public abstract void setQueueSize(int size);

    public abstract void setMigrating();

    public abstract void setServingRequest();

    public abstract void setActive();

    public abstract void setWaitingForRequest();

    public abstract void setUnknown();

    public void setState(State state) {
        switch (state) {
        case SERVING_REQUEST:
            setServingRequest();
            break;
        case MIGRATING:
            setMigrating();
            break;
        case ACTIVE:
            setActive();
            break;
        case WAITING_FOR_REQUEST:
            setWaitingForRequest();
            break;
        default:
            setUnknown();
            break;
        }
    }
}
