package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import org.objectweb.proactive.extra.gcmdeployment.Environment;
import org.objectweb.proactive.extra.gcmdeployment.GCMParserConstants;


public interface GCMDeploymentParser extends GCMParserConstants {
    public Environment getEnvironment();

    public GCMDeploymentInfrastructure getInfrastructure();
}
