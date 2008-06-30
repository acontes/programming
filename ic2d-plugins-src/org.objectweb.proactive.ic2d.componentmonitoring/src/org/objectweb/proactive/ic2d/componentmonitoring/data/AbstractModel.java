package org.objectweb.proactive.ic2d.componentmonitoring.data;

import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;



public class AbstractModel extends AbstractData {

	
//	 -------------------------------------------
    // --- Constructor ---------------------------
    // -------------------------------------------

    /**
     * The standard contructor for this model. Only object name have to be provided.
     * A HashMap<String, AbstractData> is used for monitored children and not monitored ones.
     * @param objectName An instance of ObjectName for this model
     */
    public AbstractModel(final ObjectName objectName) {
        this(objectName, new HashMap<String, AbstractData>(), new HashMap<String, AbstractData>());
    }

    /**
     * This constructor is provided to allow subclasses to specify their own map implementations
     * for monitored children.
     * @param objectName An instance of ObjectName for this model
     * @param monitoredChildren An instance of map for monitored children
     */
    public AbstractModel(final ObjectName objectName, final Map<String, AbstractData> monitoredChildren) {
        this(objectName, monitoredChildren, new HashMap<String, AbstractData>());
    }

    /**
     * This constructor is provided to allow subclasses to specify their own map implementations
     * for monitored children and not monitored children.
     * @param objectName An instance of ObjectName for this model
     * @param monitoredChildren An instance of map for monitored children
     * @param notMonitoredChildren An instance of map for not monitored children
     */
    public AbstractModel(final ObjectName objectName, final Map<String, AbstractData> monitoredChildren,
            final Map<String, AbstractData> notMonitoredChildren) {
        super(objectName,monitoredChildren,notMonitoredChildren);
    }
	@Override
	public void explore() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends AbstractData> T getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
