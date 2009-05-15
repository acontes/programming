package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.vm;

import java.io.Serializable;

import org.objectweb.proactive.extensions.gcmdeployment.core.TopologyImpl;


public class VMBean implements Serializable {
    String id;
    String name;
    TopologyImpl node;
    boolean clone;

    VMBean(String id, boolean c, String name) {
        this.id = id;
        this.clone = c;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TopologyImpl getNode() {
        return node;
    }

    public void setNode(TopologyImpl node) {
        this.node = node;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isClone() {
        return this.clone;
    }
}