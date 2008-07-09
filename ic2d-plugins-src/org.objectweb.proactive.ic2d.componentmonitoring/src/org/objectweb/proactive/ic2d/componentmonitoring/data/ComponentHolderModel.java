package org.objectweb.proactive.ic2d.componentmonitoring.data;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractHolder;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.HolderTypes;
import org.objectweb.proactive.ic2d.componentmonitoring.util.ComponentMVCNotification;
import org.objectweb.proactive.ic2d.componentmonitoring.util.ComponentMVCNotificationTag;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;

public class ComponentHolderModel extends AbstractData implements AbstractHolder
{

	public Map<UniqueID,ComponentModel> components;
	
	private static String ObjectNameString = "org.objectweb.proactive.ic2d.componentmonitoring:type=ComponentHolder";

	// -------------------------------------------
	// --- Constructor ---------------------------
	// -------------------------------------------

	private String name = "";

	public ComponentHolderModel() throws MalformedObjectNameException, NullPointerException
	{
		super(new ObjectName(ObjectNameString));
		name = ComponentHolderModel.class.getName();
        this.components = new ConcurrentHashMap<UniqueID,ComponentModel>();

	}

	@Override
	public void explore()
	{
		// TODO Auto-generated method stub
		findSubComponents();
	}

	@Override
	public String getKey()
	{
		// TODO Auto-generated method stub
		return getName();
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public <T extends AbstractData> T getParent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType()
	{
		// TODO Auto-generated method stub
		return "Components-holder";
	}

	private void findSubComponents()
	{

	}
	
	 /**
     * Adds a child to this object, and explore this one.
     * @param <T>
     * @param child The child to explore
     */
    public synchronized void addChild(AbstractData child) {
        if (!this.monitoredChildren.containsKey(child.getKey())) {
            this.monitoredChildren.put(child.getKey(), child);
            setChanged();
            notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.ADD_CHILD, child.getKey()));
            child.explore();
        }
    }

    /**
     * Deletes a child from all recorded data.
     * @param child The child to delete.
     */
    public void removeChild(AbstractData child) {
        if (child == null) {
            return;
        }

        child.removeAllConnections();

        String key = child.getKey();
        monitoredChildren.remove(key);
        notMonitoredChildren.remove(key);
        setChanged();

        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.REMOVE_CHILD, key));
    }

	@Override
	public HolderTypes getHolderType() {
		return HolderTypes.COMPONENT_HOLDER;
	}

}
