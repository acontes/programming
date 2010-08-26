package org.objectweb.proactive.core.component.componentcontroller.monitoring.event;

import java.io.Serializable;

public class RemmosEvent implements Serializable {

	RemmosEventType eventType;
	
	Object eventData;
	
	public RemmosEvent(RemmosEventType type, Object data) {
		this.eventType = type;
		this.eventData = data;
	}
	
	public RemmosEventType getType() {
		return eventType;
	}
	
	public Object getData() {
		return eventData;
	}
}
