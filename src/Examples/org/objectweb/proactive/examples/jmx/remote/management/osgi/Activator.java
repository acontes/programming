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
package org.objectweb.proactive.examples.jmx.remote.management.osgi;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.objectweb.proactive.examples.jmx.remote.management.jmx.IJmx;
import org.objectweb.proactive.examples.jmx.remote.management.mbean.OSGiFramework;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 *
 * @author The ProActive Team
 *
 */
public class Activator implements BundleActivator {
    private BundleContext context;
    private MBeanServer mbs;
    private ObjectName on;
    private OSGiFramework osgi;

    /**
     *
     */
    public void start(BundleContext ctx) throws Exception {
        this.mbs = ManagementFactory.getPlatformMBeanServer();
        this.context = ctx;

        try {
            this.osgi = new OSGiFramework(this.context);
            ((IJmx) this.osgi).register();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void stop(BundleContext arg0) throws Exception {
        ((IJmx) this.osgi).unregister();
    }
}
