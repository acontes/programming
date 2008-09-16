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

package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMParserHelper;
import org.w3c.dom.Node;

public class GroupEC2Parser extends AbstractJavaGroupParser {

    private static final String NODE_NAME = "ec2Group";

    private static final String XPATH_IMAGENAME = "dep:imageName";
    private static final String XPATH_ACCESSKEYID = "dep:accessKeyId";
    private static final String XPATH_SECRETACCESSKEY = "dep:secretAccessKey";

    @Override
    public AbstractJavaGroup createGroup() {
        return new GroupEC2();
    }

    @Override
    public String getNodeName() {
        return NODE_NAME;
    }

    @Override
    public AbstractJavaGroup parseGroupNode(Node groupNode, XPath xpath) {
        
        GroupEC2 ec2Group = (GroupEC2) super.parseGroupNode(groupNode, xpath);
        
        try {

            Node imageName = (Node) xpath.evaluate(XPATH_IMAGENAME, groupNode, XPathConstants.NODE);
            Node accessKeyId = (Node) xpath.evaluate(XPATH_ACCESSKEYID, groupNode, XPathConstants.NODE);
            Node secretAccessKey = (Node) xpath.evaluate(XPATH_SECRETACCESSKEY, groupNode, XPathConstants.NODE);

            ec2Group.setImageId(GCMParserHelper.getElementValue(imageName));
            ec2Group.setAccessKeyId(GCMParserHelper.getElementValue(accessKeyId));
            ec2Group.setSecretAccessKey(GCMParserHelper.getElementValue(secretAccessKey));
            
            String hostCapacity = GCMParserHelper.getAttributeValue(groupNode, "dep:hostCapacity");
            String vmCapacity = GCMParserHelper.getAttributeValue(groupNode, "dep:vmCapacity");
            
            ec2Group.setHostCapacity(hostCapacity);
            ec2Group.setVmCapacity(vmCapacity);

        } catch (XPathExpressionException e) {
            GCMDeploymentLoggers.GCMD_LOGGER.error(e.getMessage(), e);
        }

        
        return ec2Group;
    }
    
}
