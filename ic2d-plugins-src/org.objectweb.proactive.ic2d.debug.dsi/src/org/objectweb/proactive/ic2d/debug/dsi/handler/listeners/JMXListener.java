package org.objectweb.proactive.ic2d.debug.dsi.handler.listeners;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.management.Notification;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.notification.RequestNotificationData;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.debug.dsi.Activator;
import org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IJMXListenerExtPoint;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.IC2DThreadPool;


public class JMXListener implements Serializable, IJMXListenerExtPoint {

    private static final long serialVersionUID = -4438864290999520319L;

    //private DSIHandler handler;

    public JMXListener() {
        Console.getInstance(Activator.CONSOLE_NAME).log("JMX DSI Listener ON");
        //this.handler = DSIHandler.getInstance();
    }

    @SuppressWarnings("unchecked")
    public void handleNotification(Notification notification, Object handback){
        // Get the type of the notification
        String type = notification.getType();

        if (type.equals(NotificationType.setOfNotifications)){
            ConcurrentLinkedQueue<Notification> notifications = (ConcurrentLinkedQueue<Notification>)notification.getUserData();
            IC2DThreadPool.execute(new Task(notifications));
        }
    }

    ////////Begin -- Task for handling notifications ///////
    private class Task implements Runnable {
        private ConcurrentLinkedQueue<Notification> notifications;

        public Task(ConcurrentLinkedQueue<Notification> notifications) {
            this.notifications = notifications;
        }

        public void run() {
            for (Notification notification : notifications) {
                    if (notification.getType().equals(NotificationType.requestSent) ||
                            notification.getType().equals(NotificationType.requestReceived) ||
                            notification.getType().equals(NotificationType.replySent) ||
                            notification.getType().equals(NotificationType.replyReceived) ) {
                            
                        RequestNotificationData data = (RequestNotificationData) notification.getUserData();
                        String method = data.getMethodName(); 
                        if( method != null && !method.equals("handleNotification") ){
                            setRequest(notification.getType(), (RequestNotificationData) notification.getUserData(), notification.getSequenceNumber());
                        }
                }
            }
        }
        
        private void setRequest(String type, RequestNotificationData request, long seqNumber) {
            UniqueID idSrc = request.getSource();
            UniqueID idDst = request.getDestination();
            String method = request.getMethodName();
            long seq = request.getSequenceNumber();
            //handler.addService(idSrc, idDst, type, method, seq, request.getTags());
            Console.getInstance(Activator.CONSOLE_NAME).log("Service added :" + method);
        }
        
    }

}