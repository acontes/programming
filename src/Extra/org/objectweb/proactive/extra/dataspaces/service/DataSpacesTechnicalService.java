/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces.service;

import java.net.URISyntaxException;
import java.util.Map;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.descriptor.services.TechnicalService;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.DataSpacesNodes;
import org.objectweb.proactive.extra.dataspaces.exceptions.AlreadyConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;


/**
 * TechnicalService that configures ProActive node to support Data Spaces on the base of specified
 * properties and local ProActive's properties.
 */
public class DataSpacesTechnicalService implements TechnicalService {

    private static final long serialVersionUID = -6664368270086233701L;

    public static final String PROPERTY_NAMING_SERVICE_URL = "proactive.dataspaces.naming_service_url";

    private static final String LOG_MSG_CONFIGURATION1_FAILS = "Exception while applying configuration on a node, see internal message for details";

    private static final String LOG_MSG_CONFIGURATION2_FAILS = "Exception while applying application specific configuration, see internal message for details";

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES_CONFIGURATOR);

    private String namingServiceURL;

    /**
     * Configures Data Spaces for an application on a node with configuration specified properties
     * and local ProActive's properties.
     * <p>
     * FIXME: Data Spaces cannot be already configured not for a node nor for an application, which
     * is related to GCM deployment specification and lack of acquisition specification yet. After
     * this is done, reconfiguration should be allowed.
     **/
    public void apply(final Node node) {
        BaseScratchSpaceConfiguration baseScratchConfiguration = readConfigurationFromProperties();

        try {
            DataSpacesNodes.configureNode(node, baseScratchConfiguration);
        } catch (AlreadyConfiguredException e) {
            logger.error(LOG_MSG_CONFIGURATION1_FAILS, e);
            return;
        } catch (ConfigurationException e) {
            logger.error(LOG_MSG_CONFIGURATION1_FAILS, e);
            return;
        } catch (FileSystemException e) {
            logger.error(LOG_MSG_CONFIGURATION1_FAILS, e);
            return;
        }

        // FIXME this code depends on PROACTIVE-661 story with application id, as configureApplication assumes
        // that application id is already set up accrodingly
        try {
            DataSpacesNodes.configureApplication(node, namingServiceURL);
        } catch (NotConfiguredException e) {
            // will not happen
            ProActiveLogger.logImpossibleException(logger, e);
            throw new ProActiveRuntimeException(e);
        } catch (FileSystemException e) {
            logger.error(LOG_MSG_CONFIGURATION2_FAILS, e);
        } catch (ProActiveException e) {
            logger.error(LOG_MSG_CONFIGURATION2_FAILS, e);
        } catch (URISyntaxException e) {
            logger.error(LOG_MSG_CONFIGURATION2_FAILS, e);
        }
        final String runtimeURL = node.getProActiveRuntime().getURL();
        final String nodeName = node.getNodeInformation().getName();
        final ObjectName mBeanObjectName = FactoryName.createNodeObjectName(runtimeURL, nodeName);
        JMXNotificationManager.getInstance().subscribe(mBeanObjectName, new NotificationListener() {

            public void handleNotification(Notification notification, Object handback) {
                if (!notification.getType().equals(NotificationType.nodeDestroyed))
                    return;

                // TODO unsubscribe
                try {
                    DataSpacesNodes.closeNodeConfig(node);
                } catch (NotConfiguredException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * Requires NamingService URL property set by {@link #PROPERTY_NAMING_SERVICE_URL}.
     **/
    public void init(Map<String, String> argValues) {
        namingServiceURL = argValues.get(PROPERTY_NAMING_SERVICE_URL);

        if (namingServiceURL == null)
            throw new IllegalArgumentException(
                "Provided properties are incomplete, NamingService URL must be specified.");
    }

    private BaseScratchSpaceConfiguration readConfigurationFromProperties() {
        final String scratchURL = PAProperties.PA_DATASPACES_SCRATCH_PATH.getValue();
        final String scratchPath = PAProperties.PA_DATASPACES_SCRATCH_URL.getValue();

        if (scratchURL == null && scratchPath == null) {
            return null;
        }
        try {
            return new BaseScratchSpaceConfiguration(scratchURL, scratchPath);
        } catch (ConfigurationException e) {
            ProActiveLogger.logImpossibleException(logger, e);
            return null;
        }
    }
}
