/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
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
