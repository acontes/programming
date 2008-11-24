/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.executable;

import java.util.Collections;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.objectweb.proactive.extensions.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.executable.ApplicationExecutableBean.Instances;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ApplicationParserExecutable {
    private static final String XPATH_PATH = "app:path";
    private static final String XPATH_NODE_PROVIDER = "app:nodeProvider";
    private static final String XPATH_COMMAND = "app:command";
    private static final String XPATH_ARG = "app:arg";

    final ApplicationExecutableBean configBean;
    final Map<String, NodeProvider> nodeProviders;
    
    public ApplicationParserExecutable(final ApplicationExecutableBean configBean, final Map<String, NodeProvider> nodeProviders) {
        this.configBean = configBean;
        this.nodeProviders = Collections.unmodifiableMap(nodeProviders);
    }
    
    
    public void parseExecutableNode(Node appNode, XPath xpath) throws Exception {

        String instancesValue = GCMParserHelper.getAttributeValue(appNode, "instances");
        if (instancesValue != null) {
            configBean.setInstances(Instances.valueOf(instancesValue));
        }

        NodeList nodeProviderNodes;
        nodeProviderNodes = (NodeList) xpath.evaluate(XPATH_NODE_PROVIDER, appNode, XPathConstants.NODESET);
  
        // resource providers
        if (nodeProviderNodes.getLength() != 0) {
            for (int i = 0; i < nodeProviderNodes.getLength(); ++i) {
                Node npNode = nodeProviderNodes.item(i);
                String refid = GCMParserHelper.getAttributeValue(npNode, "refid");
                NodeProvider nodeProvider = nodeProviders.get(refid);
                configBean.addProvider(nodeProvider);
            }
        } else {
            for (NodeProvider provider : nodeProviders.values()) {
                configBean.addProvider(provider);
            }
        }

        Node commandNode = (Node) xpath.evaluate(XPATH_COMMAND, appNode, XPathConstants.NODE);
        String name = GCMParserHelper.getAttributeValue(commandNode, "name");
        configBean.setCommand(name);

        Node pathNode = (Node) xpath.evaluate(XPATH_PATH, commandNode, XPathConstants.NODE);
        if (pathNode != null) {
            // path tag is optional
            configBean.setPath(GCMParserHelper.parsePathElementNode(pathNode));
        }

        // command args
        NodeList argNodes = (NodeList) xpath.evaluate(XPATH_ARG, commandNode, XPathConstants.NODESET);
        for (int i = 0; i < argNodes.getLength(); ++i) {
            Node argNode = argNodes.item(i);
            String argVal = argNode.getFirstChild().getNodeValue();
            configBean.addArg(argVal);
        }
    }
}
