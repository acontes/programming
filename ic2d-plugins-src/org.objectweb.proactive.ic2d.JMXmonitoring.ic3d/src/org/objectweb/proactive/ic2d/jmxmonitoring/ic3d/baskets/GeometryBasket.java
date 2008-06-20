package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets;

import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.QuadArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;


/**
 * Every geometry is a singleton and geometry is shared between the figures that
 * use the geometry. Modifying the geometry object for one figure changes the
 * geometry of all the figures using that type of geometry. *
 * 
 * @author vjuresch
 * 
 */
public final class GeometryBasket {
    private static final Logger logger = Logger.getLogger(GeometryBasket.class.getName());

    /**
     * Contains an application wide value for the size of the figures.
     * It is needed for placing and scaling the figures. 
     */
    public static final float FIGURE_SCALE = 1f;
    public static final int EARTH_RADIUS = 150;
    //default geometries
    private static Geometry nodeGeometry = nodeGeometry();
    private static Geometry activeObjectGeometry = activeObjectGeometry();
    private static Geometry gridGeometry = gridGeometry();
    private static Geometry hostGeometry = hostGeometry();
    private static Geometry alternateHostGeometry = alternateHostGeometry();
    private static Geometry barMonitorGeometry = barMonitorGeometry();

    private static Geometry runtimeGeometry = runtimeGeometry();
    private static Geometry coordinatesGeometry = coordinatesGeometry();
    private static Geometry queueGeometry = queueGeometry();
    private static Geometry flatMap = flatMap();
    private static Geometry earthGridGeometry = earthGridGeometry();

    /** Private constructor as this class should never be instantiated */
    private GeometryBasket() {
    }

    private static Geometry nodeGeometry() {
        // -------------GEOMETRY POINTS--------------
        final Point3f a = new Point3f(0f, 0f, 0f);
        final Point3f b = new Point3f(0f, GeometryBasket.FIGURE_SCALE, 0f);
        final Point3f c = new Point3f(GeometryBasket.FIGURE_SCALE, GeometryBasket.FIGURE_SCALE, 0f);
        final Point3f d = new Point3f(GeometryBasket.FIGURE_SCALE, 0f, 0f);

        final Point3f a1 = new Point3f(0f, 0f, GeometryBasket.FIGURE_SCALE);
        final Point3f b1 = new Point3f(0f, GeometryBasket.FIGURE_SCALE, GeometryBasket.FIGURE_SCALE);
        final Point3f c1 = new Point3f(GeometryBasket.FIGURE_SCALE, GeometryBasket.FIGURE_SCALE,
            GeometryBasket.FIGURE_SCALE);
        final Point3f d1 = new Point3f(GeometryBasket.FIGURE_SCALE, 0f, GeometryBasket.FIGURE_SCALE);

        // ------------------------------------------
        final Point3f[] pts = new Point3f[24];
        // create the 6 face

        // front
        pts[0] = a;
        pts[1] = d;
        pts[2] = d1;
        pts[3] = a1;

        // back
        pts[4] = b;
        pts[5] = c;
        pts[6] = c1;
        pts[7] = b1;

        // bottom
        pts[8] = d1;
        pts[9] = c1;
        pts[10] = b1;
        pts[11] = a1;

        // top
        pts[12] = a;
        pts[13] = b;
        pts[14] = c;
        pts[15] = d;

        // left
        pts[16] = a;
        pts[17] = b;
        pts[18] = b1;
        pts[19] = a1;

        // right
        pts[20] = d;
        pts[21] = c;
        pts[22] = c1;
        pts[23] = d1;

        // say what points belong to the shape
        final int[] stripCount = new int[6];
        stripCount[0] = 4;
        stripCount[1] = 4;
        stripCount[2] = 4;
        stripCount[3] = 4;
        stripCount[4] = 4;
        stripCount[5] = 4;

        // say what the faces are
        final int[] contourCount = new int[6];
        contourCount[0] = 1;
        contourCount[1] = 1;
        contourCount[2] = 1;
        contourCount[3] = 1;
        contourCount[4] = 1;
        contourCount[5] = 1;

        // build the geometry
        final GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);

