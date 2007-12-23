package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;


import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.proearth.SphereArrow3D;

/**
 * All representation of a grid should extend this class.
 * Usually the grid is an invisible figure that contains the 
 * hosts. However there may be cases where a some geometry for 
 * the grid might be desirable. 
 * @author vjuresch
 *
 */
public abstract class AbstractGrid3D extends AbstractFigure3D {
    //needed to schedule arrow deletion
    /**
     * A timer for the deletion of the arrows 
     */
    private Timer arrowTimer = new Timer();
    /**
     * A list containing all the arrows in this grid. 
     */
    private Hashtable<String, AbstractArrow3D> arrows = new Hashtable<String, AbstractArrow3D>();

	
	/**
	 * @param name - name of the figure, for a grid is usually empty 
	 */
	public AbstractGrid3D(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }
    
    
    /**
     *   Draws a straight arrow from the AbstractFigure3D start
     *    to AbstractFigure3D stop
     * and removes it after timeToLive milliseconds. 
     * @param key key of the AbstractFigure3D
     * @param name name of the arrow, usually empty
     * @param timeToLive  time after which the arrow is removed
     * @param startAO	the start figure
     * @param stopAO	the end figure
     */
    //TODO this might need to be a method in abstract figure
	public void drawArrow(final String key, String name, long timeToLive,
        AbstractFigure3D startAO, AbstractFigure3D stopAO) {
        Transform3D start = new Transform3D();
        Transform3D stop = new Transform3D();
        //got the coordinates of start and stop
        startAO.getLocalToVworld(start);
        stopAO.getLocalToVworld(stop);
        Vector3f begin = new Vector3f();
        Vector3f end = new Vector3f();
        start.get(begin);
        stop.get(end);
        //create a line between
        Arrow3D arrow = new Arrow3D(name, begin, end);
        // arrow ->rotTG->TG ->particular BG -> generalBG
        getRootBranch().addChild(arrow.getRootBranch());

        //add to list of arrows
        arrows.put(key, arrow);
        //set destruction time
        TimerTask arrowDestruction = new TimerTask() {
                @Override
                public void run() {
                    removeArrow(key);
                }
            };
        //start the timer   
        arrowTimer.schedule(arrowDestruction, timeToLive);
    }
    /**
     * Draws a curved arrow on a sphere. The start
     * and stop coordinates are spherical, given 
     * by two pairs of angles.
     *  
     * @param key	arrow key, needed for deletion, should be unique
     * @param name	arrow name, usually empty
     * @param timeToLive	time after which the arrow is removed
     * @param from	start pair of angles
     * @param to	stop pair of angles
     */
	//TODO this might need to be somewhere else, doesn't work yet 
    public void drawSphereArrow(final String key, String name, long timeToLive,
            Tuple2d from, Tuple2d to) {
            //create a line between
            SphereArrow3D arrow = new SphereArrow3D(name, from, to);
            // arrow ->rotTG->TG ->particular BG -> generalBG
            getRootBranch().addChild(arrow.getRootBranch());
            //add to list of arrows
            arrows.put(key, arrow);
            //set destruction time
            TimerTask arrowDestruction = new TimerTask() {
                    @Override
                    public void run() {
                        removeArrow(key);
                    }
                };
            arrowTimer.schedule(arrowDestruction, timeToLive);
        }
    /**
     * Removes an arrow using its key. This method is for internal use
     * Since it acts on a branch group it can remove 
     * any type of arrow (arrows are also shapes connected to branch groups).
     * @param key	arrow key
     */
    //TODO this could be transformed into a generic method for 
    //removing a shape after a timer
    private void removeArrow(String key) {
        // arrow ->rotTG->TG ->particular BG -> generalBG
        BranchGroup bg = arrows.get(key).getRootBranch();
        //detach branch group
        bg.detach();
        //remove from list of arrows
        arrows.remove(key);
    }
}
