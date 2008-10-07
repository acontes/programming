package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.FigureType;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;


public class Cluster3D extends AbstractCluster3D {

    public Cluster3D(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    
    public void animateCreation() {
        // TODO Auto-generated method stub

    }

    
    public void arrangeSubFigures() {
        // TODO Auto-generated method stub

    }

    
    protected Appearance createAppearance() {
        // TODO Auto-generated method stub
        return null;
    }

    
    protected Geometry createGeometry() {
        // TODO Auto-generated method stub
        return null;
    }

    
    protected TransformGroup createTextBranch() {
        // TODO Auto-generated method stub
        return null;
    }

    
    protected Figure3D setArrow(final String name, final Vector3f start, final Vector3f stop) {
        // TODO Auto-generated method stub
        return null;
    }

	
	public FigureType getType() {
		// TODO Auto-generated method stub
		return FigureType.CLUSTER;
	}

}
