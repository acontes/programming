/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces.service;

import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.descriptor.services.TechnicalService;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.DataSpacesNodes;
import org.objectweb.proactive.extra.dataspaces.exceptions.AlreadyConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;


// FIXME PROACTIVE-661 story with application id should be resolved somehow, but probably not through this TS
/**
 * TechnicalService that configures ProActive node to support Data Spaces on the base of specified
 * properties and local ProActive's properties.
 */
public class DataSpacesTechnicalService implements TechnicalService {

    public static final String PROPERTY_NAMING_SERVICE_URL = "proactive.dataspaces.naming_service_url";

    private static final String LOG_MSG_CONFIGURATION0_FAILS = "Exception while checking configuration on a node, see internal message for details";

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
    public void apply(Node node) {
        BaseScratchSpaceConfiguration baseScratchConfiguration;
        try {
            baseScratchConfiguration = readConfigurationFromProperties();
        } catch (ConfigurationException e1) {
            logger.error(LOG_MSG_CONFIGURATION0_FAILS, e1);
            return;
        }

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

    private BaseScratchSpaceConfiguration readConfigurationFromProperties() throws ConfigurationException {
        final String scratchURL = PAProperties.PA_DATASPACES_SCRATCH_PATH.getValue();
        final String scratchPath = PAProperties.PA_DATASPACES_SCRATCH_URL.getValue();

        if (scratchPath == null || scratchURL == null)
            return null;
        return new BaseScratchSpaceConfiguration(scratchURL, scratchPath);
    }
}
