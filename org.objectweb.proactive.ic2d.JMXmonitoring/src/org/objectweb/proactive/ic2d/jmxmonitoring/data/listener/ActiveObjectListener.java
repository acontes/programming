package org.objectweb.proactive.ic2d.jmxmonitoring.data.listener;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.notification.RequestNotificationData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.NamesFactory;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.State;

public class ActiveObjectListener implements NotificationListener{

	private ActiveObject ao;
	private String name;

	public ActiveObjectListener(ActiveObject ao){
		this.ao = ao;
	}

	public void handleNotification(Notification notifications, Object handback) {

		if (!(notifications.getUserData() instanceof ConcurrentLinkedQueue)) {
			int a = 0;
			a++;
		}
		
		final ConcurrentLinkedQueue<Notification> notifs = (ConcurrentLinkedQueue<Notification>)notifications.getUserData();
		/*new Thread(){
			public void run(){*/
		for (Notification notification : notifs) {
			String type = notification.getType();
			
			if(type.equals(NotificationType.requestReceived)){
				System.out.println(".................................Request Received : "+ao.getName());
				
				RequestNotificationData request = (RequestNotificationData) notification.getUserData();
				UniqueID sourceID = request.getSource();
				UniqueID destinationID = request.getDestination();
				
				if(!destinationID.equals(ao.getUniqueID())){
					System.err.println("ActiveObjectListener.handleNotification() the destination id != ao.id");
				}
				
				//MethodName
				String methodName = request.getMethodName();

				// Try to find the name used in the display by the active object
				String sourceName = NamesFactory.getInstance().getName(sourceID);
				if(sourceName==null){
					System.out.println(sourceID +" -- " +methodName+ " --> "+ao.getName());
				}
				else{
					// Update the request queue length
					ao.setRequestQueueLength(request.getRequestQueueLength());

					// Add a communication
					ao.addCommunication(sourceID);
				}
			}
			// --- RequestQueueEvent ----------------
			else if(type.equals(NotificationType.addRequest)){
				//System.out.println("...............................Add a request");
				//ao.addRequest();
			}
			else if(type.equals(NotificationType.removeRequest)){
				//System.out.println("...............................Remove a request");
				//ao.removeRequest();
			}
			else if(type.equals(NotificationType.waitForRequest)){
				//System.out.println("...............................Wait for request");
				ao.setState(State.WAITING_FOR_REQUEST);
				ao.setRequestQueueLength(0);
			}


			// --- MessageEvent ---------------------
			else if(type.equals(NotificationType.replyReceived)){
				System.out.println("...............................Reply received : "+ao.getName());
				// Do Nothing
			}
			else if(type.equals(NotificationType.replySent)){
				System.out.println("...............................Reply sent : "+ao.getName());
				ao.setState(State.ACTIVE);
				Integer requestQueueLength = (Integer) notification.getUserData();
				ao.setRequestQueueLength(requestQueueLength);
			}
			else if(type.equals(NotificationType.requestSent)){
				System.out.println("...............................Request sent : "+ao.getName());
				// Do Nothing
			}
			else if(type.equals(NotificationType.servingStarted)){
				System.out.println("...............................Serving started : "+ao.getName());
				ao.setState(State.SERVING_REQUEST);
				Integer requestQueueLength = (Integer) notification.getUserData();
				ao.setRequestQueueLength(requestQueueLength);
			}
			else if(type.equals(NotificationType.voidRequestServed)){
				System.out.println("...............................Void request served : "+ao.getName());
				ao.setState(State.ACTIVE);
				Integer requestQueueLength = (Integer) notification.getUserData();
				ao.setRequestQueueLength(requestQueueLength);
			}


			// --- MigrationEvent -------------------
			else if(type.equals(NotificationType.migratedBodyRestarted)){
				System.out.println("...............................Migration body restarted : "+ao.getName());
			}
			else if(type.equals(NotificationType.migrationAboutToStart)){
				System.out.println("...............................Migration about to start "+ao+", node="+ao.getParent());
				ao.setState(State.MIGRATING);
			}
			else if(type.equals(NotificationType.migrationExceptionThrown)){
				System.out.println("...............................Migration Exception thrown : "+ao.getName());
				ao.migrationFailed((MigrationException)notification.getUserData());
			}
			else if(type.equals(NotificationType.migrationFinished)){
				System.out.println("...............................Migration finished : "+ao.getName());
			}
			else		
				System.out.println(ao.getName()+" => "+type);
		}		
		/*}
		}.start();*/
	}
}
