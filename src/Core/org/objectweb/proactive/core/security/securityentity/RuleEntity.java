package org.objectweb.proactive.core.security.securityentity;

import java.io.Serializable;


public abstract class RuleEntity implements Serializable {
    public static final int MATCH_OK = 1;
    public static final int MATCH_DEFAULT = 0;
    public static final int MATCH_FAILED = -1;
    
    public static final int UNDEFINED_LEVEL = 0;
    
    /**
     * Level of the entity, equals the depth of its certificate in the certificate tree (UNDEFINED_LEVEL is the root, above the self signed certificates)
     */
    protected int level;
    protected int type;

    protected RuleEntity(int type) {
        this.type = type;
        this.level = UNDEFINED_LEVEL;
    }

    protected int getLevel() {
    	return this.level;
    }
    
    public int getType() {
    	return this.type;
    }

    protected int match(Entities e) {
        for (Entity entity : e) {
            if (match(entity) == RuleEntity.MATCH_FAILED) {
                return RuleEntity.MATCH_FAILED;
            }
        }
        return RuleEntity.MATCH_OK;
    }

    abstract protected int match(Entity e);
    
    abstract public String getName();
    
    @Override
	public String toString() {
    	return "RuleEnty :\n\tLevel : " + this.level;
    }
}
