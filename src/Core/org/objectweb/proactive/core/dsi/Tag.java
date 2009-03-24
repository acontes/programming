package org.objectweb.proactive.core.dsi;

import java.io.Serializable;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.dsi.propagation.PropagationPolicy;

public class Tag implements Serializable{

    protected UniqueID value;
    protected PropagationPolicy policy;
    protected Object data;

    public Tag(UniqueID value, PropagationPolicy policy, Object data) {
        this.value = value;
        this.policy = policy;
        this.data = data;
        policy.setTag(this);
    }

    public void setPolicy(PropagationPolicy policy) {
        this.policy = policy;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void propagate(){
        this.policy.propagate();
    }

    public String toString() {
        return "<TAG: value="+value+", policy="+policy+", data="+data+">";
    }

    public UniqueID getValue() {
        return value;
    }

    public void setValue(UniqueID value) {
        this.value = value;
    }

}
