package org.eclipse.proactive.extendeddebugger.core.listener;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.eclipse.proactive.extendeddebugger.core.ExtendedDebugger;
import org.objectweb.proactive.core.debug.stepbystep.RemoteDebugInfo;
import org.objectweb.proactive.core.jmx.notification.NotificationType;


public class ExtendedDebuggerNotificationListener implements NotificationListener{

	private ExtendedDebugger extendedDebugger;
	
	public ExtendedDebuggerNotificationListener(ExtendedDebugger extendedDebugger) {
		this.extendedDebugger = extendedDebugger;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleNotification(Notification notifications, Object handback) {
		ConcurrentLinkedQueue<Notification> notifs = (ConcurrentLinkedQueue<Notification>) notifications.getUserData();
		for(Notification notification : notifs){
			if(notification.getType().equals(NotificationType.sendRequest)){
				System.out.println("*** recieve a sendRequest notification.");
				extendedDebugger.RecievedNotification((RemoteDebugInfo) notification.getUserData());
			}
		}
	}

}
