package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;


/**
 * Every geometry is a singleton and
 * geometry is shared between the
 * figures that use the geometry. Modifying the geometry
 * object for one figure changes the geometry of
 * all the figures using that type of geometry. 
 *
 * * @author vjuresch
 *
 */
public class GeometryBasket {

    /**
     * Contains an application wide value for the size of the figures.
     * It is needed for placing and scaling the figures. 
     */
    public static final float FIGURE_SCALE = 1f;
    //default geometries
    private static Geometry nodeGeometry = nodeGeometry();
    private static Geometry activeObjectGeometry = activeObjectGeometry();
    private static Geometry gridGeometry = gridGeometry();
    private static Geometry hostGeometry = hostGeometry();
    private static Geometry runtimeGeometry = runtimeGeometry();
    private static Geometry coordinatesGeometry = coordinatesGeometry();
    private static Geometry queueGeometry = queueGeometry();

    //earth view geometries
    private static Geometry earthGridGeometry = earthGridGeometry();

    private static Geometry nodeGeometry() {
        //-------------GEOMETRY POINTS--------------
        Point3f a = new Point3f(0f, 0f, 0f);
        Point3f b = new Point3f(0f, 0f, FIGURE_SCALE);
        Point3f c = new Point3f(FIGURE_SCALE, 0f, FIGURE_SCALE);
        Point3f d = new Point3f(FIGURE_SCALE, 0f, 0f);

        Point3f a1 = new Point3f(0f, FIGURE_SCALE, 0f);
        Point3f b1 = new Point3f(0f, FIGURE_SCALE, FIGURE_SCALE);
        Point3f c1 = new Point3f(FIGURE_SCALE, FIGURE_SCALE, FIGURE_SCALE);
        Point3f d1 = new Point3f(FIGURE_SCALE, FIGURE_SCALE, 0f);

        //------------------------------------------
        Point3f[] pts = new Point3f[24];
        //create the 6 face

        //front
        pts[0] = a;
        pts[1] = d;
        pts[2] = d1;
        pts[3] = a1;

        //back
        pts[4] = b;
        pts[5] = c;
        pts[6] = c1;
        pts[7] = b1;

        //bottom
        pts[8] = d1;
        pts[9] = c1;
        pts[10] = b1;
        pts[11] = a1;

        //top
        pts[12] = a;
        pts[13] = b;
        pts[14] = c;
        pts[15] = d;

        //left
        pts[16] = a;
        pts[17] = b;
        pts[18] = b1;
        pts[19] = a1;

        //right
        pts[20] = d;
        pts[21] = c;
        pts[22] = c1;
        pts[23] = d1;

        //say what points belong to the shape
        int[] stripCount = new int[6];
        stripCount[0] = 4;
        stripCount[1] = 4;
        stripCount[2] = 4;
        stripCount[3] = 4;
        stripCount[4] = 4;
        stripCount[5] = 4;

        //say what the faces are
        int[] contourCount = new int[6];
        contourCount[0] = 1;
        contourCount[1] = 1;
        contourCount[2] = 1;
        contourCount[3] = 1;
        contourCount[4] = 1;
        contourCount[5] = 1;

        //build the geometry
        GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);

        //needed for lighting and shading the object
        NormalGenerator normGen = new NormalGenerator();
        //the angle beyond which the normal generator will not smooth the angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        //generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    private static Geometry queueGeometry() {
        //-------------GEOMETRY POINTS--------------
        Point3f a = new Point3f(0f, 0f, 0f);
        Point3f b = new Point3f(0f, 0f, FIGURE_SCALE);
        Point3f c = new Point3f(-FIGURE_SCALE, 0f, FIGURE_SCALE);
        Point3f d = new Point3f(-FIGURE_SCALE, 0f, 0f);

        Point3f a1 = new Point3f(0f, FIGURE_SCALE, 0f);
        Point3f b1 = new Point3f(0f, FIGURE_SCALE, FIGURE_SCALE);
        Point3f c1 = new Point3f(-FIGURE_SCALE, FIGURE_SCALE, FIGURE_SCALE);
        Point3f d1 = new Point3f(-FIGURE_SCALE, FIGURE_SCALE, 0f);

