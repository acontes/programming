package org.objectweb.proactive.core.body.tags;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Map of tag attached on a message. At each sendrequest, the "apply" method
 * of each tag is called, and the tags resulting are attached to the request which
 * will be sent.
 */
public class MessageTags implements Serializable {

    /**  Map of all tags/policy-data associated with a request */
    protected Map<String, Tag> messagestags;

    /**
     * Constructor
     */
    public MessageTags() {
        this.messagestags = new HashMap<String, Tag>();
    }

    /**
     * Add a tag on the request with an different propagation policy 
     * than the one in the TagRegistry, and a user data content.
     * @param id     - Identifier of the tag in the TagRegistry
     * @param policy - Propagation Policy
     * @param data   - User data attached to the tag
     * @return the new tag
     */
    public Tag addTag(Tag tag) {
        this.messagestags.put(tag.getId(), tag);
        return messagestags.get(tag.getId());
    }
    
    /**
     * Remove the tag with this identifier from this request.
     * @param id Tag identifier
     * @return the Tag removed
     */
    public Tag removeTag(String id){
        return this.messagestags.remove(id);
    }
    
    /**
     * Return all the Tags 
     * @return Collection of Tags
     */
    public Collection<Tag> getTags(){
        return this.messagestags.values();
    }
    
    /**
     * Return the Tag with the specified Id
     * @param id - Tag identifier
     * @return The Tag
     */
    public Tag getTag(String id){
        return messagestags.get(id);
    }
    
    /**
     * Return the user data content attached to this tag.
     * @param id - Identifier of the tag
     * @return User data content
     */
    public Object getData(String id) {
        return messagestags.get(id).getData();
    }

    /**
     * Return all Tags Name setted.
     * @return Set of tag name setted
     */
    public Set<String> getAllTagsID() {
        return messagestags.keySet();
    }

    /**
     * Return true if the tag exist, false otherwise.
     * @param id - Tag identifier
     * @return true if the tag exist, false otherwise
     */
    public boolean check(String id){
        return messagestags.get(id) != null;
    }
    
    /**
     * Display informations of all tags
     */
    public String toString() {
        String res = "";
        for (Entry<String, Tag> e : messagestags.entrySet()) {
            res += e.getKey() + "" + e.getValue() + "\n";
        }
        return res;
    }

}