package org.objectweb.proactive.core.body.tags.tag;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.tags.Tag;

public class DsiTag extends Tag {

    public DsiTag() {
        super("DSI", new UniqueID());
    }

    public Tag apply() {
        // Propagate itself
        return this;
    }

}