        //------------------------------------------
        //create the points
        Point3f[] pts = new Point3f[24];
        //create the 6 face

        //front
        pts[0] = a;
        pts[1] = d;
        pts[2] = d1;
        pts[3] = a1;

        //back
        pts[4] = b;
        pts[5] = c;
        pts[6] = c1;
        pts[7] = b1;

        //bottom
        pts[8] = d1;
        pts[9] = c1;
        pts[10] = b1;
        pts[11] = a1;

        //top
        pts[12] = a;
        pts[13] = b;
        pts[14] = c;
        pts[15] = d;

        //left
        pts[16] = a;
        pts[17] = b;
        pts[18] = b1;
        pts[19] = a1;

        //right
        pts[20] = d;
        pts[21] = c;
        pts[22] = c1;
        pts[23] = d1;

        //say what points belong to the shape
        int[] stripCount = new int[6];
        stripCount[0] = 4;
        stripCount[1] = 4;
        stripCount[2] = 4;
        stripCount[3] = 4;
        stripCount[4] = 4;
        stripCount[5] = 4;

        //say what the faces are
        int[] contourCount = new int[6];
        contourCount[0] = 1;
        contourCount[1] = 1;
        contourCount[2] = 1;
        contourCount[3] = 1;
        contourCount[4] = 1;
        contourCount[5] = 1;

