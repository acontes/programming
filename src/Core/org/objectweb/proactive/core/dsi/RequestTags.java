package org.objectweb.proactive.core.dsi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.dsi.propagation.PropagationPolicy;

/**
 * RequestTags : set a map of tag on a request,
 * with a propagation policy of the tag's identifier
 * value
 */
public class RequestTags implements Serializable {

    /**  Map of all tags/policy-data associated with a request */
    protected Map<String, Tag> tags;

    /**
     * Constructor
     */
    public RequestTags() {
        this.tags = new HashMap<String, Tag>();
    }

    /**
     * Set a tag for the request with a propagation policy
     * and a user data content.
     * @param id     - Name of the tag
     * @param value  - Value of this tag
     * @param policy - Propagation Policy
     * @param data   - User data attached to the tag
     */
    public void setTag(String id, UniqueID value, PropagationPolicy policy, Object data) {
        this.tags.put(id, new Tag(value, policy, data));
    }

    /**
     * Set a tag for the request with default propagation policy
     * (propagate the current tag value) and a user data content.
     * @param id     - Name of the tag
     * @param value  - Value of this tag
     * @param data   - User data attached to the tag
     */
    public void setTag(String id, UniqueID value, Object data){
        this.tags.put(id, new Tag(value, data));
    }

    /**
     * Set a tag for the request with default propagation policy
     * (propagate the current tag value).
     * @param id     - Name of the tag
     * @param value  - Value of this tag
     */
    public void setTag(String id, UniqueID value){
        this.tags.put(id, new Tag(value));
    }

    /**
     * Return the user data content attached to this tag.
     * @param id - Tag name
     * @return User data content
     */
    public Object getData(String id) {
        return tags.get(id).getData();
    }

    /**
     * Return all Tags Name setted.
     * @return Set of tag name setted
     */
    public Set<String> getAllTagsID() {
        return tags.keySet();
    }

    /**
     * Propagate all tags setted
     */
    public void propagateTags() {
        for (Tag t : tags.values()) {
            t.propagate();
        }
    }

    /**
     * Display informations of all tags
     */
    public String toString() {
        String res = "";
        for (Entry<String, Tag> e : tags.entrySet()) {
            res += e.getKey() + "" + e.getValue() + "\n";
        }
        return res;
    }
}
