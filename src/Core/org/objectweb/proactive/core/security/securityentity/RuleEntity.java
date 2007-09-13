package org.objectweb.proactive.core.security.securityentity;

import java.io.Serializable;

import org.objectweb.proactive.core.security.SecurityConstants.EntityType;


public abstract class RuleEntity implements Serializable {
	public enum Match {
		OK,
		DEFAULT,
		FAILED;
	}
    
    public static final int UNDEFINED_LEVEL = 0;
    
    /**
     * Level of the entity, equals the depth of its certificate in the certificate tree (UNDEFINED_LEVEL is the root, above the self signed certificates)
     */
    protected int level;
    protected EntityType type;

    protected RuleEntity(EntityType type) {
        this.type = type;
        this.level = UNDEFINED_LEVEL;
    }

    protected int getLevel() {
    	return this.level;
    }
    
    public EntityType getType() {
    	return this.type;
    }

    protected Match match(Entities e) {
        for (Entity entity : e) {
            if (match(entity) == Match.FAILED) {
                return Match.FAILED;
            }
        }
        return Match.OK;
    }

    abstract protected Match match(Entity e);
    
    abstract public String getName();
    
    @Override
	public String toString() {
    	return "RuleEntity :\n\tLevel : " + this.level;
    }
}