        //build the geometry
        GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);

        //needed for lighting and shading the object
        NormalGenerator normGen = new NormalGenerator();
        //the angle beyond which the normal generator will not smooth the angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        //generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    private static Geometry runtimeGeometry() {
        //-------------GEOMETRY POINTS--------------
        Point3f a = new Point3f(0f, 0f, 0f);
        Point3f b = new Point3f(0f, 0f, FIGURE_SCALE);
        Point3f c = new Point3f(FIGURE_SCALE, 0f, FIGURE_SCALE);
        Point3f d = new Point3f(FIGURE_SCALE, 0f, 0f);

        Point3f a1 = new Point3f(0f, FIGURE_SCALE, 0f);
        Point3f b1 = new Point3f(0f, FIGURE_SCALE, FIGURE_SCALE);
        Point3f c1 = new Point3f(FIGURE_SCALE, FIGURE_SCALE, FIGURE_SCALE);
        Point3f d1 = new Point3f(FIGURE_SCALE, FIGURE_SCALE, 0f);

        //------------------------------------------
        //create the points
        Point3f[] pts = new Point3f[24];
        //create the 6 face

        //front
        pts[0] = a;
        pts[1] = d;
        pts[2] = d1;
        pts[3] = a1;

        //back
        pts[4] = b;
        pts[5] = c;
        pts[6] = c1;
        pts[7] = b1;

        //bottom
        pts[8] = d1;
        pts[9] = c1;
        pts[10] = b1;
        pts[11] = a1;

        //top
        pts[12] = a;
        pts[13] = b;
        pts[14] = c;
        pts[15] = d;

        //left
        pts[16] = a;
        pts[17] = b;
        pts[18] = b1;
        pts[19] = a1;

        //right
        pts[20] = d;
        pts[21] = c;
        pts[22] = c1;
        pts[23] = d1;

        //say what points belong to the shape
        int[] stripCount = new int[6];
        stripCount[0] = 4;
        stripCount[1] = 4;
        stripCount[2] = 4;
        stripCount[3] = 4;
        stripCount[4] = 4;
        stripCount[5] = 4;

        //say what the faces are
        int[] contourCount = new int[6];
        contourCount[0] = 1;
        contourCount[1] = 1;
        contourCount[2] = 1;
        contourCount[3] = 1;
        contourCount[4] = 1;
        contourCount[5] = 1;

        //build the geometry
        GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);

        //needed for lighting and shading the object
        NormalGenerator normGen = new NormalGenerator();
        //the angle beyond which the normal generator will not smooth the angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        //generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    private static Geometry hostGeometry() {
        //-------------GEOMETRY POINTS--------------
        Point3f a = new Point3f(0f, 0f, 0f);
        Point3f b = new Point3f(0f, FIGURE_SCALE, 0f);
        Point3f c = new Point3f(FIGURE_SCALE, FIGURE_SCALE, 0f);
        Point3f d = new Point3f(FIGURE_SCALE, 0f, 0f);

        //------------------------------------------
        //create the points
        Point3f[] pts = new Point3f[8];
        //create the 6 face

        //front
        pts[0] = a;
        pts[1] = b;
        pts[2] = c;
        pts[3] = d;

        //back
        pts[4] = d;
        pts[5] = c;
        pts[6] = b;
        pts[7] = a;

        //say what points belong to the shape
        int[] stripCount = new int[2];
        stripCount[0] = 4;
        stripCount[1] = 4;

        //say what the faces are
        int[] contourCount = new int[2];
        contourCount[0] = 1;
        contourCount[1] = 1;

        //build the geometry
        GeometryInfo geoInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geoInfo.setCoordinates(pts);
        geoInfo.setStripCounts(stripCount);
        geoInfo.setContourCounts(contourCount);

        //needed for lighting and shading the object
        NormalGenerator normGen = new NormalGenerator();
        //the angle beyond which the normal generator will not smooth the angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        //generate the normals
        normGen.generateNormals(geoInfo);
        return geoInfo.getGeometryArray();
    }

    //not static because lines have different coordinates
    public static Geometry getDefaultLineGeometry(Vector3f begin, Vector3f end) {
        Point3f startCoord = new Point3f(begin);
        Point3f stopCoord = new Point3f(end);
        Point3f[] pts = new Point3f[2];
        pts[0] = startCoord;
        pts[1] = stopCoord;

        LineArray geoInfo = new LineArray(2, 1);
        geoInfo.setCoordinates(0, pts);

        //needed for lighting and shading the object
        NormalGenerator normGen = new NormalGenerator();
        //the angle beyond which the normal generator will not smooth the angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        //generate the normals
        return geoInfo;
    }

    //TODO does not work
    public static Geometry sphereArrowGeometry(Tuple2d ao1, Tuple2d ao2, int segments, double radius) {
        /*
        using polar coordinates system
        x = radius * sin PHI * cos THETA
        y = radius * sin PHI * sin THETA
        z = radius * cos PHI

        The three coordinates (radius,alpha, beta) are defined as:
        radius ??? 0 is the distance from the origin to a given point
        0 alpha is the angle between the positive
                z-axis and the line formed between the origin and P.
        0 beta < 2?? is the angle between the positive
                x-axis and the line from the origin to the P
                projected onto the xy-plane.

         */
        segments = segments * 2; //we need a multiple of two
        Point3d[] pts = new Point3d[segments];

        //TODO normalize  to get into the range specified above

        //generate intermediate points
        double x;
        double y;
        double z;
        //distance in radians between the angles (coordinates)
        double distance1 = (ao2.x - ao1.x) / segments;
        double distance2 = (ao2.y - ao1.y) / segments;

        System.out.println("Distances:" + distance1 + ":" + distance2);

        System.out.println("ao1.x :" + ao1.x + " ao1.y :" + ao1.y + " ao2.x :" + ao2.x + " ao2.y :" + ao2.y);
        //calculate a segments number of points between
        //the angles of ao1, ao2
        for (int i = 0; i < segments; i++) {

            //get the coordinates in a plane
            x = Math.sin(distance1 * i + ao1.x) * Math.cos(distance2 * i + ao1.y) * radius;

            y = Math.sin(distance1 * i + ao1.x) * Math.sin(distance2 * i + ao1.y) * radius;

            //get the z coordinate
            z = Math.cos(distance1 * i + ao1.x) * radius;

            //make the x and y proportional

            //                      x = x*Math.cos(distance2 * i  + ao1.y);
            //                      y = y*Math.cos(distance2 * i  + ao1.y);

            pts[i] = new Point3d(x, y, z);
            System.out.println((distance1 * i + ao1.x) + ":" + (distance2 * i + ao1.y));
            //       System.out.println(pts[i]);
        }

        //generate coordinates
        LineArray geoInfo = new LineArray(pts.length, 1);
        geoInfo.setCoordinates(0, pts);

        //needed for lighting and shading the object
        //                NormalGenerator normGen = new NormalGenerator();
        //              //the angle beyond which the normal generator will not smooth the angles
        //              normGen.setCreaseAngle((float) Math.toRadians(90));
        //generate the normals
        return geoInfo;
    }

    //not static because lines have different coordinates
    public static Geometry getDefaultCurveGeometry(Vector3f begin, Vector3f end, int segments) {
        Point3f startCoord = new Point3f(begin);
        Point3f stopCoord = new Point3f(end);
        Point3f[] pts = new Point3f[segments + 1];

        float length = ((startCoord.x - stopCoord.x) * (startCoord.x - stopCoord.x)) +
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
            System.out.println("punct" + pts[i]);
        }

        LineArray geoInfo = new LineArray(pts.length, 1);
        geoInfo.setCoordinates(0, pts);

        //needed for lighting and shading the object
        NormalGenerator normGen = new NormalGenerator();
        //the angle beyond which the normal generator will not smooth the angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        //generate the normals
        return geoInfo;
    }

    //not static because lines have different coordinates
    private static Geometry coordinatesGeometry() {
        Point3f z1 = new Point3f(0, 0, 400);
        Point3f z2 = new Point3f(0, 0, -400);

        Point3f x1 = new Point3f(400, 0, 0);
        Point3f x2 = new Point3f(-400, 0, 0);

        Point3f y1 = new Point3f(0, 400, 0);
        Point3f y2 = new Point3f(0, -400, 0);

        Point3f v1 = new Point3f(5, 0, 400);
        Point3f v2 = new Point3f(5, 0, -400);

        Point3f v3 = new Point3f(0, 5, 400);
        Point3f v4 = new Point3f(0, 5, -400);

        Point3f v5 = new Point3f(-5, 0, 400);
        Point3f v6 = new Point3f(-5, 0, -400);

        Point3f v7 = new Point3f(0, -5, 400);
        Point3f v8 = new Point3f(0, -5, -400);

        Point3f[] pts = new Point3f[14];
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

        LineArray geoInfo = new LineArray(14, 1);
        geoInfo.setCoordinates(0, pts);

        //needed for lighting and shading the object
        NormalGenerator normGen = new NormalGenerator();
        //the angle beyond which the normal generator will not smooth the angles
        normGen.setCreaseAngle((float) Math.toRadians(90));
        //generate the normals
        return geoInfo;
    }

    private static Geometry gridGeometry() {
        //TODO auto-generated
        return null;
    }

    private static Geometry activeObjectGeometry() {
        Sphere s = new Sphere(1, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, 25);
        return s.getShape().getGeometry();
    }

    private static Geometry earthGridGeometry() {
        Sphere s = new Sphere(15, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, 150);
        return s.getShape().getGeometry();
    }

    //------------- PUBLIC METHODS ----------------
    public static Geometry getEarthGridGeometry() {
        return earthGridGeometry;
    }

    public static Geometry getDefaultQueueGeometry() {
        return queueGeometry;
    }

    public static Geometry getDefaultNodeGeometry() {
        return nodeGeometry;
    }

    public static Geometry getDefaultRuntimeGeometry() {
        return runtimeGeometry;
    }

    public static Geometry getDefaultHostGeometry() {
        return hostGeometry;
    }

    public static Geometry getDefaultActiveObjectGeometry() {
        return activeObjectGeometry;
    }

    public static Geometry getDefaultGridGeometry() {
        return gridGeometry;
    }

    public static Geometry getCoordinatesGeometry() {
        return coordinatesGeometry;
    }
}
