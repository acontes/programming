package org.objectweb.proactive.core.dsi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.dsi.propagation.PropagationPolicy;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class RequestTags implements Serializable {
    public static Logger dsilogger = ProActiveLogger.getLogger(Loggers.DSI);

    /**  Map of all tags/policy-data associated with a request */
    protected Map<String, Tag> tags;

    public RequestTags() {
        this.tags = new HashMap<String, Tag>();
        dsilogger.debug("Tags created");
    }

    public void setTag(String id, UniqueID value, PropagationPolicy policy, Object data) {
        this.tags.put(id, new Tag(value, policy, data));
    }

    public Object getData(String id) {
        return tags.get(id).getData();
    }

    public Set<String> getAllTagsID() {
        return tags.keySet();
    }

    public void propagateTags() {
        dsilogger.debug("RequestTags.propagateTags() \n" + this);
        for (Tag t : tags.values()) {
            t.propagate();
        }
    }

    public String toString() {
        String res = "";
        for (Entry<String, Tag> e : tags.entrySet()) {
            res += e.getKey() + "" + e.getValue() + "\n";
        }
        return res;
    }
}
