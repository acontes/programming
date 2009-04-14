package org.objectweb.proactive.ic2d.componentmonitoring.data;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;

/**
 * This class will hold the set of Components monitored.
 * It will consult the WorldObject to extract the AO-Components, and add it to the set.
 * After, it will observe the WorldObject to receive notifications of ActiveObjects added/removed, which could be Components.
 * 
 * It will also contain a MonitorThread in charge of periodically collect the statistics of the monitored components.
 * 
 *
 */
public class ComponentModelHolder implements Observer {

	public Logger logger = Logger.getLogger("ComponentModelHolder");
	
	private static String ObjectNameString = "org.objectweb.proactive.ic2d.componentmonitoring:type=ComponentHolder";
	private static String TypeString = "ComponentModel-Holder";
	
	/** 
	 * The list of all components included in this holder
	 */
	public Map<UniqueID, ComponentModel> components;

	/**
	 * Model name
	 */
	private String name = "";
	
	/**
	 * The model to observe and look for AO-Components.
	 */
	private WorldObject worldObject;
	
	/**
	 * Creates a new holder for components, which will observe the WorldObject received.
	 * 
	 * @param worldObject
	 */
	
	public ComponentModelHolder(WorldObject worldObject) {
		logger.setLevel(Level.DEBUG);
		logger.debug("constructor");
		this.name = worldObject.getName();
		this.worldObject = worldObject;
		this.components = new ConcurrentHashMap<UniqueID, ComponentModel>();
		// adds itself as observer for this worldObject
		logger.debug("Adding itself as observer for WorldObject "+ worldObject.getKey());
		worldObject.addObserver(this);
	}
	
	public void explore() {
		// TODO This should explore all the components listed for subcomponents ...
		
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return TypeString;
	}

	@Override
	public void update(Observable o, Object arg) {
		final MVCNotification mvcNotif = (MVCNotification) arg;
        final MVCNotificationTag mvcNotifTag = mvcNotif.getMVCNotification();
        logger.debug("Received MVCNotification from WorldObject: "+ mvcNotif.getMVCNotification().toString()+ ", "+ mvcNotif.getData().toString());
        // now it has to create the new ComponentModel (+ the edit parts and etc.), if it didn't exist before and add it to his list
		
	}
	

}
