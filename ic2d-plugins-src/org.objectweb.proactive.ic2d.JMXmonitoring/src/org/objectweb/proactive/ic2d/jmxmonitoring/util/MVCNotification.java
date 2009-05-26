/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.util;

/**
 *  MVCNotifications are used in the IC2D MVC Pattern.
 *  The model objects send MVCNotification which are treated by the EditParts.
 * @author The ProActive Team
 *
 */
public class MVCNotification {
    private MVCNotificationTag notification;
    private Object data;

    public MVCNotification(MVCNotificationTag notif, Object data) {
        this.notification = notif;
        this.data = data;
    }

    public MVCNotification(MVCNotificationTag notif) {
        this.notification = notif;
        this.data = null;
    }

    public MVCNotificationTag getMVCNotification() {
        return notification;
    }

    public Object getData() {
        return data;
    }
}