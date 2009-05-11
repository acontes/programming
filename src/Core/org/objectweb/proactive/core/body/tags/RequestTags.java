package org.objectweb.proactive.core.body.tags;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.tags.propagation.PropagationPolicy;

/**
 * RequestTags : Add a tag on a request with a tag value and its propagation policy, and an object container binded to it.
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
     * Add a tag on the request with an different propagation policy 
     * than the one in the TagRegistry, and a user data content.
     * @param id     - Identifier of the tag in the TagRegistry
     * @param policy - Propagation Policy
     * @param data   - User data attached to the tag
     */
    public void addTag(String id,PropagationPolicy policy, Object data) {
        this.tags.put(id, new Tag(policy, data));
    }

    /**
     * Add a tag on the request and a user data content.
     * @param id     - Identifier of the tag in the TagRegistry
     * @param data   - User data attached to the tag
     * @throws UnknowTagException 
     */
    public void addTag(String id, Object data) throws UnknowTagException{
        Tag t = new Tag(TagRegistry.getInstance().getPolicy(id),data);
        this.tags.put(id, t);
    }

    /**
     * Add a tag on the request.
     * @param id - Identifier of the tag in the TagRegistry
     * @throws UnknowTagException 
     */
    public void addTag(String id) throws UnknowTagException{
        Tag t = new Tag(TagRegistry.getInstance().getPolicy(id));
        this.tags.put(id, t);
    }
    
    /**
     * Remove the tag with this identifier from this request.
     * @param id Tag identifier
     * @return the Tag removed
     */
    public Tag removeTag(String id){
        return this.tags.remove(id);
    }
    
    /**
     * Return the user data content attached to this tag.
     * @param id - Identifier of the tag
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
     * To get a specifig tag value
     * @param id - Tag identifier
     * @return value of this tag
     */
    public UniqueID getTagValue(String id){
        return tags.get(id).getValue();
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
