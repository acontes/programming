package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import java.util.Hashtable;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;


public interface Figure3D {

    /**
     * When implemented this abstract method should arrange the subfigures
     * attached to this figure.
     */
    void arrangeSubFigures();

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
     * The implementations should have different appearances for different
     * states.
     */
    void setState(State state);

    /**
     * Sets the figure's appearance to unknown. All appearances should be taken
     * from the class AppearanceBasket.
     */
    void setStateUnkown();

    /**
     * Removes all the subfigure for this figure. This method calls
     * removeSubFigure(Figure3D figure) for each subfigure.
     */
    void removeAllSubFigures();

    /**
     * This method returns a subfigure from the list of subfigures.
     * 
     * @param key
     *            the key of the subfigure
     * 
     * Keys are not properties of the figures but of the list contained in the
     * parent figure.
     * 
     * @return the Figure3D with the corresponding key
     */
    Figure3D getSubFigure(String key);

    /**
     * Removes a subfigure by key. This method calls removeSubFigure(Figure3D
     * figure).
     * 
     * @param key
     */
    void removeSubFigure(String key);

    /**
     * @return a String containing the figure name
     */
    String getFigureName();

    /**
     * Sets the figure name.
     * 
     * @param name
     */
    void setFigureName(String name);

    /**
     * @return a Hashtable containing the subfigures
     */
    Hashtable<String, Figure3D> getSubFigures();

    /**
     * Adds a subfigure to the subFigures BranchGroup.
     * 
     * @param key
     *            the key of the figure (should be unique)
     * @param figure
     *            the Figure3D to be added
     */
    void addSubFigure(String key, Figure3D figure);

    /**
     * Places a Figure3D at the x,y,z coordinates in the universe. For now it is
     * used by the PlacementBasket class.
     * 
     * @param figure
     * @param x
     * @param y
     * @param z
     */
    void placeSubFigure(Figure3D figure, double x, double y, double z);

    /**
     * This method removes a subfigure for the caller figure. It also removes
     * all the children for the subfigure BEFORE removing the actual subfigure.
     * 
     * @param figure
     */
    void removeSubFigure(Figure3D figure);

    /**
     * Returns a  branch group containing the root branch of the figure.
     * The root branch connects to the subfigures branch of the parent
     * figure.
     *  
     * @return a BranchGroup containing the highest BranchGroup of this figure
     */
    BranchGroup getRootBranch();

    /**
     * @return the BranchGroup to which the actual shape is connected
     */
    BranchGroup getFigureBranch();

    /**
     * Utility method that returns the shortened name of the figure. It returns
     * characters number of characters or the full name if the name is shorter
     * than characters.
     * 
     * @param characters
     */
    String getShortenedName(int characters);

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
    void drawCommunication(final String key, String name, long timeToLive, Figure3D startAO, Figure3D stopAO);

    /**
     * Returns the transform containing the translation and scaling
     * for the figure. 
     * @return TransformGroup containing scaling and translation
     */
    TransformGroup getTranslateScaleTransform();

    TransformGroup getRotationTransform();

    Geometry getGeometry();

    Transform3D getCoordinates();

    void animateCreation();

}