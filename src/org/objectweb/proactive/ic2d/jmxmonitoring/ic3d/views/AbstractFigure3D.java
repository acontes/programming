package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.Font;
import java.util.Hashtable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;

import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.pickfast.behaviors.PickRotateBehavior;


public abstract class AbstractFigure3D extends Shape3D {
    private Hashtable<String, AbstractFigure3D> figures = new Hashtable<String, AbstractFigure3D>();
    private String name = new String();
    private Geometry voGeometry;
    private Appearance voAppearance;
    private Level logLevel = Level.OFF;
    private Logger logger = Logger.getLogger(
            "org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views");
    private static ConsoleHandler handler = new ConsoleHandler();
    private BranchGroup rootBranch;
    private BranchGroup figureBranch;
    private BranchGroup subFiguresBranch;

    public abstract void arrangeSubFigures();

    protected abstract Geometry createGeometry();

    public void setState(State state) {
        switch (state) {
        case UNKNOWN:
            setStateUnkown();
            break;
        }
    }

    /**
     * sets the figure appearance to unknown
     */
    public void setStateUnkown() {
        this.setAppearance(AppearanceBasket.defaultUnkownState);
    }

    protected abstract Appearance createAppearance();

    private RotationInterpolator rot;

    // ------- IMPLEMENTED METHODS --------

    // create Shape3D with geometry and appearance
    // the geometry is created in method createGeometry
    // the appearance is created in method createAppearance
    public AbstractFigure3D(String name) {
        logger.log(Level.FINE, "Setting appearance and geometry...");
        // initialize logger
        logger.setLevel(logLevel);
        handler.setLevel(logLevel);
        logger.addHandler(handler);
        // --------------- SHAPE CREATION ----------------
        // construct the geometry and appearance
        /*
         * change this part to change the look of the visual object
         */
        this.name = new String(name);
        voGeometry = createGeometry();
        voAppearance = createAppearance();
        this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        this.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        this.setCapability(Shape3D.ENABLE_PICK_REPORTING);

        this.setGeometry(voGeometry);
        this.setAppearance(voAppearance);
        this.setBounds(new BoundingSphere());
        /*
         * subFiguresBG -> generalBG figure -> rotTG -> generalTG -> figureBG ->
         * generalBG text -> rotTG
         */

        // create root branches
        rootBranch = createBranch();
        // create the BG group for the figure
        figureBranch = createBranch();
        // create the BG for the subfigures
        subFiguresBranch = createBranch();

        // connect
        rootBranch.addChild(subFiguresBranch);
        rootBranch.addChild(figureBranch);

        // and a transformgroup for rotation
        TransformGroup trans = createTransform();
        TransformGroup transRotate = createTransform();

        // add a transform group and the current figure
        trans.addChild(transRotate);
        // add text
        trans.addChild(createTextBranch());
        transRotate.addChild(this);

        figureBranch.addChild(trans);

        rootBranch.compile();
    }

    // creates a branch that contains the title of the figure
    // text2d -> TG -> ....
    protected TransformGroup createTextBranch() {
        // add the text
        Text2D figureName = new Text2D(getShortenedName(20),
                ColorPalette.BLACK, "Arial", 25, Font.PLAIN);

        // move a little the font shape
        TransformGroup fontTrans = createTransform();
        TransformGroup fontTranslate = createTransform();

        Transform3D rotate = new Transform3D();
        rotate.rotX(Math.PI / 4);
        fontTrans.setTransform(rotate);

        Transform3D translate = new Transform3D();
        translate.setTranslation(new Vector3d(0d, -0.1d, 0.1d));
        fontTranslate.setTransform(translate);

        fontTranslate.addChild(fontTrans);
        // TODO make text perpendicular on host
        fontTrans.addChild(figureName);

        // add billboard behaviour to the font transform
        // Billboard bill = new Billboard(fontTrans,
        // Billboard.ROTATE_ABOUT_AXIS,
        // new Vector3f(0f, 0f, 1f));
        // bill.setSchedulingBounds(new BoundingSphere());
        // fontTrans.addChild(bill);

        // add the transform the the branch group
        return fontTranslate;
    }

