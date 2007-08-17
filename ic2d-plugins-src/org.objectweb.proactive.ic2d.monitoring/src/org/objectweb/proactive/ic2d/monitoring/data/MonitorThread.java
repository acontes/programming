/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.monitoring.data;

import java.util.Observable;
import java.util.Observer;


public class MonitorThread implements Observer {

	private final static int DEFAULT_DEPTH = 3;
	private final static int DEFAULT_TTR = 30;


	/** Hosts will be recursively searched up to this depth */
	private int depth;

	/** Thread which refresh the objects */
	private Thread refresher;
	/** true if we want to refresh, false otherwise */
	private boolean refresh;
	/** Time To Refresh (in seconds) */
	private int ttr;

	//
	// -- CONSTRUCTORS -----------------------------------------------
	//

	/**
	 * Creates a new MonitorThread
	 * @param worl A world object
	 */
	public MonitorThread(WorldObject world){
		this.depth = DEFAULT_DEPTH;
		this.ttr = DEFAULT_TTR;

		this.refresh = false;
		this.refresher = new Thread(new MonitorThreadRefresher(world));
	}

	//
	// -- PUBLICS METHODS -----------------------------------------------
	//

	/**
	 * Hosts will be recursively searched up to 
	 * the depth returned by this method.
	 * @return depth depth used to searched up hosts
	 */
	public int getDepth(){
		return this.depth;
	}

	/**
	 * Sets the depth used to searched up hosts.
	 * @param depth the news depth
	 */
	public void setDepth(int depth){
		this.depth = depth;
	}

	/**
	 * Returns the Time To Refresh (in seconds). 
	 * @return time to refresh
	 */
	public int getTTR() {
		return this.ttr;
	}

	/**
	 * Sets the Time To Refresh (in seconds).
	 * @param ttr the new time to refresh
	 */
	public void setTTR(int ttr) {
		this.ttr = ttr;
	}


	public void update(Observable o, Object arg) {
		if (arg != null && o instanceof WorldObject && arg instanceof WorldObject.methodName) {
			WorldObject world = (WorldObject)o;
			WorldObject.methodName method = (WorldObject.methodName)arg;
			if(method == WorldObject.methodName.PUT_CHILD)
				world.getMonitorThread().startRefreshing((WorldObject)o);
			else if (method == WorldObject.methodName.REMOVE_CHILD)
				world.getMonitorThread().stopRefreshing();
		}
	}

	public void forceRefresh() {
		refresher.interrupt();
	}
	
	//
	// -- PROTECTED METHODS -----------------------------------------------
	//

	protected void startRefreshing(WorldObject world) {
		refresh = true;
		if(refresher.getState()==Thread.State.TERMINATED){
			refresher = new Thread(new MonitorThreadRefresher(world));
		}
		refresher.start();
	}


	protected void stopRefreshing() {
		refresh = false;
		refresher.interrupt();
	}
	
	//
	// -- INNER CLASS -----------------------------------------------
	//

	private class MonitorThreadRefresher implements Runnable {

		/** The World to refresh*/
		private WorldObject world;
		
		public MonitorThreadRefresher(WorldObject world){
			this.world = world;
		}
		
		public void run() {
			while(refresh) {
				world.explore();
				try {
					Thread.sleep(ttr * 1000);
				} catch (InterruptedException e) {/* Do nothing */}
			}
		}
	}
}
