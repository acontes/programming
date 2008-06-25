package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;


/**
 * This class is the parent of all figures. It implements some basic actions
 * like remove subfigures, add subfigures, branch creation and transform
 * creation.
 * 
 * @author vjuresch
 * 
 */
public abstract class AbstractFigure3D extends Shape3D implements Figure3D, IObservable {

    private final static Logger logger = Logger.getLogger(AbstractFigure3D.class.getName());

    /**
     * Contains the list of subFigures for this figure.
     */
    private final Hashtable<String, Figure3D> figures = new Hashtable<String, Figure3D>();
    /**
     * Arrows for this figure.
     */
    private final Hashtable<String, Figure3D> arrows = new Hashtable<String, Figure3D>();
    /**
     * Timer for the threads specified to run as a daemon so as not to block the
     * application
     */
    private final Timer arrowTimer = new Timer();
    /**
     * This name will be displayed according to the formatting chosen through
     * createTextBranch() from the TextStyleBasket class
     */
    private String name = "";
    private final Geometry voGeometry; // geometry of the figure
    private final Appearance voAppearance; // appearance of the figure
    /**
     * The figureBranch and the subFiguresBranch connect to the rootBranch.
     */
    private final BranchGroup rootBranch;
    /**
     * The figureBranch connects to the rootBranch and has the translation and
     * scale transform group connected to it.
     */
    private final BranchGroup figureBranch;
    /**
     * The subFiguresBranch contains the subfigures of this figure.
     */
    private final BranchGroup subFiguresBranch;

    private final TransformGroup translateScaleTrans;

    private final TransformGroup rotTrans;
    
    // TODO remove this when creating LOD
    // replace the extends of this class to observable
    private HashSet<Observer> observers;

    /**
     * When implemented this abstract method should arrange the subfigures
     * attached to this figure.
     */
    public abstract void arrangeSubFigures();

    /**
     * This method sets the geometry of the figure. All geometries are stored in
     * GeometryBasket and can be accessed statically. If one type of geometry is
     * assigned to the figure it is shared with all the figure having the
     * geometry. This means that changing the Geometry object in of the figures
     * changes it in all.
     * 
     * @return geometry of the figure
     */
    protected abstract Geometry createGeometry();

    /**
     * This method sets the state of the object. This should be implemented as a
     * change of appearance or geometry. Takes as a parameter an enumeration
     * State defined in org.objectweb.proactive.ic2d.jmxmonitoring.util.State
     * 
     * @param state
     *            a Enum defined in
     *            {@link org.objectweb.proactive.ic2d.jmxmonitoring.util.State}
     *            The possible states(for now) are
     *            <ul>
     *            <li> UNKNOWN</li>
     *            <li>MIGRATING</li>
     *            <li>NOT_RESPONDING</li>
     *            <li>HIGHLIGHTED</li>
     *            <li>NOT_HIGHLIGHTED</li>
     *            <li>NOT_MONITORED</li>
     *            <li>SERVING_REQUEST</li>
     *            <li>WAITING_BY_NECESSITY</li>
     *            <li>WAITING_BY_NECESSITY_WHILE_ACTIVE</li>
     *            <li>WAITING_BY_NECESSITY_WHILE_SERVING</li>
     *            <li>RECEIVED_FUTURE_RESULT</li>
     *            <li>ACTIVE</li>
     *            <li>WAITING_FOR_REQUEST</li>
     *            </ul>
     * 
     * This is the only part that connects this class directly to IC2D. For this
     * reason it is possible that it may be removed or made more generic.
     * 
     * The implementations of AbstractFigure3D should implement different
     * appearances for different states.
     */
    public void setState(final State state) {
        switch (state) {
            case UNKNOWN:
                this.setStateUnkown();
                break;
        }
    }

    /**
     * Sets the figure's appearance to unknown. All appearances should be taken
     * from the class AppearanceBasket.
     */
    public void setStateUnkown() {
        this.setAppearance(AppearanceBasket.defaultUnkownStateAppearance);
    }

    /**
     * This method sets the appearance of the figure. All appearances are stored
     * in AppearanceBasket and can be accessed statically.
     */
    protected abstract Appearance createAppearance();

    // private RotationInterpolator rot;

