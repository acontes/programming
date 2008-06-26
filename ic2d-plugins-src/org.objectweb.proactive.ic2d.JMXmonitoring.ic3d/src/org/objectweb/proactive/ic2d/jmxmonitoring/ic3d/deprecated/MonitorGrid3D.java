package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.deprecated;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.FigureType;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.PlacementBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;


public class MonitorGrid3D extends AbstractGrid3D {

    public MonitorGrid3D() {
        super("");
        System.out.println("MonitorGrid");
        // TODO Auto-generated constructor stub
    }

    @Override
    public void animateCreation() {
        // TODO Auto-generated method stub

    }

    @Override
    public void arrangeSubFigures() {
        int figureIndex = 1;
        for (final Figure3D host : this.getSubFigures().values()) {
            PlacementBasket.matrixArrangement(figureIndex, host);
            figureIndex++;
            host.arrangeSubFigures();
        }
    }

    @Override
    protected Appearance createAppearance() {
        // TODO Auto-generated method stub
        return AppearanceBasket.defaultNodeAppearance;
    }

    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getDefaultHostGeometry();
    }

    @Override
    protected TransformGroup createTextBranch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Figure3D setArrow(final String name, final Vector3f start, final Vector3f stop) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FigureType getType() {
    	// TODO Auto-generated method stub
    	return FigureType.GRID;
    }
}
