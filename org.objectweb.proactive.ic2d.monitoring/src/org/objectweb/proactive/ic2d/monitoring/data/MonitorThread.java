/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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

import java.util.ArrayList;
import java.util.List;

public class MonitorThread {
	
	private final static int DEFAULT_DEPTH = 3;
	private final static int DEFAULT_TTR = 30;
	
	/** Singleton design pattern */
	private static MonitorThread instance;
	
	/** Hosts will be recursively searched up to this depth */
	private int depth;
	
	/** List of hosts which are monitored (List<HostObject>) */
	private List monitoredHosts;
	
	/** Thread which refresh the objects */
	private Thread refresher;
	/** true if we want to refresh, false otherwise */
	private boolean refresh;
	/** Time To Refresh (in seconds) */
	private int ttr;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	private MonitorThread(){
		this.depth = DEFAULT_DEPTH;
		this.ttr = DEFAULT_TTR;
		
		this.monitoredHosts = new ArrayList();
		
		this.refresh = false;
		this.refresher = new Thread(new Refresher());
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//
	
	/**
	 * TODO
	 */
	public static MonitorThread getInstance(){
		if(instance == null)
			instance = new MonitorThread();
		return instance;
	}
	
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
	
	/**
	 * Begin to monitor the host specified. 
	 * @param host the host to monitor
	 */
	public void addMonitoredHost(HostObject host) {
		if(!monitoredHosts.contains(host))
			monitoredHosts.add(host);
		if(monitoredHosts.size() == 1)
			startRefreshing();
	}
	
	/**
	 * Stop monitoring the host specified.
	 * @param host the host to stop monitoring
	 */
	public void removeMonitoredHost(HostObject host) {
		monitoredHosts.remove(host);
		if(monitoredHosts.size() == 0)
			stopRefreshing();
	}
	
	//
	// -- PRIVATE METHODS -----------------------------------------------
	//
	
	private void startRefreshing() {
		refresh = true;
		refresher.start();
	}
	
	private void stopRefreshing() {
		refresh = false;
	}
	
	private void monitor() {
		for(int i=0 ; i<monitoredHosts.size() ; i++)
			((HostObject)monitoredHosts.get(i)).explore();
	}
	
	//
	// -- INNER CLASS -----------------------------------------------
	//
	
	private class Refresher implements Runnable {
		
		public void run() {
			while(refresh) {
				System.out.println("******* MonitorThread : run ********");
				monitor();
				try {
					Thread.sleep(ttr * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
