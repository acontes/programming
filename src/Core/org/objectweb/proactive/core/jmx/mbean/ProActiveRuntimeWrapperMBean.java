package org.objectweb.proactive.core.jmx.mbean;

import java.io.Serializable;
import java.util.List;

import javax.management.ObjectName;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.security.securityentity.Entity;
import org.objectweb.proactive.core.jmx.notification.NotificationType;


/**
 * MBean representing a ProActiveRuntime.
 * @author ProActiveRuntime
 */
public interface ProActiveRuntimeWrapperMBean extends Serializable {

    /**
     * Returns the url of the ProActiveRuntime associated.
     * @return The url of the ProActiveRuntime associated.
     */
    public String getURL();

    /**
     * Returns a list of Object Name used by the MBeans of the nodes containing in the ProActive Runtime.
     * @return a list of Object Name used by the MBeans of the nodes containing in the ProActive Runtime.
     * @throws ProActiveException
     */
    public List<ObjectName> getNodes() throws ProActiveException;

    /**
     * Sends a new notification.
     * @param type The type of the notification. See {@link NotificationType}
     */
    public void sendNotification(String type);

    /**
     * Sends a new notification.
     * @param type Type of the notification. See {@link NotificationType}
     * @param userData The user data.
     */
    public void sendNotification(String type, Object userData);

    /**
     * Returns the object name used for this MBean.
     * @return The object name used for this MBean.
     */
    public ObjectName getObjectName();

    /**
     * Kills this ProActiveRuntime.
     * @exception Exception if a problem occurs when killing this ProActiveRuntime
     */
    public void killRuntime() throws Exception;

    /**
     * Returns the security manager.
     * @param user
     * @return the security manager
     */
    public ProActiveSecurityManager getSecurityManager(Entity user);

    /**
     * Modify the security manager.
     * @param user
     * @param policyServer
     */
    public void setSecurityManager(Entity user, PolicyServer policyServer);
}
