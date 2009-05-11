package org.objectweb.proactive.core.body.tags;

import org.objectweb.proactive.core.body.tags.propagation.policy.AbstractPolicy;

public interface RequestTagsFactory {

    /**
     * Return a new RequestTags object
     * @return RequestTags Object
     */
    public RequestTags newRequestTags();
    
    /**
     * Register a new Tag with the default propagation policy (Propagate)
     * @param id - Tag Identifier
     */
    public void register(String id);
    
    /**
     * Register a new Tag with the specified propagation policy
     * @param id - Tag Identifier
     * @param policy - Propagation Policy for this tag
     */
    public void register(String id, AbstractPolicy policy);
}
