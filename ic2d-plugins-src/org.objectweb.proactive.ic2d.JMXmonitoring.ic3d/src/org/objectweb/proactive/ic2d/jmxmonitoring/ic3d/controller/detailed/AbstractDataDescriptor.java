package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.objectweb.proactive.ic2d.chartit.data.provider.IDataProvider;
import org.objectweb.proactive.ic2d.chartit.data.resource.IResourceDescriptor;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;

/**
 * @author <a href="mailto:support@activeeon.com">ActiveEon Team</a>.
 * 
 */
public class AbstractDataDescriptor implements IResourceDescriptor {
    /**
     * The abstract data described as a resource
     */
    final AbstractData abstractData;

    /**
     * Some custom data providers
     */
    final IDataProvider[] customProviders;

    /**
     * Creates a new instance of <code>AbstractDataDescriptor</code>
     * 
     * @param abstractData
     *            The abstract data described as a resource
     * @throws IOException
     *             Thrown if a problem occurred during custom providers
     *             creation
     */
    public AbstractDataDescriptor(final AbstractData abstractData) throws IOException {
        this.abstractData = abstractData;
        this.customProviders = new IDataProvider[0];
    }

    public String getHostUrlServer() {
        return this.abstractData.getHostUrlServer();
    }

    public MBeanServerConnection getMBeanServerConnection() {
        return this.abstractData.getMBeanServerConnection();
    }

    public String getName() {
        return this.abstractData.getName();
    }

    public ObjectName getObjectName() {
        return this.abstractData.getObjectName();
    }

    public IDataProvider[] getCustomDataProviders() {
        return this.customProviders;
    }
}