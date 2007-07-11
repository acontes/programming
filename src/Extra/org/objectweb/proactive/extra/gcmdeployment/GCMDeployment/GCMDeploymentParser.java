package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.util.Map;

import org.objectweb.proactive.extra.gcmdeployment.Environment;
import org.objectweb.proactive.extra.gcmdeployment.GCMParserConstants;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;

public interface GCMDeploymentParser extends GCMParserConstants {

    public Environment getEnvironment();
    
    public Map<String, HostInfo> getHosts();
    
    public Map<String, Group> getGroups();
}
