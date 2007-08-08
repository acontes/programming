package org.objectweb.proactive.extra.gcmdeployment.GCMApplication.ApplicationParsers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.objectweb.proactive.extra.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extra.gcmdeployment.PathElement;
import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.GCMApplicationParser;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractApplicationParser implements ApplicationParser {

    protected CommandBuilder commandBuilder;
    protected XPath xpath;
    
    public AbstractApplicationParser() {
        commandBuilder = createCommandBuilder();
    }
    
    public CommandBuilder getCommandBuilder() {
        return commandBuilder;
    }

    public void parseApplicationNode(Node paNode, GCMApplicationParser applicationParser, XPath xpath) {
        this.xpath = xpath;
    }
    
    protected abstract CommandBuilder createCommandBuilder();

    protected List<PathElement> parseClasspath(Node classPathNode)
            throws XPathExpressionException {
                NodeList pathElementNodes = (NodeList) xpath.evaluate("pa:pathElement",
                        classPathNode, XPathConstants.NODESET);
            
                ArrayList<PathElement> res = new ArrayList<PathElement>();
            
                for (int i = 0; i < pathElementNodes.getLength(); ++i) {
                    Node pathElementNode = pathElementNodes.item(i);
                    PathElement pathElement = parsePathElementNode(pathElementNode);
                    res.add(pathElement);
                }
            
                return res;
            }

    protected PathElement parsePathElementNode(Node pathElementNode) {
        PathElement pathElement = new PathElement();
        String attr = GCMParserHelper.getAttributeValue(pathElementNode,
                "relpath");
        pathElement.setRelPath(attr);
        attr = GCMParserHelper.getAttributeValue(pathElementNode, "base");
        if (attr != null) {
            pathElement.setBase(attr);
        }
    
        return pathElement;
    } 
    
}
