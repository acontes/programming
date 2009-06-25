package org.objectweb.proactive.core.body.tags.tag;

import java.rmi.server.UID;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.tags.Tag;


/**
 * CMTag allows to follow a Component Request
 * 
 */
public class CMTag extends Tag {

    public static final String IDENTIFIER = "PA_TAG_CM";
    
    /**
     * Constructor setting the Tag name "PA_TAG_CM"
     * and an UniqueID as the tag DATA
     */
    public CMTag(UniqueID id, long cpt, String name) {
        //super(IDENTIFIER, new UniqueID().getCanonString());
        //super(IDENTIFIER, "" + id.getCanonString() + "::" + cpt);
    	super(IDENTIFIER, "" + name + "::" + cpt);
    }

    /**
     * This tag return itself at each propagation.
     */
    public Tag apply() {
        // propagates itself
        return this;
    }
    
}
