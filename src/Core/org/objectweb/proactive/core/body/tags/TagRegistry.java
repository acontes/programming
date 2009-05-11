package org.objectweb.proactive.core.body.tags;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.proactive.core.body.tags.propagation.PropagationPolicy;
import org.objectweb.proactive.core.body.tags.propagation.policy.PropagatePolicy;


/**
 * Bind a Tag Identifier with its policy
 *
 */
public class TagRegistry {

    static private TagRegistry _singleton;
    
    private Map<String, PropagationPolicy> types;
    
    /**
     * Constructor
     */
    private TagRegistry() {
        this.types = new ConcurrentHashMap<String, PropagationPolicy>();
    }
    
    /**
     * Return the instance of the Tag Registry
     * @return - instance of TagRegistry
     */
    static public TagRegistry getInstance(){
        if(_singleton == null){
            _singleton = new TagRegistry();
        }
        return _singleton;
    }
    
    /**
     * Register a tag type with its policy.
     * Override a previously registering if the tag Id is already registered.
     * @param id     - Tag Type Id
     * @param policy - Propagation policy for this tag
     */
    public void register(String id, PropagationPolicy policy){
        this.types.put(id, policy);
    }
    
    /**
     * Register a tag type with the default policy (propagation of the current tag value).
     * @param id  - Tag Type Id
     */
    public void register(String id){
        this.register(id,new PropagatePolicy());
    }
    
    /**
     * Return the propagation policy used with the tag 'id'
     * @param id - Tag Identifier
     * @return propagation policy for this tag
     * @throws UnknowTagException 
     */
    public PropagationPolicy getPolicy(String id) throws UnknowTagException{
        PropagationPolicy policy = this.types.get(id); 
        if (policy != null) {
            return policy;
        } else {
            throw new UnknowTagException("Trying to get a policy for an unregistered Tag");
        }
    }
}
