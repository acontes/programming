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
package org.objectweb.proactive.ic2d.infrastructuremanager.data;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.IMFactory;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMAdmin;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMMonitoring;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;
import org.objectweb.proactive.extra.infrastructuremanager.test.simple.ComparatorIMNode;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.infrastructuremanager.Activator;
import org.objectweb.proactive.ic2d.infrastructuremanager.views.IMViewInfrastructure;

public class IMData implements Runnable {

	private URI uri;
	private IMViewInfrastructure view;
	private IMAdmin admin;
	private IMMonitoring monitoring;

	private IntWrapper freeNode, busyNode, downNode;
	private ArrayList<IMNode> infrastructure;
	
	private Console console;
	
	private long ttr = 5;

	public IMData() {
	}

	public IMData(String urlString, IMViewInfrastructure view) {
		try {
			uri = new URI(urlString);
			this.view = view;
			console = Console.getInstance(Activator.CONSOLE_NAME);
			admin = IMFactory.getAdmin(uri);
			monitoring = IMFactory.getMonitoring(uri);
		}
		catch (ActiveObjectCreationException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
		}		
	}

	public ArrayList<IMNode> getInfrastructure() {
		return infrastructure;
	}

	public IMAdmin getAdmin() {
		return admin;
	}

	public IntWrapper getFree() {
		return freeNode;
	}

	public IntWrapper getBusy() {
		return busyNode;
	}
	
	public IntWrapper getDown() {
	    return downNode;
	}

	@SuppressWarnings("unchecked")
	public void updateInfrastructure() {
		infrastructure = monitoring.getListAllIMNodes();
		System.out.println("infrastructure = " + infrastructure);
		// New counting of nodes
		int freeN = 0;
		int busyN = 0;
		int downN = 0;
		for(IMNode imn : infrastructure) {
			if(imn.isDown()) downN++;
			else try{
				if(imn.isFree()) freeN++;
				else busyN++;
			} catch (NodeException e) {
				// Node is down (this case should not appears)
				downN++;
			}
		}
		freeNode = new IntWrapper(freeN);
		busyNode = new IntWrapper(busyN);
		downNode = new IntWrapper(downN);
		Collections.sort(infrastructure, new ComparatorIMNode());
	}

	public void run() {
		while(view != null) {
			// Not very nice
			//console.log("Refresh");
			updateInfrastructure();
			view.getParent().getDisplay().asyncExec( new Runnable(){
				public void run(){
					view.drawInfrastructure();
				}
			});
			try {
				Thread.sleep(ttr * 1000);
			} 
			catch (InterruptedException e) {
			}
		}
	}

	public long getTTR() {
		return ttr;
	}

	public void setTTR(long t) {
		console.log("Set TTR : Time To Refresh = " + t + " seconds");
		ttr = t;
		view.threadRefresh.interrupt();
	}
	
}
