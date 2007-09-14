package org.objectweb.proactive.infrastructuremanager.data;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.extra.infrastructuremanager.IMFactory;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMAdmin;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMMonitoring;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;
import org.objectweb.proactive.infrastructuremanager.views.Console;
import org.objectweb.proactive.infrastructuremanager.views.IMViewInfrastructure;

public class IMData implements Runnable {

	private URI uri;
	private IMViewInfrastructure view;
	private IMAdmin admin;
	private IMMonitoring monitoring;

	private int freeNode, busyNode, downNode;
	private ArrayList<IMNode> infrastructure;
	
	private long ttr = 5;

	public IMData() {
	}

	public IMData(String urlString, IMViewInfrastructure view) {
		try {
			uri = new URI(urlString);
			this.view = view;
			admin = IMFactory.getAdmin(uri);
			monitoring = IMFactory.getMonitoring(uri);
			ProActive.waitFor(monitoring);
			ProActive.waitFor(admin);
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

	public int getFree() {
		return freeNode;
	}

	public int getBusy() {
		return busyNode;
	}
	
	public int getDown() {
		return downNode;
	}
	

	public void updateInfrastructure() {
		infrastructure = monitoring.getListAllIMNodes();
		freeNode = monitoring.getNumberOfFreeResource().intValue();
		busyNode = monitoring.getNumberOfBusyResource().intValue();
		downNode = monitoring.getNumberOfDownResource().intValue();
		
		Collections.sort(infrastructure);
	}

	public void run() {
		while(view != null) {
			Console.getInstance().log("Refresh");
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
		Console.getInstance().log("Set TTR : Time To Refresh = " + t + " seconds");
		ttr = t;
		view.threadRefresh.interrupt();
	}
	
}
