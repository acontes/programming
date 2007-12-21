package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.Font;
import java.util.Hashtable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.image.TextureLoader;


//creates a flat surfaces of size 1x1 m
public class Host3D extends AbstractHost3D {
    public Host3D(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    private double RUNTIMES_SPACING = 0.03;

    // modify the already added runtimes(scale and
    @Override
    public void arrangeSubFigures() {
        Vector3d oldTransl = new Vector3d();
        Transform3D hostPosition = new Transform3D();
        // get host Transform3d(scale and translation)
        this.getLocalToVworld(hostPosition);
        // get the number of runtimes,
        int noOfRuntimes = this.getSubFigures().size();

        // TODO remove constant
        // get the runtime width by subtracting
        // the amount of space (noOfRuntimes + 1)*RUN_SPACE
        // from the total (1) and dividing by the nr of runtimes
        double runtimeWidth = (1 - ((noOfRuntimes + 1) * RUNTIMES_SPACING)) / noOfRuntimes;
        // TODO remove constant
        hostPosition.setScale(new Vector3d(runtimeWidth, 0.7, 0.1));
        int i = 1; // to count the runtimes
        TransformGroup moveOld;
        hostPosition.get(oldTransl);

        for (AbstractFigure3D oldRuntime : this.getSubFigures().values()) {
            // translate
            // TODO remove constant
            // get the host translation
            // translate the runtime
            hostPosition.setTranslation(new Vector3d(oldTransl.x +
                    (RUNTIMES_SPACING * i) + (runtimeWidth * (i - 1)),
                    oldTransl.y + 0.15, oldTransl.z + 0));
            moveOld = (TransformGroup) oldRuntime.getParent().getParent();
            moveOld.setTransform(hostPosition);
            i++;
            oldRuntime.arrangeSubFigures();
        }
    }

    @Override
    protected Geometry createGeometry() {
        return GeometryBasket.getDefaultHostGeometry();
    }

    // code to create default appearance of visual object
    @Override
    protected Appearance createAppearance() {
        return AppearanceBasket.defaultHostAppearance;
    }
}