    public void removeAllSubFigures() {
        logger.log(Level.FINE, "Removing all subfigures...");
        while (!figures.isEmpty())
            removeSubFigure(figures.keys().nextElement());
    }

    public AbstractFigure3D getSubFigure(String key) {
        return figures.get(key);
    }

    public void removeSubFigure(String key) {
        removeSubFigure(figures.get(key));
    }

    public String getFigureName() {
        return new String(this.name);
    }

    public void setFigureName(String name) {
        logger.log(Level.FINE, "Changing figure name...");
        this.name = new String(name);
    }

    public Hashtable<String, AbstractFigure3D> getSubFigures() {
        return figures;
    }

    /*
     * Adds a figure and returns a reference to the branch group to which it is
     * attached *BG* -> TG -> Figure @param key @param figure
     */
    public void addSubFigure(String key, AbstractFigure3D figure) {
        logger.log(Level.FINE, "Adding a new figure:" + key);
        // get the main branch group of the figure
        // figure ->rotTG ->generalTG-> BG -> BG (from the constructor)
        BranchGroup root = (BranchGroup) figure.getRootBranch();

        // add the figure
        root.compile();
        figureBranch.addChild(root);
        figures.put(key, figure);
        arrangeSubFigures();
    }

    protected BranchGroup createBranch() {
        // create the general runtime branch
        BranchGroup branch = new BranchGroup();
        // set the capabilities (detach, add)
        branch.setCapability(BranchGroup.ALLOW_DETACH);
        branch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        branch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        branch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        branch.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
        branch.setCapability(BranchGroup.ALLOW_BOUNDS_READ);

        branch.setCapability(BranchGroup.ALLOW_PARENT_READ);
        return branch;
    }

    public void placeSubFigure(AbstractFigure3D figure, double x, double y,
        double z) {
        // get the parent transform
        TransformGroup trans = (TransformGroup) figure.getParent().getParent();
        Transform3D translate = new Transform3D();
        translate.setTranslation(new Vector3d(x, y, z));
        trans.setTransform(translate);
    }

    protected TransformGroup createTransform() {
        TransformGroup trans = new TransformGroup();
        // set the transform group capabilities
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        trans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        trans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        return trans;
    }

    public void removeSubFigure(AbstractFigure3D figure) {
        logger.log(Level.FINE, "Removing figure..." + figure.toString());

        // get the branch: figure ->rotTG -> TG->particularBG -> *figures BG*
        BranchGroup parentBranch = (BranchGroup) figure.getRootBranch()
                                                       .getParent();
        logger.log(Level.FINE,
            "Got parent of the root branch..." + parentBranch.toString());
        // get the branch: figure ->rotTG -> TG->*particularBG* -> hosts BG
        BranchGroup rootBranch = (BranchGroup) figure.getRootBranch();
        logger.log(Level.FINE, "Got the root branch..." +
            rootBranch.toString());

        // tell the subfigure to remove all the subsubfigures
        figure.removeAllSubFigures();
        rootBranch.detach();
        parentBranch.removeChild(rootBranch);
        // remove from the hashtable
        System.out.println("Figure has been removed:" +
            figures.values().remove(figure));
        arrangeSubFigures();
    }

    public void setText(String text) {
    }

    // returns the highest BranchGroup of this figure
    public BranchGroup getRootBranch() {
        return rootBranch;
    }

    // returns the branchgroup immediate bellow the root branch
    public BranchGroup getParticularBranch() {
        return figureBranch;
    }

    public String getShortenedName(int characters) {
        // return the first 10 or less characters
        return new String(name.substring(0, Math.min(characters, name.length())));
    }
}
