package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group;

import java.io.Serializable;

public abstract class AbstractJavaGroup implements JavaGroup, Serializable   {

    private String id;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