        // needed for lighting and shading the object
        final NormalGenerator normGen = new NormalGenerator();
        // the angle beyond which the normal generator will not smooth the
        // angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    private static Geometry flatMap() {
        float length = FIGURE_SCALE * 50;
        //-------------GEOMETRY POINTS--------------
        Point3f a = new Point3f(-length, 0f, -length);
        Point3f b = new Point3f(-length, 0f, length);
        Point3f c = new Point3f(length, 0f, length);
        Point3f d = new Point3f(length, 0f, -length);

        //------------------------------------------
        //create the points
        Point3f[] pts = new Point3f[4];
        //create the 6 face

        //front
        pts[0] = a;
        pts[1] = b;
        pts[2] = c;
        pts[3] = d;

        TexCoord2f[] coords = new TexCoord2f[4];
        coords[1] = new TexCoord2f(0f, 0f);
        coords[0] = new TexCoord2f(0f, 1f);
        coords[3] = new TexCoord2f(1f, 1f);
        coords[2] = new TexCoord2f(1f, 0f);

        QuadArray quad = new QuadArray(4, QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2);
        quad.setCoordinates(0, pts);
        quad.setTextureCoordinates(0, 0, coords);
        return quad;

    }

    private static Geometry queueGeometry() {
        //-------------GEOMETRY POINTS--------------
        Point3f a = new Point3f(0f, 0f, 0f);
        Point3f b = new Point3f(0f, FIGURE_SCALE, 0f);
        Point3f c = new Point3f(-FIGURE_SCALE, FIGURE_SCALE, 0f);
        Point3f d = new Point3f(-FIGURE_SCALE, 0f, 0f);

        final Point3f a1 = new Point3f(0f, 0f, GeometryBasket.FIGURE_SCALE);
        final Point3f b1 = new Point3f(0f, GeometryBasket.FIGURE_SCALE, GeometryBasket.FIGURE_SCALE);
        final Point3f c1 = new Point3f(-GeometryBasket.FIGURE_SCALE, GeometryBasket.FIGURE_SCALE,
            GeometryBasket.FIGURE_SCALE);
        final Point3f d1 = new Point3f(-GeometryBasket.FIGURE_SCALE, 0f, GeometryBasket.FIGURE_SCALE);

        // ------------------------------------------
        // create the points
        final Point3f[] pts = new Point3f[24];
        // create the 6 face

        // front
        pts[0] = a;
        pts[1] = d;
        pts[2] = d1;
        pts[3] = a1;

        // back
        pts[4] = b;
        pts[5] = c;
        pts[6] = c1;
        pts[7] = b1;

        // bottom
        pts[8] = d1;
        pts[9] = c1;
        pts[10] = b1;
        pts[11] = a1;

        // top
        pts[12] = a;
        pts[13] = b;
        pts[14] = c;
        pts[15] = d;

        // left
        pts[16] = a;
        pts[17] = b;
        pts[18] = b1;
        pts[19] = a1;

        // right
        pts[20] = d;
        pts[21] = c;
        pts[22] = c1;
        pts[23] = d1;

        // say what points belong to the shape
        final int[] stripCount = new int[6];
        stripCount[0] = 4;
        stripCount[1] = 4;
        stripCount[2] = 4;
        stripCount[3] = 4;
        stripCount[4] = 4;
        stripCount[5] = 4;

        // say what the faces are
        final int[] contourCount = new int[6];
        contourCount[0] = 1;
        contourCount[1] = 1;
        contourCount[2] = 1;
        contourCount[3] = 1;
        contourCount[4] = 1;
        contourCount[5] = 1;

        // build the geometry
        final GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);

        // needed for lighting and shading the object
        final NormalGenerator normGen = new NormalGenerator();
        // the angle beyond which the normal generator will not smooth the
        // angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    private static Geometry runtimeGeometry() {
        // -------------GEOMETRY POINTS--------------
        final Point3f a = new Point3f(0f, 0f, 0f);
        final Point3f b = new Point3f(0f, GeometryBasket.FIGURE_SCALE, 0f);
        final Point3f c = new Point3f(GeometryBasket.FIGURE_SCALE, GeometryBasket.FIGURE_SCALE, 0f);
        final Point3f d = new Point3f(GeometryBasket.FIGURE_SCALE, 0f, 0f);

        final Point3f a1 = new Point3f(0f, 0f, GeometryBasket.FIGURE_SCALE);
        final Point3f b1 = new Point3f(0f, GeometryBasket.FIGURE_SCALE, GeometryBasket.FIGURE_SCALE);
        final Point3f c1 = new Point3f(GeometryBasket.FIGURE_SCALE, GeometryBasket.FIGURE_SCALE,
            GeometryBasket.FIGURE_SCALE);
        final Point3f d1 = new Point3f(GeometryBasket.FIGURE_SCALE, 0f, GeometryBasket.FIGURE_SCALE);

        // ------------------------------------------
        // create the points
        final Point3f[] pts = new Point3f[24];
        // create the 6 face

        // front
        pts[0] = a;
        pts[1] = d;
        pts[2] = d1;
        pts[3] = a1;

        // back
        pts[4] = b;
        pts[5] = c;
        pts[6] = c1;
        pts[7] = b1;

        // bottom
        pts[8] = d1;
        pts[9] = c1;
        pts[10] = b1;
        pts[11] = a1;

        // top
        pts[12] = a;
        pts[13] = b;
        pts[14] = c;
        pts[15] = d;

        // left
        pts[16] = a;
        pts[17] = b;
        pts[18] = b1;
        pts[19] = a1;

        // right
        pts[20] = d;
        pts[21] = c;
        pts[22] = c1;
        pts[23] = d1;

        // say what points belong to the shape
        final int[] stripCount = new int[6];
        stripCount[0] = 4;
        stripCount[1] = 4;
        stripCount[2] = 4;
        stripCount[3] = 4;
        stripCount[4] = 4;
        stripCount[5] = 4;

        // say what the faces are
        final int[] contourCount = new int[6];
        contourCount[0] = 1;
        contourCount[1] = 1;
        contourCount[2] = 1;
        contourCount[3] = 1;
        contourCount[4] = 1;
        contourCount[5] = 1;

        // build the geometry
        final GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);

        // needed for lighting and shading the object
        final NormalGenerator normGen = new NormalGenerator();
        // the angle beyond which the normal generator will not smooth the
        // angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    private static Geometry hostGeometry() {
        // -------------GEOMETRY POINTS--------------
        final Point3f a = new Point3f(0f, 0f, 0f);
        final Point3f b = new Point3f(0f, 0f, GeometryBasket.FIGURE_SCALE);
        final Point3f c = new Point3f(GeometryBasket.FIGURE_SCALE, 0f, GeometryBasket.FIGURE_SCALE);
        final Point3f d = new Point3f(GeometryBasket.FIGURE_SCALE, 0f, 0f);

        // ------------------------------------------
        // create the points
        final Point3f[] pts = new Point3f[8];
        // create the 6 face

        // front
        pts[0] = a;
        pts[1] = b;
        pts[2] = c;
        pts[3] = d;

        // back
        pts[4] = d;
        pts[5] = c;
        pts[6] = b;
        pts[7] = a;

        // say what points belong to the shape
        final int[] stripCount = new int[2];
        stripCount[0] = 4;
        stripCount[1] = 4;

        // say what the faces are
        final int[] contourCount = new int[2];
        contourCount[0] = 1;
        contourCount[1] = 1;

        // build the geometry
        final GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);

        // needed for lighting and shading the object
        final NormalGenerator normGen = new NormalGenerator();
        // the angle beyond which the normal generator will not smooth the
        // angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    private static Geometry alternateHostGeometry() {
        // -------------GEOMETRY POINTS--------------

        final Point3f a = new Point3f(0f, 0f, 0f);
        final Point3f m1 = new Point3f(0.0f, 0f, GeometryBasket.FIGURE_SCALE / 2);
        final Point3f b = new Point3f(0f, 0f, GeometryBasket.FIGURE_SCALE);

        final Point3f m2 = new Point3f(GeometryBasket.FIGURE_SCALE, 0f, GeometryBasket.FIGURE_SCALE / 2);
        final Point3f c = new Point3f(GeometryBasket.FIGURE_SCALE, 0f, GeometryBasket.FIGURE_SCALE);
        final Point3f d = new Point3f(GeometryBasket.FIGURE_SCALE, 0f, 0f);

        final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
        final Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
        final Color3f[] myColors = { red, red, red, red, green, green, green, green, red, red, red, red,
                green, green, green, green };
        // ------------------------------------------
        // create the points
        final Point3f[] pts = new Point3f[16];
        // create the 6 face

        // Host used resource
        pts[0] = a;
        pts[1] = m1;
        pts[2] = m2;
        pts[3] = d;

        // Host free resources
        pts[4] = m1;
        pts[5] = b;
        pts[6] = c;
        pts[7] = m2;

        // back free resources
        pts[8] = d;
        pts[9] = m2;
        pts[10] = m1;
        pts[11] = a;

        // back used resource
        pts[12] = m2;
        pts[13] = c;
        pts[14] = b;
        pts[15] = m1;

        // say what points belong to the shape
        final int[] stripCount = new int[4];
        stripCount[0] = 4;
        stripCount[1] = 4;
        stripCount[2] = 4;
        stripCount[3] = 4;

        // say what the faces are
        final int[] contourCount = new int[4];
        contourCount[0] = 1;
        contourCount[1] = 1;
        contourCount[2] = 1;
        contourCount[3] = 1;

        // build the geometry
        final GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);
        geoInfo.setColors(myColors);

        // quite needed for lighting and shading the object
        final NormalGenerator normGen = new NormalGenerator();
        // the angle beyond which the normal generator will not smooth the
        // angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    private static Geometry barMonitorGeometry() {
        // -------------GEOMETRY POINTS--------------

        final Point3f a = new Point3f(0f, 0f, 0f);
        final Point3f b = new Point3f(0f, 0f, GeometryBasket.FIGURE_SCALE * 0.25f);
        final Point3f c = new Point3f(GeometryBasket.FIGURE_SCALE * 0.25f, 0f,
            GeometryBasket.FIGURE_SCALE * 0.25f);
        final Point3f d = new Point3f(GeometryBasket.FIGURE_SCALE * 0.25f, 0f, 0f);
        final Point3f e = new Point3f(a);
        e.y = e.y + GeometryBasket.FIGURE_SCALE * 2;
        final Point3f f = new Point3f(b);
        f.y = f.y + GeometryBasket.FIGURE_SCALE * 2;
        final Point3f g = new Point3f(c);
        g.y = g.y + GeometryBasket.FIGURE_SCALE * 2;
        final Point3f h = new Point3f(d);
        h.y = h.y + GeometryBasket.FIGURE_SCALE * 2;

        // ------------------------------------------
        // create the points
        final Point3f[] pts = { a, b, c, d, a, d, h, e, a, e, f, b, g, h, d, c, c, b, f, g, h, g, f, e // Top
        };

        // say what points belong to the shape
        final int[] stripCount = { 4, 4, 4, 4, 4, 4 };

        // say what the faces are
        final int[] contourCount = { 1, 1, 1, 1, 1, 1 };

        // build the geometry
        final GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);

        // quite needed for lighting and shading the object
        final NormalGenerator normGen = new NormalGenerator();
        // the angle beyond which the normal generator will not smooth the
        // angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    // not static because lines have different coordinates
    public static Geometry getDefaultLineGeometry(final Vector3f begin, final Vector3f end) {
        final Point3f startCoord = new Point3f(begin);
        final Point3f stopCoord = new Point3f(end);
        final Point3f[] pts = new Point3f[2];
        pts[0] = startCoord;
        pts[1] = stopCoord;

        final LineArray geoInfo = new LineArray(2, 1);
        geoInfo.setCoordinates(0, pts);

        // needed for lighting and shading the object
        final NormalGenerator normGen = new NormalGenerator();
        // the angle beyond which the normal generator will not smooth the
        // angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        return geoInfo;
    }

    // TODO does not work
    public static Geometry sphereArrowGeometry(final Tuple2d ao1, final Tuple2d ao2, int segments,
            final double radius) {
        /*
         * using polar coordinates system x = radius * sin PHI * cos THETA y =
         * radius * sin PHI * sin THETA z = radius * cos PHI
         * 
         * The three coordinates (radius,alpha, beta) are defined as: radius ???
         * 0 is the distance from the origin to a given point 0 alpha is the
         * angle between the positive z-axis and the line formed between the
         * origin and P. 0 beta < 2?? is the angle between the positive x-axis
         * and the line from the origin to the P projected onto the xy-plane.
         * 
         */
        segments = segments * 2; // we need a multiple of two
        final Point3d[] pts = new Point3d[segments];

        // TODO normalize to get into the range specified above

        // generate intermediate points
        double x;
        double y;
        double z;
        // distance in radians between the angles (coordinates)
        final double distance1 = (ao2.x - ao1.x) / segments;
        final double distance2 = (ao2.y - ao1.y) / segments;

        GeometryBasket.logger.trace("Distances:" + distance1 + ":" + distance2);

        GeometryBasket.logger.trace("ao1.x :" + ao1.x + " ao1.y :" + ao1.y + " ao2.x :" + ao2.x + " ao2.y :" +
            ao2.y);
        // calculate a segments number of points between
        // the angles of ao1, ao2
        for (int i = 0; i < segments; i++) {

            // get the coordinates in a plane
            x = Math.sin(distance1 * i + ao1.x) * Math.cos(distance2 * i + ao1.y) * radius;

            y = Math.sin(distance1 * i + ao1.x) * Math.sin(distance2 * i + ao1.y) * radius;

            // get the z coordinate
            z = Math.cos(distance1 * i + ao1.x) * radius;

            // make the x and y proportional

            // x = x*Math.cos(distance2 * i + ao1.y);
            // y = y*Math.cos(distance2 * i + ao1.y);

            pts[i] = new Point3d(x, y, z);
            System.out.println((distance1 * i + ao1.x) + ":" + (distance2 * i + ao1.y));
            // System.out.println(pts[i]);
        }

        // generate coordinates
        final LineArray geoInfo = new LineArray(pts.length, 1);
        geoInfo.setCoordinates(0, pts);

        // needed for lighting and shading the object
        // NormalGenerator normGen = new NormalGenerator();
        // //the angle beyond which the normal generator will not smooth the
        // angles
        // normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        return geoInfo;
    }

    // not static because lines have different coordinates
    public static Geometry getDefaultCurveGeometry(final Vector3f begin, final Vector3f end,
            final int segments) {
        final Point3f startCoord = new Point3f(begin);
        final Point3f stopCoord = new Point3f(end);
        final Point3f[] pts = new Point3f[segments + 1];

        final float length = ((startCoord.x - stopCoord.x) * (startCoord.x - stopCoord.x)) +
            ((startCoord.y - stopCoord.y) * (startCoord.y - stopCoord.y)) +
            ((startCoord.z - stopCoord.z) * (startCoord.x - stopCoord.z));

        double xf;
        double yf;
        double zf;
        double angle;

        pts[0] = startCoord;
        pts[segments] = stopCoord;

        for (int i = 1; i < segments; i++) {
            angle = length / segments / Math.sin((2 * Math.PI) / segments * i);
            xf = (angle * Math.abs(startCoord.x - stopCoord.x)) / length;
            yf = (angle * Math.abs(startCoord.y - stopCoord.y)) / length;
            zf = (angle * Math.abs(startCoord.z - stopCoord.z)) / length;
            pts[i] = new Point3f((float) xf, (float) yf, (float) zf);
            GeometryBasket.logger.trace("punct" + pts[i]);
        }

        final LineArray geoInfo = new LineArray(pts.length, 1);
        geoInfo.setCoordinates(0, pts);

        // needed for lighting and shading the object
        final NormalGenerator normGen = new NormalGenerator();
        // the angle beyond which the normal generator will not smooth the
        // angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        return geoInfo;
    }

    // not static because lines have different coordinates
    private static Geometry coordinatesGeometry() {
        final Point3f z1 = new Point3f(0, 400, 0);
        final Point3f z2 = new Point3f(0, -400, 0);

        final Point3f x1 = new Point3f(400, 0, 0);
        final Point3f x2 = new Point3f(-400, 0, 0);

        final Point3f y1 = new Point3f(0, 0, 400);
        final Point3f y2 = new Point3f(0, 0, -400);

        final Point3f v1 = new Point3f(5, 400, 0);
        final Point3f v2 = new Point3f(5, -400, 0);

        final Point3f v3 = new Point3f(0, 400, 5);
        final Point3f v4 = new Point3f(0, -400, 5);

        final Point3f v5 = new Point3f(-5, 400, 0);
        final Point3f v6 = new Point3f(-5, -400, 0);

        final Point3f v7 = new Point3f(0, 400, -5);
        final Point3f v8 = new Point3f(0, -400, -5);

        final Point3f[] pts = new Point3f[14];
        pts[0] = x1;
        pts[1] = x2;
        pts[2] = y1;
        pts[3] = y2;
        pts[4] = z1;
        pts[5] = z2;

        pts[6] = v1;
        pts[7] = v2;

        pts[8] = v3;
        pts[9] = v4;

        pts[10] = v5;
        pts[11] = v6;

        pts[12] = v7;
        pts[13] = v8;

        final LineArray geoInfo = new LineArray(14, 1);
        geoInfo.setCoordinates(0, pts);

        // needed for lighting and shading the object
        final NormalGenerator normGen = new NormalGenerator();
        // the angle beyond which the normal generator will not smooth the
        // angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        // generate the normals
        return geoInfo;
    }

    private static Geometry gridGeometry() {
        // TODO auto-generated
        return null;
    }

    private static Geometry activeObjectGeometry() {
        final Sphere s = new Sphere(1, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, 25);
        return s.getShape().getGeometry();
    }

    private static Geometry earthGridGeometry() {
        Sphere s = new Sphere(EARTH_RADIUS, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS,
            150);
        return s.getShape().getGeometry();
    }

    // ------------- PUBLIC METHODS ----------------
    public static Geometry getEarthGridGeometry() {
        return GeometryBasket.earthGridGeometry;
    }

    public static Geometry getDefaultQueueGeometry() {
        return GeometryBasket.queueGeometry;
    }

    public static Geometry getDefaultNodeGeometry() {
        return GeometryBasket.nodeGeometry;
    }

    public static Geometry getDefaultRuntimeGeometry() {
        return GeometryBasket.runtimeGeometry;
    }

    public static Geometry getDefaultHostGeometry() {
        return GeometryBasket.hostGeometry;
    }

    public static Geometry getDefaultActiveObjectGeometry() {
        return GeometryBasket.activeObjectGeometry;
    }

    public static Geometry getDefaultGridGeometry() {
        return GeometryBasket.gridGeometry;
    }

    public static Geometry getCoordinatesGeometry() {
        return GeometryBasket.coordinatesGeometry;
    }

    public static Geometry getAlternateHostGeometry() {
        return GeometryBasket.alternateHostGeometry;
    }

    public static Geometry getBarMonitorGeometry() {
        return GeometryBasket.barMonitorGeometry;
    }

    public static Geometry getFlatMap() {
        return GeometryBasket.flatMap;
    }
}
