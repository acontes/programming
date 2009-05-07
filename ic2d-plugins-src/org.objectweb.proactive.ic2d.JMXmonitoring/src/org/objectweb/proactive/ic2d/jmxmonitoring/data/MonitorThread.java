/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.data;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.jmxmonitoring.Activator;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;


/**
 * This class is used to perform a refresh over the model data (the IC2D
 * representations of the monitored objects). It defines a thread (<code>MonitorThreadRefresher</code>)
 * that explores all the data in order to guaranty the sync between the
 * monitored objects and their representations.
 * 
 * It also defines a thread (<code> MonitorThreadSelectiveRefresher </code>)
 * that will performed an explore on objects for which an explore have been
 * specifically asked. This thread is used to explore new found objects in the
 * system. The time between two executions of the
 * MonitorThreadSelectiveRefresher (timeForSelectiveRefresh * 1000) should
 * always be less that
 * 
 * @author The ProActive Team
 * 
 */
public final class MonitorThread implements Observer {
    private static org.apache.log4j.Logger logger = ProActiveLogger.getLogger(Loggers.JMX);
    /**
     * Default time to refresh in seconds
     */
    private final static int DEFAULT_TTR = 10;

    /**
     * Default selective time to refresh in seconds
     */
    private final static int DEFAULT_TIME_SELECTIVE_REFRESH = 20;

    // /** Hosts will be recursively searched up to this depth */
    // private int depth;

    /** Thread which refresh the objects */
    private Thread refresher;
    private Thread selectiveRefresher;

    /** true if we want to refresh, false otherwise */
    private boolean refresh;
    private boolean selectiveRefresh;

    private WorldObject worldObj;

    /** Time To Refresh (in seconds) */
    private int ttr;
    private int timeForSelectiveRefresh = DEFAULT_TIME_SELECTIVE_REFRESH;
    private ConcurrentHashMap<String, AbstractData<?, ?>> objectsToRefreshSelectively;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //

    /**
     * Creates a new MonitorThread
     * 
     * @param world
     *            A world object
     */
    public MonitorThread(WorldObject world) {
        this.ttr = DEFAULT_TTR;
        this.worldObj = world;
        this.objectsToRefreshSelectively = new ConcurrentHashMap<String, AbstractData<?, ?>>();
        this.refresh = false;
        this.refresher = new Thread(new MonitorThreadRefresher(world), "IC2D refresh thread");
        this.selectiveRefresh = true;
        this.selectiveRefresher = new Thread(new MonitorThreadSelectiveRefresher(),
            "IC2D selective refresh thread");
        selectiveRefresher.start();
    }

    //
    // -- PUBLICS METHODS -----------------------------------------------
    //

    // /**
    // * Hosts will be recursively searched up to
    // * the depth returned by this method.
    // * @return depth depth used to searched up hosts
    // */
    // public int getDepth(){
    // return this.depth;
    // }
    //
    // /**
    // * Sets the depth used to searched up hosts.
    // * @param depth the news depth
    // */
    // public void setDepth(int depth){
    // this.depth = depth;
    // }

    /**
     * Returns the Time To Refresh (in seconds).
     * 
     * @return time to refresh
     */
    public int getTTR() {
        return this.ttr;
    }

    /**
     * Sets the Time To Refresh (in seconds).
     * 
     * @param ttr
     *            the new time to refresh
     */
    public void setTTR(int ttr) {
        this.ttr = ttr;
    }

    /**
     * Adds an object to be explored When a new Object is discovered (i.e. a new
     * Runtime) this method is to be called in order to explore the new object.
     * Calling this method instead of directly calling explore on the new object
     * is recommended as it would avoid exploring the object several times.
     * 
     * @param data
     *            the object to explore
     */
    public void addObjectToExplore(AbstractData<?, ?> data) {
        this.objectsToRefreshSelectively.put(data.getKey(), data);
    }

    public void update(Observable o, Object arg) {
        if ((arg != null) && o instanceof WorldObject && arg instanceof MVCNotification) {
            MVCNotificationTag notification = ((MVCNotification) arg).getMVCNotification();
            if (notification == MVCNotificationTag.WORLD_OBJECT_FIRST_CHILD_ADDED) {
                startRefreshing((WorldObject) o);
            } else if (notification == MVCNotificationTag.WORLD_OBJECT_LAST_CHILD_REMOVED) {
                stopRefreshing();
            }
        }
    }

    public void forceRefresh() {
        gefRefresh();
        refresher.interrupt();
    }

    //
    // -- PROTECTED METHODS -----------------------------------------------
    //
    protected void startRefreshing(WorldObject world) {
        refresh = true;
        if (refresher.getState() == Thread.State.TERMINATED) {
            refresher = new Thread(new MonitorThreadRefresher(world));
            refresher.start();
        }

        // the thread has not yet been started. We start it now.
        if (refresher.getState() == Thread.State.NEW) {
            refresher.start();
        }
    }

    protected void stopRefreshing() {
        refresh = false;
        refresher.interrupt();
    }

    //
    // -- INNER CLASS -----------------------------------------------
    //
    private class MonitorThreadRefresher implements Runnable {

        /** The World to refresh */
        private WorldObject world;

        public MonitorThreadRefresher(WorldObject world) {
            this.world = world;
        }

        public void run() {
            while (refresh) {
                try {
                    world.explore();
                } catch (Exception e) {
                    logger
                            .debug("Exception when performing refresh on the WorldObject: " +
                                e.getStackTrace());
                    Console
                            .getInstance(Activator.CONSOLE_NAME)
                            .err(
                                    "Refresh could not be performed. See logged errors for more details. Make sure the same ProActive version is used for IC2D and all monitored ressources. Reason : " +
                                        e);
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(ttr * 1000);
                } catch (InterruptedException e) { /* Do nothing */
                    System.out.println("IC2D exploring thread has been interrupted.");
                }
            }
        }
    }

    private class MonitorThreadSelectiveRefresher implements Runnable {
        public void run() {
            try {
                while (selectiveRefresh) {
                    for (AbstractData<?, ?> ad : objectsToRefreshSelectively.values()) {
                        ad.explore();
                        // System.out.println("Selective monitoring thread
                        // explores
                        // "+ad);
                    }
                    objectsToRefreshSelectively.clear();

                    Thread.sleep(timeForSelectiveRefresh * 1000);
                }
            } catch (InterruptedException e) {
                System.out.println("IC2D selective exploring thread has been interrupted.");
            }
        }
    }

    private void gefRefresh() {
        worldObj.notifyChanged();

    }

}
