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
        // start Naming Service
        final NamingServiceDeployer namingServiceDeployer = new NamingServiceDeployer();
        final String namingServiceURL = namingServiceDeployer.getNamingServiceURL();
        final NamingService remoteNamingService = namingServiceDeployer.getRemoteNamingService();

        // use some unique application id
        final long applicationId = 1234431;
        // register application, without predefined inputs and outputs 
        remoteNamingService.registerApplication(applicationId, null);

        // configure node for DS
        final Node halfBodiesNode = NodeFactory.getHalfBodiesNode();

        // node is configured without scratch
        DataSpacesNodes.configureNode(halfBodiesNode, null);
        DataSpacesNodes.configureApplication(halfBodiesNode, applicationId, namingServiceURL);

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
        DataSpacesNodes.closeNodeConfig(halfBodiesNode);
        namingServiceDeployer.terminate();
        // (actually, this part should be also implemented as finally, as we should always close DS on node and NamingService)

        PALifeCycle.exitSuccess();
    }
}
