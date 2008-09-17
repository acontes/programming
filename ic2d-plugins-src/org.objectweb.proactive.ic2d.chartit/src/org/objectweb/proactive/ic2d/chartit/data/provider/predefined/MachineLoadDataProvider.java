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
 *  Contributor(s): ActiveEon Team - http://www.activeeon.com
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.chartit.data.provider.predefined;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.io.RandomAccessFile;
import java.lang.management.OperatingSystemMXBean;

import javax.management.MBeanServerConnection;

import org.objectweb.proactive.ic2d.chartit.data.provider.IDataProvider;


/**
 * Used to get the load of the machine this runtime is running on.<br/>
 * 
 * WARNING: only available from Java 1.6 !!!!!
 * 
 * @author <a href="mailto:support@activeeon.com">ActiveEon Team</a>.
 */
public class MachineLoadDataProvider implements IDataProvider {

    public static final String NAME = "MachineLoad";
    public static final String DESCRIPTION = "Load of the Machine this is running on";
    public static final String TYPE = "double";
    public static int osType = 0;
    public static String fileName;
    public static RandomAccessFile in;
    
    /**
     * The reference on the mbean
     */
    private final OperatingSystemMXBean mBean;

    /**
     * Builds a new instance of MachineLoadDataProvider class.
     *
     * @param mBean
     *            The reference on mbean
     */
    public MachineLoadDataProvider() {
        this(ManagementFactory.getOperatingSystemMXBean());
    }

    public MachineLoadDataProvider(final OperatingSystemMXBean mBean) {
        this.mBean = mBean;

        String osName = System.getProperty("os.name");
        if(osName.contains("Linux")) {
                osType = 0;

        }
        else if(osName.contains("Windows")) {
                osType = 1;
                // load appropriate DLL (I've got this in the windows version, on the laptop, GLUP!)

        }
        // unknown
        else {
                osType = 2;
        }
    }

    public MachineLoadDataProvider(final MBeanServerConnection mBeanServerConnection) throws IOException {
        this(ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection,
                ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.chronolog.data.provider.IDataProvider#provideValue()
     */
    public Object provideValue() {

        double value = -1.0;
        // Linux
        // the method mbean.getSystemLoadAverage is not available until Java 1.6
        if(osType == 0) {
        		// this method is only available from Java 1.6 (and doesn't work in Windows)
        		return this.mBean.getSystemLoadAverage();
        }
        // TODO: add the code for handling the case of windows (from the laptop)
        return 0;

        
    }

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.chronolog.data.provider.IDataProvider#getName()
     */
    public String getName() {
        return MachineLoadDataProvider.NAME;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.chronolog.data.provider.IDataProvider#getDescription()
     */
    public String getDescription() {
        return MachineLoadDataProvider.DESCRIPTION;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.chronolog.data.provider.IDataProvider#getType()
     */
    public String getType() {
        return MachineLoadDataProvider.TYPE;
    }

    // /////////////////////////////////////////////
    // Static methods for local and remote creation
    // /////////////////////////////////////////////

    /**
     * Returns the reference on the remote MBean
     *
     * @param mBeanServerConnection
     *            The connection to the remote MBean server
     * @return The reference on the remote MBean
     */
    public static MachineLoadDataProvider build(final MBeanServerConnection mBeanServerConnection) {
        if (mBeanServerConnection == null) {
            return new MachineLoadDataProvider(ManagementFactory.getOperatingSystemMXBean());
        }
        try {
            return new MachineLoadDataProvider(ManagementFactory.newPlatformMXBeanProxy(
                    mBeanServerConnection, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class));
        } catch (Exception e) {
            // TODO : log the exception
            e.printStackTrace();
        }
        return null;

    }

    
    
    
    
    
    
    
    
    
    
   
}
