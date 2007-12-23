package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.util.Hashtable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;

/**
 * This class is the parent of all figures. It implements some basic actions
 * like remove subfigures, add subfigures, branch creation and transform creation.
 * 
 * @author vjuresch
 *
 */
public abstract class AbstractFigure3D extends Shape3D {
	/**
	 * Contains the list of subFigures for this figure.
	 */
	private Hashtable<String, AbstractFigure3D> figures = new Hashtable<String, AbstractFigure3D>();
	/**
	 * This name will be displayed according to 
	 * the formatting chosen through createTextBranch() from the TextStyleBasket class
	 */
	private String name = new String();
	private Geometry voGeometry; // geometry of the figure
	private Appearance voAppearance; // appearance of the figure
	private Level logLevel = Level.OFF;
    private Logger logger = Logger.getLogger(
            "org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.views");
	private static ConsoleHandler handler = new ConsoleHandler();
	/**
	 * The figureBranch and the subFiguresBranch connect to the rootBranch.
	 */
	private BranchGroup rootBranch;
	/**
	 * The figureBranch contains the actual Shape3D for this figure. 
	 */
	private BranchGroup figureBranch;
	/**
	 * The subFiguresBranch contains the subfigures of this figure. 
	 */
	private BranchGroup subFiguresBranch;
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
	 * @return geometry of the figure
	 */
    protected abstract Geometry createGeometry();
	/**
	 * This method sets the state of the object. This should be implemented as a
	 * change of appearance or geometry. Takes as a parameter an enumeration
	 * State defined in org.objectweb.proactive.ic2d.jmxmonitoring.util.State
	 * 
	 * @param state a Enum defined in
	 *            org.objectweb.proactive.ic2d.jmxmonitoring.util.State
	 *            The possible states(for now) are
	 *            <ul><li>
	 *            UNKNOWN</li>
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
	 *   This is the only part that connects this class directly
	 *   to IC2D. For this reason it is possible that it may be 
	 *   removed or made more generic.
	 *   
	 *   The implementations of AbstractFigure3D should 
	 *   implement different appearances for different states.  
     */
	public void setState(State state) {
		switch (state) {
		case UNKNOWN:
			setStateUnkown();
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

  //  private RotationInterpolator rot;

	/**
	 * The constructor creates the Shape3D with the geometry and appearance from
	 * the method createGeometry and createAppearance. subFiguresBG -> generalBG
	 * Each figure is actual a small tree composed of a few transform groups,
	 * branch groups and the actual shape 3D.
	 * <pre>
	 *  -> = child - parent
	 * 
	 * The structure is:
	 * 
	 * actual figure -> rotTG -> generalTG -> figureBG -> generalBG textNode ->
	 * rotTG subFigures BG -> generalBG
	 * 
	 * </pre>
	 * Each subfigure has a similar structure with its generalBG connected to
	 * the subFiguresBG of the parent figure. The highest generalBG connects to
	 * the locale of the universe. The several BG levels are necessary for
	 * detaching and attaching subfigures.
	 * 
	 * 
	 * @param name The figure name to be displayed.
	 */
	public AbstractFigure3D(String name) {
		logger.log(Level.FINE, "Setting appearance and geometry...");
		// initialize logger
		logger.setLevel(logLevel);
		handler.setLevel(logLevel);
		logger.addHandler(handler);
		// set the name
		this.name = new String(name);
		/*
		 * set the capabilities for this shape to allow later change of
		 * appearance, geometry, picking, etc.
		 */
		this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		this.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

		// create geometry and appearance
		voGeometry = createGeometry();
		voAppearance = createAppearance();
		// set the geometry and appearance
		this.setGeometry(voGeometry);
		this.setAppearance(voAppearance);
		// set the bounds for the figure - useful for picking
		// TODO is this correct ?
		this.setBounds(new BoundingSphere());
		/**
		 * subFiguresBG -> generalBG Each figure is actual a small tree composed
		 * of a few transform groups, branch groups and the actual shape.
		 *  -> = child - parent
		 * 
		 * The structure is:
		 * <pre>
		 * actual figure -> rotTG -> generalTG -> figureBG -> rootBG
		 * 		textNode -> rotTG
		 *  								subFigures BG -> rootBG
		 * </pre>
		 * Each subfigure has a similar structure with its generalBG connected
		 * to the subFiguresBG of the parent figure. The highest generalBG
		 * connects to the locale of the universe. The several BG levels are
		 * necessary for detaching and attaching subfigures.
		 */
		// create root branch
		rootBranch = createBranch();
		// create the BG group for the figure
		figureBranch = createBranch();
		// create the BG for the subfigures
		subFiguresBranch = createBranch();
		// connect
		
		//rootBranch.addChild(subFiguresBranch);
		
		rootBranch.addChild(figureBranch);
		// and a transform group for rotation and translation
		TransformGroup trans = createTransform();
		TransformGroup transRotate = createTransform();
		// connect the rotation transform to the translation transform
		trans.addChild(transRotate);
		// add the text to the rotation Transform
		transRotate.addChild(createTextBranch());
		// add this figure to the rotation transform
		transRotate.addChild(this);
		
		//TODO maybe change like this, needs to be checked
		transRotate.addChild(subFiguresBranch);
		
		
		// connect the transforms to the figure branch
		figureBranch.addChild(trans);
		// optimize
		rootBranch.compile();
	}
	/**
	 * This method creates the text branch and sets the appearance of the text.
	 * All text styles are stored in TextStyleBasket and can be accessed
	 * statically.
	 */
	protected abstract TransformGroup createTextBranch();
	/**
	 * Removes all the subfigure for this figure.
	 * This method calls removeSubFigure(AbstractFigure3D figure)
	 * for each subfigure.
	 */
   public void removeAllSubFigures() {
        logger.log(Level.FINE, "Removing all subfigures...");
        while (!figures.isEmpty())
            removeSubFigure(figures.keys().nextElement());
    }
	/**
	 * This method returns a subfigure from the list of subfigures.
	 * @param key the key of the subfigure
	 * 
	 * Keys are not properties of the figures but of the list 
	 * contained in the parent figure.
	 * 
	 * 	@return the AbstractFigure3D with the corresponding key	 
	 */
    public AbstractFigure3D getSubFigure(String key) {
        return figures.get(key);
    }
	/**
	 * Removes a subfigure by key. This method calls 
	 * removeSubFigure(AbstractFigure3D figure).
	 * @param key
	 */
    public void removeSubFigure(String key) {
        removeSubFigure(figures.get(key));
    }

    /**
     * @return a String cotaining the figure name
     */
    public String getFigureName() {
        return new String(this.name);
    }
    
    /**
     * Sets the figure name.
     * @param name
     */
    //TODO also update the text branch for the figure
    public void setFigureName(String name) {
        logger.log(Level.FINE, "Changing figure name...");
        this.name = new String(name);
    }
	/**
	 * @return a Hashtable containing the subfigures
	 */
    public Hashtable<String, AbstractFigure3D> getSubFigures() {
        return figures;
    }

	/**
	 * Adds a subfigure to the subFigures
	 * BranchGroup.
	 * 
	 * @param key the key of the figure (should be unique)
	 * @param figure the Abstract3DFigure to be added
	 */
	public void addSubFigure(String key, AbstractFigure3D figure) {
		logger.log(Level.FINE, "Adding a new figure:" + key);
		// gets the main branch group of the subFigure
		// and it adds it to the subFigures BranchGroup
	
		// figure ->rotTG ->generalTG-> particularBG -> rootBG (from the constructor)
		BranchGroup root = figure.getRootBranch();

		root.compile();
		// add the figure
		subFiguresBranch.addChild(root);
		figures.put(key, figure);
		//update the view
		arrangeSubFigures();
		//animate the creation
		figure.animateCreation();

	}
	/**
	 * Method that animates the creation of a
	 * figure by changing the geometry or the
	 * appearance. 
	 * The animations should be found in the class
	 * AnimationBasket.
	 */
	protected abstract void animateCreation();
	/**
	 * Utility method for creating a BranchGroup
	 * @return a BranchGroup with the needed capabilities set
	 */
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
	/**
	 * Places an AbstractFigure3D at the x,y,z coordinates in the universe.
	 * For now it is used by the PlacementBasket class. 
	 * 
	 * @param figure
	 * @param x
	 * @param y
	 * @param z
	 */
    //TODO pass a Transform3D instead of x.y.x coordinates so
    //rotation can be also applied
    public void placeSubFigure(AbstractFigure3D figure, double x, double y,
        double z) {
        // get the parent transform
        TransformGroup trans = (TransformGroup) figure.getParent().getParent();
        Transform3D translate = new Transform3D();
        translate.setTranslation(new Vector3d(x, y, z));
        trans.setTransform(translate);
    }
	/**
	 * Utility method for creating a TransformGroup
	 * with the correct capabilities set
	 * @return a TransformGroup with the needed capabilities set
	 */
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

	/**
	 * This method removes a subfigure for the caller figure.
	 * It also removes all the children for the subfigure BEFORE
	 * removing the actual subfigure. 
	 * @param figure
	 */
	public void removeSubFigure(AbstractFigure3D figure) {
		logger.log(Level.FINE, "Removing figure..." + figure.toString());

		// get the branch: figure ->rotTG -> TG->particularBG -> *figures BG*
		BranchGroup generalBranch = figure.getRootBranch();
		//get the parent branch (the subfigure branch of the superfigure)
		BranchGroup superBranch = (BranchGroup)generalBranch.getParent();
		
		// tell the subfigure to remove all the subsubfigures
		figure.removeAllSubFigures();
		generalBranch.detach();
		superBranch.removeChild(generalBranch);
		// remove from the hashtable
		figures.values().remove(figure);
		arrangeSubFigures();
	}
	/**
	 * @return the highest BranchGroup of this figure
	 */
    public BranchGroup getRootBranch() {
        return rootBranch;
    }
    /**
	 *  @return the BranchGroup immediate bellow the root branch
	 *  (the BranchGroup to which the actual shape is connected).
	 */
    public BranchGroup getParticularBranch() {
        return figureBranch;
    }
	/**
	 * Utility method that returns the shortened name of the figure.
	 * It returns characters number of characters or the full name
	 * if the name is shorter than characters. 
	 * 
	 * @param characters
	 */
    public String getShortenedName(int characters) {
        // return the first 10 or less characters
        return new String(name.substring(0, Math.min(characters, name.length())));
    }
}
