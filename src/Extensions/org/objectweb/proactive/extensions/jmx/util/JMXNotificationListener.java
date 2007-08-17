/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extensions.jmx.util;

import java.io.IOException;
import java.io.Serializable;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.jmx.ProActiveConnection;


/**
 * An IC2DListener is an active object which listens several remotes MBeans with the ProActiveConnection.
 * This listener is used by the JMXNotificationManager.
 * @author ProActive Team
 */
public class JMXNotificationListener implements NotificationListener,
    ProActiveInternalObject, Serializable {
    private Logger logger = ProActiveLogger.getLogger(Loggers.JMX);

    public JMXNotificationListener() {
        // Empty Constructor
    }

    /**
     * Subscribes the current active object to the JMX notifications of a remote MBean.
     * @param connection The ProActiveConnection in order to connect to the remote server MBean.
     * @param oname The ObjectName of the MBean
     * @param filter A notification filter
     * @param handback A hanback
     */
    public void subscribe(ProActiveConnection connection, ObjectName oname,
        NotificationFilter filter, Object handback) {
        try {
            connection.addNotificationListener(oname,
                (NotificationListener) ProActive.getStubOnThis(), filter,
                handback);
        } catch (InstanceNotFoundException e) {
            logger.error("Doesn't find the object name " + oname +
                " during the registration", e);
        } catch (IOException e) {
            logger.error("Doesn't subscribe the JMX Notification listener to the Notifications",
                e);
        }
    }

    /**
     * Unsubscribes the current active object to the JMX notifications of a remote MBean.
     * @param connection The ProActiveConnection in order to connect to the remote server MBean.
     * @param oname The ObjectName of the MBean
     * @param filter A notification filter
     * @param handback A hanback
     */
    public void unsubscribe(ProActiveConnection connection, ObjectName oname,
        NotificationFilter filter, Object handback) {
        try {
            if (connection.isRegistered(oname)) {
                connection.removeNotificationListener(oname,
                    (NotificationListener) ProActive.getStubOnThis(), filter,
                    handback);
            }
        } catch (InstanceNotFoundException e) {
            logger.error("Doesn't find the object name " + oname +
                " during the registration", e);
        } catch (ListenerNotFoundException e) {
            logger.error("Doesn't find the Notification Listener", e);
        } catch (IOException e) {
            logger.error("Can't unsubscribe the JMX Notification listener to the Notifications",
                e);
        }
    }

    public void handleNotification(Notification notification, Object handback) {
        JMXNotificationManager.getInstance()
                              .handleNotification(notification, handback);
    }
}