    /**
     * The constructor creates the Shape3D with the geometry and appearance from
     * the method createGeometry and createAppearance. subFiguresBG -> generalBG
     * Each figure is actual a small tree composed of a few transform groups,
     * branch groups and the actual shape 3D.
     * 
     * <pre>
     *  -&gt; = child - parent
     * 
     * The structure is:
     * 
     * actual figure -&gt; rotTG -&gt; generalTG -&gt; figureBG -&gt; generalBG textNode -&gt;
     * rotTG subFigures BG -&gt; generalBG
     * 
     * </pre>
     * 
     * Each subfigure has a similar structure with its generalBG connected to
     * the subFiguresBG of the parent figure. The highest generalBG connects to
     * the locale of the universe. The several BG levels are necessary for
     * detaching and attaching subfigures.
     * 
     * 
     * @param name
     *            The figure name to be displayed.
     */
    public AbstractFigure3D(final String name) {
        AbstractFigure3D.logger.debug("Starting figure creation...");
        // logger.log(Level.FINE, "Setting appearance and geometry...");
        // initialize logger
        // loggersetLevel(logLevel);
        // handler.setLevel(logLevel);
        // loggeraddHandler(handler);
        // set the name
        this.name = name;
        /*
         * set the capabilities for this shape to allow later change of
         * appearance, geometry, picking, etc.
         */
        this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        this.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        this.setCapability(Node.ALLOW_PICKABLE_READ);
        this.setCapability(Node.ALLOW_PICKABLE_WRITE);
        this.setPickable(true);

        // create geometry and appearance
        this.voGeometry = this.createGeometry();
        this.voAppearance = this.createAppearance();
        // set the geometry and appearance
        this.setGeometry(this.voGeometry);
        this.setAppearance(this.voAppearance);
        // set the bounds for the figure - useful for picking
        // TODO is this correct ?
        this.setBounds(new BoundingSphere());
        /**
         * subFiguresBG -> generalBG Each figure is actual a small tree composed
         * of a few transform groups, branch groups and the actual shape. -> =
         * child - parent
         * 
         * The structure is:
         * 
         * <pre>
         * actual figure -&gt; rotTG -&gt; generalTG -&gt; figureBG -&gt; rootBG
         * 		textNode -&gt; rotTG
         *  								subFigures BG -&gt; rootBG
         * </pre>
         * 
         * Each subfigure has a similar structure with its generalBG connected
         * to the subFiguresBG of the parent figure. The highest generalBG
         * connects to the locale of the universe. The several BG levels are
         * necessary for detaching and attaching subfigures.
         */
        // create root branch
        this.rootBranch = this.createBranch();
        // create the BG group for the figure
        this.figureBranch = this.createBranch();
        // create the BG for the subfigures
        this.subFiguresBranch = this.createBranch();
        // connect

        // rootBranch.addChild(subFiguresBranch);

        this.rootBranch.addChild(this.figureBranch);
        this.translateScaleTrans = this.createTransform();
        this.rotTrans = this.createTransform();
        // connect the rotation transform to the translation transform
        this.translateScaleTrans.addChild(this.rotTrans);
        // add the text to the rotation Transform
        this.rotTrans.addChild(this.createTextBranch());
        // add this figure to the rotation transform
        this.rotTrans.addChild(this);

        // TODO maybe change like this, needs to be checked
        this.rotTrans.addChild(this.subFiguresBranch);

        // connect the transforms to the figure branch
        this.figureBranch.addChild(this.translateScaleTrans);
        // optimize
        this.rootBranch.compile();
        AbstractFigure3D.logger.debug("Figure created");
        
        // Create the observer set
        // TODO remove this while implementing LOD
        this.observers = new HashSet<Observer>();
    }

    /**
     * This method creates the text branch and sets the appearance of the text.
     * All text styles are stored in TextStyleBasket and can be accessed
     * statically.
     */
    protected abstract TransformGroup createTextBranch();

    /**
     * Removes all the subfigure for this figure. This method calls
     * removeSubFigure(AbstractFigure3D figure) for each subfigure.
     */
    public void removeAllSubFigures() {
        AbstractFigure3D.logger.debug("Removing all subfigures...");
        while (!this.figures.isEmpty()) {
            this.removeSubFigure(this.figures.keys().nextElement());
        }
    }

