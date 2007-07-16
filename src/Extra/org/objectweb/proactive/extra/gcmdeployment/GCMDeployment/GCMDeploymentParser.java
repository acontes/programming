package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import org.objectweb.proactive.extra.gcmdeployment.GCMParserConstants;


public interface GCMDeploymentParser extends GCMParserConstants {
    public GCMDeploymentEnvironment getEnvironment();

    public GCMDeploymentInfrastructure getInfrastructure();
}
