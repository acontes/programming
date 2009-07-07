/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces.service;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.descriptor.services.TechnicalService;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.TechnicalServicesProperties;
import org.objectweb.proactive.extra.dataspaces.api.PADataSpaces;
import org.objectweb.proactive.extra.dataspaces.core.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.core.DataSpacesNodes;
import org.objectweb.proactive.extra.dataspaces.exceptions.AlreadyConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;


/**
 * TechnicalService that configures ProActive node to support Data Spaces.
 * <p>
 * Configuration is read from two sources:
 * <ul>
 * <li>Node level configuration of scratch space is read from local runtime ProActive's properties (
 * {@link PAProperties#PA_DATASPACES_SCRATCH_URL} and
 * {@link PAProperties#PA_DATASPACES_SCRATCH_PATH})</li>
 * <li>Application level configuration is read from technical service properties (
 * {@link #PROPERTY_NAMING_SERVICE_URL}) and Node properties (application id).</li>
 * </ul>
 * <p>
 * This implementation sets up Data Spaces in {@link #apply(Node)} and cleans up after Data Spaces
 * objects when Node is being destroyed. After Data Spaces are configured, user can safely use
 * {@link PADataSpaces} API.
 */
public class DataSpacesTechnicalService implements TechnicalService {

    private static final long serialVersionUID = -6664368270086233701L;

    public static final String PROPERTY_NAMING_SERVICE_URL = "proactive.dataspaces.naming_service_url";

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES_CONFIGURATOR);

    private String namingServiceURL;

    /**
     * Create technical service properties for given configuration that should initialize properly
     * this class.
     * 
     * @param namingServiceURL
     *            URL of Naming Service
     * @return technical service properties for given configuration
     */
    public static TechnicalServicesProperties createTechnicalServiceProperties(final String namingServiceURL) {
        final HashMap<String, String> dataSpacesProperties = new HashMap<String, String>();
        dataSpacesProperties.put(DataSpacesTechnicalService.PROPERTY_NAMING_SERVICE_URL, namingServiceURL);

        final HashMap<String, HashMap<String, String>> techServicesMap = new HashMap<String, HashMap<String, String>>();
        techServicesMap.put(DataSpacesTechnicalService.class.getName(), dataSpacesProperties);
        return new TechnicalServicesProperties(techServicesMap);
    }

    // TODO What about polices determining whether to leave data in scratch or remove all directories during unexpected end of application. 
    private static void closeNodeConfigIgnoreException(final Node node) {
        try {
            DataSpacesNodes.closeNodeConfig(node);
        } catch (NotConfiguredException x) {
            ProActiveLogger.logImpossibleException(logger, x);
        }
    }

    /**
     * Configures Data Spaces for an application on a node with configuration specified properties
     * and local ProActive's properties.
     * <p>
     * FIXME: Data Spaces cannot be already configured nor for a node nor for an application, which
     * is related to GCM deployment specification and lack of acquisition specification yet. After
     * it is implemented in GCM deployment, reconfiguration should be allowed.
     **/
    public void apply(final Node node) {
        if (!isProperlyInitialized())
            return;

        final BaseScratchSpaceConfiguration baseScratchConfiguration = readScratchConfiguration();
        try {
            DataSpacesNodes.configureNode(node, baseScratchConfiguration);
        } catch (AlreadyConfiguredException e) {
            ProActiveLogger.logImpossibleException(logger, e);
            // FIXME: it may happen when node acquisition will be implemented - and we have to handle that
            // in slightly better way ;) (see comment in javadoc)
            // ...or during more than one GCMA deployment on one machine if this TS is applied on local node 
            return;
        } catch (ConfigurationException e) {
            logger.error("Could not configure Data Spaces. Possible configuration problem.", e);
            return;
        } catch (FileSystemException e) {
            logger.error("Could not initialize scratch space for a node - I/O error.", e);
            return;
        }

        // we assume that node's application id is already set up accordingly in this moment
        try {
            DataSpacesNodes.configureApplication(node, namingServiceURL);
        } catch (NotConfiguredException e) {
            // it should not happen as we configure it above
            ProActiveLogger.logImpossibleException(logger, e);
            closeNodeConfigIgnoreException(node);
            return;
        } catch (URISyntaxException e) {
            // it should not happen as we check that on deployer
            ProActiveLogger.logImpossibleException(logger, e);
            closeNodeConfigIgnoreException(node);
            return;
        } catch (FileSystemException e) {
            logger.error("Could not initialize scratch space for an application on a node - I/O error.", e);
            closeNodeConfigIgnoreException(node);
            return;
        } catch (ProActiveException e) {
            logger.error("Could not contact Naming Service specified by an application.", e);
            closeNodeConfigIgnoreException(node);
            return;
        }

        registerNotificationListener(node);
    }

    /**
     * Requires NamingService URL property: {@link #PROPERTY_NAMING_SERVICE_URL}.
     **/
    public void init(Map<String, String> argValues) {
        namingServiceURL = argValues.get(PROPERTY_NAMING_SERVICE_URL);

        if (namingServiceURL == null) {
            logger
                    .error("Initialization error - provided TS properties are incomplete, NamingService URL is not specified.");
        }
    }

    private boolean isProperlyInitialized() {
        return namingServiceURL != null;
    }

    private void registerNotificationListener(final Node node) {
        final String runtimeURL = node.getProActiveRuntime().getURL();
        final ObjectName mBeanObjectName = FactoryName.createRuntimeObjectName(runtimeURL);
        final NotificationListener notificationListener = new NotificationListener() {

            public void handleNotification(Notification notification, Object handback) {
                final String type = notification.getType();
                final Object userData = notification.getUserData();

                if (type.equals(NotificationType.nodeDestroyed) &&
                    userData.equals(node.getNodeInformation().getURL())) {
                    // FIXME: it seems that subscribe/unsubscribe is buggy: depends on PROACTIVE-687 
                    JMXNotificationManager.getInstance().unsubscribe(mBeanObjectName, this);
                    closeNodeConfigIgnoreException(node);
                }
            }

        };
        JMXNotificationManager.getInstance().subscribe(mBeanObjectName, notificationListener);
    }

    private BaseScratchSpaceConfiguration readScratchConfiguration() {
        final String scratchPath = PAProperties.PA_DATASPACES_SCRATCH_PATH.getValue();
        final String scratchURL = PAProperties.PA_DATASPACES_SCRATCH_URL.getValue();

        if (scratchURL == null && scratchPath == null) {
            logger.warn("No scratch space configuration specified for this node.");
            return null;
        }
        try {
            return new BaseScratchSpaceConfiguration(scratchURL, scratchPath);
        } catch (ConfigurationException e) {
            // it should not happen as we check it above
            ProActiveLogger.logImpossibleException(logger, e);
            return null;
        }
    }
}