    /**
     * This method returns a subfigure from the list of subfigures.
     * 
     * @param key
     *            the key of the subfigure
     * 
     * Keys are not properties of the figures but of the list contained in the
     * parent figure.
     * 
     * @return the AbstractFigure3D with the corresponding key
     */
    public Figure3D getSubFigure(final String key) {
        return this.figures.get(key);
    }

    /**
     * Removes a subfigure by key. This method calls
     * removeSubFigure(AbstractFigure3D figure).
     * 
     * @param key
     */
    public void removeSubFigure(final String key) {
        this.removeSubFigure(this.figures.get(key));
    }

    /**
     * @return a String containing the figure name
     */
    public String getFigureName() {
        return this.name;
    }

    /**
     * Sets the figure name.
     * 
     * @param name
     */
    // TODO also update the text branch for the figure
    public void setFigureName(final String name) {
        // loggerlog(Level.FINE, "Changing figure name...");
        this.name = name;
    }

    /**
     * @return a Hashtable containing the subfigures
     */
    public Hashtable<String, Figure3D> getSubFigures() {
        return new Hashtable<String, Figure3D>(this.figures);
    }

    /**
     * Adds a subfigure to the subFigures BranchGroup.
     * 
     * @param key
     *            the key of the figure (should be unique)
     * @param figure
     *            the Abstract3DFigure to be added
     */
    public void addSubFigure(final String key, final Figure3D figure) {
        // loggerlog(Level.FINE, "Adding a new figure:" + key);
        // gets the main branch group of the subFigure
        // and it adds it to the subFigures BranchGroup

        // figure ->rotTG ->generalTG-> particularBG -> rootBG (from the
        // constructor)
        final BranchGroup root = figure.getRootBranch();

        root.compile();
        // add the figure
        this.subFiguresBranch.addChild(root);
        this.figures.put(key, figure);
        // update the view
        this.arrangeSubFigures();
        // animate the creation
        figure.animateCreation();
    }

    /**
     * Method that animates the creation of a figure by changing the geometry or
     * the appearance. The animations should be found in the class
     * AnimationBasket.
     */
    public abstract void animateCreation();

    /**
     * Utility method for creating a BranchGroup
     * 
     * @return a BranchGroup with the needed capabilities set
     */
    private BranchGroup createBranch() {
        // create the general runtime branch
        final BranchGroup branch = new BranchGroup();
        // set the capabilities (detach, add)
        branch.setCapability(BranchGroup.ALLOW_DETACH);
        branch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        branch.setCapability(Group.ALLOW_CHILDREN_READ);
        branch.setCapability(Group.ALLOW_CHILDREN_WRITE);
        branch.setCapability(Node.ENABLE_PICK_REPORTING);
        branch.setCapability(Node.ALLOW_BOUNDS_READ);

        branch.setCapability(Node.ALLOW_PARENT_READ);
        return branch;
    }

    /**
     * Places an AbstractFigure3D at the x,y,z coordinates in the universe. For
     * now it is used by the PlacementBasket class.
     * 
     * @param figure
     * @param x
     * @param y
     * @param z
     */
    // TODO pass a Transform3D instead of x.y.x coordinates so
    // rotation can be also applied
    public void placeSubFigure(final Figure3D figure, final double x, final double y, final double z) {
        // get the parent transform
        final TransformGroup trans = figure.getTranslateScaleTransform();
        final Transform3D translate = new Transform3D();
        translate.setTranslation(new Vector3d(x, y, z));
        trans.setTransform(translate);
    }

    /**
     * Utility method for creating a TransformGroup with the correct
     * capabilities set
     * 
     * @return a TransformGroup with the needed capabilities set
     */
    private TransformGroup createTransform() {
        final TransformGroup trans = new TransformGroup();
        // set the transform group capabilities
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans.setCapability(Group.ALLOW_CHILDREN_WRITE);
        trans.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
        trans.setCapability(Node.ENABLE_PICK_REPORTING);
        trans.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        return trans;
    }

