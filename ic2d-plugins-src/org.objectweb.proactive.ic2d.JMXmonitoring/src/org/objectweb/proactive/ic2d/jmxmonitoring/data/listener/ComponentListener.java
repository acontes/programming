//package org.objectweb.proactive.ic2d.jmxmonitoring.data.listener;
//
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//import javax.management.Notification;
//
//import org.apache.log4j.Logger;
//import org.objectweb.proactive.core.body.migration.MigrationException;
//import org.objectweb.proactive.core.jmx.notification.NotificationType;
//import org.objectweb.proactive.core.jmx.notification.RequestNotificationData;
//import org.objectweb.proactive.core.util.log.Loggers;
//import org.objectweb.proactive.core.util.log.ProActiveLogger;
//import org.objectweb.proactive.ic2d.jmxmonitoring.data.ComponentModel;
//import org.objectweb.proactive.ic2d.jmxmonitoring.data.listener.ActiveObjectListener.Task;
//import org.objectweb.proactive.ic2d.jmxmonitoring.data.listener.ActiveObjectListener.Type;
//import org.objectweb.proactive.ic2d.jmxmonitoring.util.IC2DThreadPool;
//
//public class ComponentListener{
//	
//	
//	 ////////  End -- Task for handling notifications ///////
//    private ComponentModel cm;
//
//    //private String name;
//    public ComponentListener(ComponentModel cm) {
//        this.cm = cm;
//    }
//    
//    @SuppressWarnings("unchecked")
//    public void handleNotification(Notification notifications, Object handback) {
//        ConcurrentLinkedQueue notifs = (ConcurrentLinkedQueue<Notification>) notifications.getUserData();
//        IC2DThreadPool.execute(new Task(notifs));
//    }
//
//    
//    private transient Logger logger = ProActiveLogger.getLogger(Loggers.JMX_NOTIFICATION);
//
//    private enum Type {
//        SENDER, RECEIVER;
//    }
//
//    ////////   Begin -- Task for handling notifications ///////
//    private class Task implements Runnable {
//        private ConcurrentLinkedQueue<Notification> notifications;
//
//        public Task(ConcurrentLinkedQueue<Notification> notifications) {
//            this.notifications = notifications;
//        }
//
//        public void run() {
//            for (Notification notification : notifications) {
//                String type = notification.getType();
//
//                if (type.equals(NotificationType.requestReceived)) {
//                    logger.debug(".................................Request Received : " + cm.getName());
//                    RequestNotificationData request = (RequestNotificationData) notification.getUserData();
//                    addRequest(request, ao, Type.RECEIVER);
//                } else if (type.equals(NotificationType.waitForRequest)) {
//                    logger.debug("...............................Wait for request");
//                    ao.setState(org.objectweb.proactive.ic2d.jmxmonitoring.util.State.WAITING_FOR_REQUEST);
//                    ao.setRequestQueueLength(0);
//                }
//                // --- MessageEvent ---------------------
//                else if (type.equals(NotificationType.replyReceived)) {
//                    logger.debug("...............................Reply received : " + ao.getName());
//                } else if (type.equals(NotificationType.replySent)) {
//                    logger.debug("...............................Reply sent : " + ao.getName());
//                    ao.setState(org.objectweb.proactive.ic2d.jmxmonitoring.util.State.ACTIVE);
//                    Integer requestQueueLength = (Integer) notification.getUserData();
//                    ao.setRequestQueueLength(requestQueueLength);
//                } else if (type.equals(NotificationType.requestSent)) {
//                    logger.debug("...............................Request sent : " + ao.getName());
//                    RequestNotificationData request = (RequestNotificationData) notification.getUserData();
//                    addRequest(request, ao, Type.SENDER);
//                } else if (type.equals(NotificationType.servingStarted)) {
//                    logger.debug("...............................Serving started : " + ao.getName());
//                    ao.setState(org.objectweb.proactive.ic2d.jmxmonitoring.util.State.SERVING_REQUEST);
//                    Integer requestQueueLength = (Integer) notification.getUserData();
//                    ao.setRequestQueueLength(requestQueueLength);
//                } else if (type.equals(NotificationType.voidRequestServed)) {
//                    logger.debug("...............................Void request served : " + ao.getName());
//                    ao.setState(org.objectweb.proactive.ic2d.jmxmonitoring.util.State.ACTIVE);
//                    Integer requestQueueLength = (Integer) notification.getUserData();
//                    ao.setRequestQueueLength(requestQueueLength);
//                }
//                // --- MigrationEvent -------------------
//                else if (type.equals(NotificationType.migratedBodyRestarted)) {
//                    logger.debug("...............................Migration body restarted : " + ao.getName());
//                } else if (type.equals(NotificationType.migrationAboutToStart)) {
//                    logger.debug("...............................Migration about to start " + ao + ", node=" +
//                        ao.getParent());
//                    ao.setState(org.objectweb.proactive.ic2d.jmxmonitoring.util.State.MIGRATING);
//                } else if (type.equals(NotificationType.migrationExceptionThrown)) {
//                    logger.debug("...............................Migration Exception thrown : " +
//                        ao.getName());
//                    ao.migrationFailed((MigrationException) notification.getUserData());
//                } else if (type.equals(NotificationType.migrationFinished)) {
//                    logger.debug("...............................Migration finished : " + ao.getName());
//                }
//                // --- FuturEvent -------------------
//                else if (type.equals(NotificationType.waitByNecessity)) {
//                    logger.debug("...............................Wait By Necessity : " + ao.getName());
//                    ao.setState(org.objectweb.proactive.ic2d.jmxmonitoring.util.State.WAITING_BY_NECESSITY);
//                } else if (type.equals(NotificationType.receivedFutureResult)) {
//                    logger.debug("...............................Received Future Result : " + ao.getName());
//                    ao.setState(org.objectweb.proactive.ic2d.jmxmonitoring.util.State.RECEIVED_FUTURE_RESULT);
//                } else {
//                    System.out.println(ao.getName() + " => " + type);
//                }
//            }
//        }
//    }
//
//
//}
