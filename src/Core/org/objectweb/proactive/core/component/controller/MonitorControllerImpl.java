package org.objectweb.proactive.core.component.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.notification.RequestNotificationData;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public class MonitorControllerImpl extends AbstractProActiveController implements MonitorController,
        NotificationListener {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_CONTROLLERS);

    private final String KEY_INFO_SEPARATOR = "-";

    private JMXNotificationManager jmxNotificationManager;

    private boolean started;

    private Map<String, MethodStatistics> statistics;

    private Map<String, String> keysList;

    public MonitorControllerImpl(Component owner) {
        super(owner);
        jmxNotificationManager = JMXNotificationManager.getInstance();
        //registerMethods();
        //startMonitoring();
    }

    public void registerMethods() {
        statistics = Collections.synchronizedMap(new HashMap<String, MethodStatistics>());
        keysList = new HashMap<String, String>();
        NameController nc = null;
        try {
            nc = (NameController) owner.getFcInterface(Constants.NAME_CONTROLLER);
        } catch (NoSuchInterfaceException e1) {
            e1.printStackTrace();
        }
        String name = nc.getFcName();
        InterfaceType itfTypes[] = ((ComponentType) owner.getFcType()).getFcInterfaceTypes();
        for (InterfaceType itfType : itfTypes) {
            try {
                Class<?> klass = ClassLoader.getSystemClassLoader().loadClass(itfType.getFcItfSignature());
                Method[] methods = klass.getDeclaredMethods();
                if (!itfType.getFcItfName().endsWith("-controller") &&
                    !itfType.getFcItfName().equals("component")) {
                    for (Method m : methods) {
                        if (!itfType.isFcClientItf()) {
                            Class<?>[] parametersTypes = m.getParameterTypes();
                            String key = generateKey(itfType.getFcItfName(), m.getName(), parametersTypes)
                                    .stringValue();
                            keysList.put(m.getName(), key);
                            statistics.put(key, new MethodStatisticsImpl(itfType.getFcItfName(), m.getName(),
                                parametersTypes));
                            logger.debug(m.getName() + " (server) added to monitoring on component " + name +
                                "!!!");
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected void setControllerItfType() {
        try {
            setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(Constants.MONITOR_CONTROLLER,
                    MonitorController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                    TypeFactory.SINGLE));
        } catch (InstantiationException e) {
            throw new ProActiveRuntimeException("cannot create controller " + this.getClass().getName());
        }
    }

    public BooleanWrapper isMonitoringStarted() {
        return new BooleanWrapper(started);
    }

    private void initMethodStatistics() {
        String[] keys = statistics.keySet().toArray(new String[] {});
        for (int i = 0; i < keys.length; i++) {
            ((MethodStatisticsImpl) statistics.get(keys[i])).reset();
        }
    }

    public void startMonitoring() {
        if (!started) {
            initMethodStatistics();
            try {
                jmxNotificationManager.subscribe(FactoryName.createActiveObjectName(PAActiveObject
                        .getBodyOnThis().getID()), this, FactoryName.getCompleteUrl(ProActiveRuntimeImpl
                        .getProActiveRuntime().getURL()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            started = true;
        }
    }

    public void stopMonitoring() {
        if (started) {
            jmxNotificationManager.unsubscribe(FactoryName.createActiveObjectName(PAActiveObject
                    .getBodyOnThis().getID()), this);
            started = false;
        }
    }

    public void resetMonitoring() {
        stopMonitoring();
        startMonitoring();
    }

    public MethodStatistics getStatistics(String itfName, String methodName) throws Exception {
        return getStatistics(itfName, methodName, new Class<?>[] {});
    }

    public MethodStatistics getStatistics(String itfName, String methodName, Class<?>[] parametersTypes)
            throws Exception {
        String supposedCorrespondingKey = generateKey(itfName, methodName, parametersTypes).stringValue();
        MethodStatistics methodStats = statistics.get(supposedCorrespondingKey);
        if (methodStats != null)
            return methodStats;
        else if (parametersTypes.length == 0) {
            String correspondingKey = null;
            String[] keys = statistics.keySet().toArray(new String[] {});
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].startsWith(supposedCorrespondingKey)) {
                    if (correspondingKey == null)
                        correspondingKey = keys[i];
                    else
                        // TODO Raise an ambiguous method exception?
                        return null;
                }
            }
            if (correspondingKey != null)
                return statistics.get(correspondingKey);
            else
                // TODO Raise an method not found exception?
                return null;
        } else
            // TODO Raise an method not found exception?
            return null;
    }

    public Map<String, MethodStatistics> getAllStatistics() {
        return statistics;
    }

    public StringWrapper generateKey(String itfName, String methodName, Class<?>[] parametersTypes) {
        String key = itfName + KEY_INFO_SEPARATOR + methodName;

        for (int i = 0; i < parametersTypes.length; i++) {
            key += KEY_INFO_SEPARATOR + parametersTypes[i].getName();
        }

        return new StringWrapper(key);
    }

    public void handleNotification(Notification notification, Object handback) {
        String type = notification.getType();
        if (type.equals(NotificationType.requestReceived)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            String key = keysList.get(data.getMethodName());
            if (key != null)
                ((MethodStatisticsImpl) statistics.get(key)).notifyArrivalOfRequest(notification
                        .getTimeStamp());
        } else if (type.equals(NotificationType.servingStarted)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            String key = keysList.get(data.getMethodName());
            if (key != null)
                ((MethodStatisticsImpl) statistics.get(key)).notifyDepartureOfRequest(notification
                        .getTimeStamp());
        } else if (type.equals(NotificationType.replySent)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            String key = keysList.get(data.getMethodName());
            if (key != null)
                ((MethodStatisticsImpl) statistics.get(key)).notifyReplyOfRequestSent(notification
                        .getTimeStamp());
        } else if (type.equals(NotificationType.voidRequestServed)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            String key = keysList.get(data.getMethodName());
            if (key != null)
                ((MethodStatisticsImpl) statistics.get(key)).notifyReplyOfRequestSent(notification
                        .getTimeStamp());
        } else if (type.equals(NotificationType.setOfNotifications)) {
            ConcurrentLinkedQueue<Notification> notificationsList = (ConcurrentLinkedQueue<Notification>) notification
                    .getUserData();
            for (Iterator<Notification> iterator = notificationsList.iterator(); iterator.hasNext();) {
                handleNotification((Notification) iterator.next(), handback);
            }
        }
    }
}