    /**
     * This method removes a subfigure for the caller figure. It also removes
     * all the children for the subfigure BEFORE removing the actual subfigure.
     * 
     * @param figure
     */
    public void removeSubFigure(final Figure3D figure) {
        // loggerlog(Level.FINE, "Removing figure..." + figure.toString());

        // get the branch: figure ->rotTG -> TG->particularBG -> *figures BG*
        final BranchGroup generalBranch = figure.getRootBranch();
        // get the parent branch (the subfigure branch of the superfigure)
        final BranchGroup superBranch = (BranchGroup) generalBranch.getParent();

        // tell the subfigure to remove all the subsubfigures
        figure.removeAllSubFigures();
        generalBranch.detach();
        // superBranch.removeAllChildren();
        AbstractFigure3D.logger.debug("Removing child from superbranch : " + superBranch);
        // FIXME - superBranch should never be null !!!!
        if (superBranch != null) {
            superBranch.removeChild(generalBranch);
        }
        // remove from the hashtable
        this.figures.values().remove(figure);
        this.arrangeSubFigures();
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D#getRootBranch()
     */
    public BranchGroup getRootBranch() {
        return this.rootBranch;
    }

    /**
     * @return the BranchGroup to which the actual shape is connected
     */
    public BranchGroup getFigureBranch() {
        return this.figureBranch;
    }

    /**
     * Utility method that returns the shortened name of the figure. It returns
     * characters number of characters or the full name if the name is shorter
     * than characters.
     * 
     * @param characters
     */
    public String getShortenedName(final int characters) {
        // return the first 10 or less characters
        return this.name.substring(0, Math.min(characters, this.name.length()));
    }

    /**
     * Draws communication between two figures. It uses the setArrow and
     * removeArrow methods to set the arrow figure and remove it. Each arrow has
     * an unique key and a TTL. For each arrow created a daemon thread is
     * spawned that will delete the arrow after timeToLive milliseconds. The
     * start and stop communication points are given as references to figures.
     * 
     * @param key
     * @param timeToLive
     * @param startAO
     * @param stopAO
     */
    public void drawCommunication(final String key, final String name, final long timeToLive,
            final Figure3D startAO, final Figure3D stopAO) {
        // TODO this is still not right, there must be a better way to draw
        // communications
        final Transform3D start = startAO.getCoordinates();
        final Transform3D stop = stopAO.getCoordinates();
        Vector3f begin = new Vector3f();
        Vector3f end = new Vector3f();
        start.get(begin);
        stop.get(end);
        begin = new Vector3f(begin);
        end = new Vector3f(end);
        // put in the list of arrows
        final Figure3D arrow = this.setArrow(name, begin, end);
        this.arrows.put(key, arrow);
        // add to the figure tree
        this.rootBranch.addChild(arrow.getRootBranch());
        // set destruction time
        final TimerTask arrowDestruction = new TimerTask() {
            @Override
            public void run() {
                AbstractFigure3D.this.removeArrow(key);
            }
        };
        // start the timer
        this.arrowTimer.schedule(arrowDestruction, 0);
    }

    /**
     * This method returns a figure that represents a communication (arrow,
     * line, etc). It should set the geometry of the figure to start at the
     * coordinates start and end with the coordinates of stop.
     * 
     * @param start
     * @param stop
     * @return
     */
    protected abstract Figure3D setArrow(String name, Vector3f start, Vector3f stop);

    /**
     * Removes the arrow identified by the UUID.
     * 
     * @param key
     */
    protected void removeArrow(final String key) {
        final Figure3D arrow = this.arrows.get(key);
        arrow.getRootBranch().detach();
        this.rootBranch.removeChild(arrow.getRootBranch());
        this.arrows.remove(key);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D#getTranslateScaleTransform()
     */
    public TransformGroup getTranslateScaleTransform() {
        return this.translateScaleTrans;
    }

    public TransformGroup getRotationTransform() {
        return this.rotTrans;
    }

    public Geometry getGeometry() {
        return this.voGeometry;
    }

    public Transform3D getCoordinates() {
        final Transform3D trans = new Transform3D();
        this.getLocalToVworld(trans);
        return trans;
    }
    
    @Override
    public void addObserver(Observer o) {
    	observers.add(o);
    }
    
    @Override
    public void notifyObservers(Object arg) {
    	for (Observer observer : observers) {
			observer.update(null, arg);
		}
    }

}
