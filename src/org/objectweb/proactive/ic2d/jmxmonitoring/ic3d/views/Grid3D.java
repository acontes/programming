/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.KeyGenerator;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.LinearFog;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.behaviors.picking.PickTranslateBehavior;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.universe.ConfiguredUniverse;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewerAvatar;
import com.sun.j3d.utils.universe.ViewingPlatform;

import sun.security.x509.UniqueIdentity;


/**
 * @author vjuresch
 *
 */
public class Grid3D extends AbstractGrid3D {
    private Hashtable<String, AbstractArrow3D> arrows = new Hashtable<String, AbstractArrow3D>();

    // needed to schedule arrow deletion
    private Timer arrowTimer = new Timer();

    public Grid3D() {
        super("");
    }

    /*
     * draws an arrow from object start to stop and removes it after timeToLive
     * milliseconds
     *
     */
    public void drawArrow(final String key, String name, long timeToLive,
        AbstractActiveObject3D startAO, AbstractActiveObject3D stopAO) {
        Transform3D start = new Transform3D();
        Transform3D stop = new Transform3D();
        // got the coordinates of start and stop
        startAO.getLocalToVworld(start);
        stopAO.getLocalToVworld(stop);
        Vector3f begin = new Vector3f();
        Vector3f end = new Vector3f();
        start.get(begin);
        stop.get(end);
        // create a line between
        Arrow3D arrow = new Arrow3D(name, begin, end);
        // arrow ->rotTG->TG ->particular BG -> generalBG
        getRootBranch().addChild(arrow.getRootBranch());

        // add to list of arrows
        arrows.put(key, arrow);
        // set destruction time
        TimerTask arrowDestruction = new TimerTask() {
                @Override
                public void run() {
                    removeArrow(key);
                }
            };

        // TODO remove constant
        arrowTimer.schedule(arrowDestruction, timeToLive);
    }

    private void removeArrow(String key) {
        // arrow ->rotTG->TG ->particular BG -> generalBG
        BranchGroup bg = (BranchGroup) arrows.get(key).getRootBranch();
        // detach brach group
        bg.detach();
        // remove from list of arrows
        arrows.remove(key);
    }

    // add a new host and translate its
    // position according to the coordinates
    // (add to a custom location)

    // TODO ***HORRIBLE PLACING ALGORITHM*** MUST BE CHANGED !!! the cost is n^2
    // * method calls in depth
    // for each host or removed placed, the entire scene gets rearranged
    public void arrangeSubFigures() {
        int i = 2;
        for (AbstractFigure3D host : this.getSubFigures().values()) {
            PlacementBasket.spiralArrangement(i, host);
            i++;
            host.arrangeSubFigures();
        }
    }

    @Override
    protected Appearance createAppearance() {
        return AppearanceBasket.coordinatesAppearance;
    }

    @Override
    protected Geometry createGeometry() {
        // return GeometryBasket.getCoordinatesGeometry();
        return null;
    }
}
