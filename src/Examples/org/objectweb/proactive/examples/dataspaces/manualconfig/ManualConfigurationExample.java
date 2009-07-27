package org.objectweb.proactive.examples.dataspaces.manualconfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.extensions.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extensions.dataspaces.api.PADataSpaces;
import org.objectweb.proactive.extensions.dataspaces.core.DataSpacesNodes;
import org.objectweb.proactive.extensions.dataspaces.core.naming.NamingService;
import org.objectweb.proactive.extensions.dataspaces.core.naming.NamingServiceDeployer;
import org.objectweb.proactive.extensions.dataspaces.exceptions.ApplicationAlreadyRegisteredException;
import org.objectweb.proactive.extensions.dataspaces.exceptions.WrongApplicationIdException;


/**
 * Simple example of manual configuration of DataSpaces for 1 node (without using GCM deployment).
 */
public class ManualConfigurationExample {
    public static void main(String[] args) throws ApplicationAlreadyRegisteredException,
            WrongApplicationIdException, ProActiveException, URISyntaxException, IOException {
        // @snippet-start DataSpacesManualConfig_StartingNS
        // start Naming Service
        final NamingServiceDeployer namingServiceDeployer = new NamingServiceDeployer();
        final String namingServiceURL = namingServiceDeployer.getNamingServiceURL();
        // @snippet-end DataSpacesManualConfig_StartingNS

        // @snippet-start DataSpacesManualConfig_RegisteringApp
        // need to guarantee uniqueness of application id somehow
        final long applicationId = 1234431;
        // access (possibly remote) Naming Service
        final NamingService namingService = NamingService.createNamingServiceStub(namingServiceURL);
        // register application, here: without predefined inputs and outputs 
        namingService.registerApplication(applicationId, null);
        // @snippet-end DataSpacesManualConfig_RegisteringApp

        // configure node for DataSpaces
        final Node node = NodeFactory.getHalfBodiesNode();

        // @snippet-start DataSpacesManualConfig_ConfigureNode
        // configure node, here: node is configured without scratch
        DataSpacesNodes.configureNode(node, null);
        // @snippet-end DataSpacesManualConfig_ConfigureNode

        // @snippet-start DataSpacesManualConfig_ConfigureNodeForApp
        // configure node for application
        DataSpacesNodes.configureApplication(node, applicationId, namingServiceURL);
        // @snippet-end DataSpacesManualConfig_ConfigureNodeForApp

        // now we can use PADataSpaces from AO/bodies on that node.
        // in case of half-bodies node, we can use PADataSpaces from non-AO
        PADataSpaces.addDefaultInput("http://www.faqs.org/ftp/rfc/rfc2616.txt", null);

        final DataSpacesFileObject fo = PADataSpaces.resolveDefaultInput();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(fo.getContent().getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } finally {
            fo.close();
            if (reader != null) {
                reader.close();
            }
        }

        // after using DS, we can clean up
        // (actually, this part should be also implemented as finally, as we should always close DS on node and NamingService)

        // @snippet-start DataSpacesManualConfig_CloseNodeConfig
        DataSpacesNodes.closeNodeConfig(node);
        // @snippet-end DataSpacesManualConfig_CloseNodeConfig

        // @snippet-start DataSpacesManualConfig_UnregisteringApp
        namingService.unregisterApplication(applicationId);
        // @snippet-end DataSpacesManualConfig_UnregisteringApp

        // @snippet-start DataSpacesManualConfig_StoppingNS
        namingServiceDeployer.terminate();
        // @snippet-end DataSpacesManualConfig_StoppingNS

        PALifeCycle.exitSuccess();
    }
}
