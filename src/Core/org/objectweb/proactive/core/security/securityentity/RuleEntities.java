package org.objectweb.proactive.core.security.securityentity;

import java.util.ArrayList;

import org.objectweb.proactive.core.security.securityentity.RuleEntity.Match;


public class RuleEntities extends ArrayList<RuleEntity> {

    /**
     *
     */
    private static final long serialVersionUID = -5629440174390543367L;

    public RuleEntities() {
        super();
    }

    public RuleEntities(RuleEntities entities) {
        super(entities);
    }

    public Match match(Entities entities) {
        if (isEmpty()) {
            return Match.DEFAULT;
        }

        for (RuleEntity entity : this) {
            if (entity.match(entities) == Match.FAILED) {
                return Match.FAILED;
            }
        }
        return Match.OK;
    }

    /**
     * level represents the specificity of the target entities of a rule, higher
     * level is more specific
     *
     * @return the maximum level among the RuleEnties
     */
    public int getLevel() {
        int maxLevel = RuleEntity.UNDEFINED_LEVEL;
        for (RuleEntity rule : this) {
            if (maxLevel < rule.getLevel()) {
                maxLevel = rule.getLevel();
            }
        }
        return maxLevel;
    }

    public boolean contains(Entity entity) {
        for (RuleEntity rule : this) {
            if (rule.match(entity) == Match.OK) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String result = new String();
        for (RuleEntity rule : this) {
            result += rule.toString();
            result += "\n";
        }
        return result;
    }
}
