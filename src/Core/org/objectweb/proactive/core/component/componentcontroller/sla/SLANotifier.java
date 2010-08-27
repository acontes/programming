package org.objectweb.proactive.core.component.componentcontroller.sla;

public interface SLANotifier {

	void notifyAlarm(AlarmLevel level, AlarmData data);
	
}
