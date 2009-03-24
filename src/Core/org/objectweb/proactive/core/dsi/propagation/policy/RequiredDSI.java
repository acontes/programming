package org.objectweb.proactive.core.dsi.propagation.policy;

import org.objectweb.proactive.core.UniqueID;


public class RequiredDSI extends PolicyDSI {

    @Override
    public void propagate() {
        if(this.tag.getValue() == null){
            this.tag.setValue(new UniqueID());
        }
    }

}
