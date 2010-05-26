package org.objectweb.proactive.core.body.tags.tag;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.tags.Tag;


/**
 * CMTag allows to follow a Component Request
 * 
 */
public class CMTag extends Tag {

    public static final String IDENTIFIER = "PA_TAG_CM";
    
    public static final int OLD_SEQ_ID = 0; 
    public static final int NEW_SEQ_ID = 1;
    public static final int ROOT_ID = 6;
    
    /**
     * Constructor setting the Tag name "PA_TAG_CM"
     * and an UniqueID as the tag DATA
     */
    public CMTag(UniqueID id, long oldSeqID, long newSeqID, String sourceName, String destName, String interfaceName, String methodName, long rootID) {
    	super(IDENTIFIER, "" + oldSeqID + "::" + newSeqID + "::" + sourceName + "::" + destName + "::" + interfaceName + "::" + methodName + "::" + rootID);
    }
    
    /**
     * Constructor to create the CMTag from the notification String
     * @param data
     */
    public CMTag(String data) {
    	super(IDENTIFIER, data);
    }

    /**
     * This tag return itself at each propagation.
     */
    public Tag apply() {
        // propagates itself
        return this;
    }
    
    public long getOldSeqID() {
    	String stringTag = (String) data;
    	String[] elements = stringTag.split("::");
    	return Long.parseLong(elements[OLD_SEQ_ID]);
    }
    
    public long getNewSeqID() {
    	String stringTag = (String) data;
    	String[] elements = stringTag.split("::");
    	return Long.parseLong(elements[NEW_SEQ_ID]);
    }
    
    public long getRootID() {
    	String stringTag = (String) data;
    	String[] elements = stringTag.split("::");
    	return Long.parseLong(elements[ROOT_ID]);
    }
    
}
