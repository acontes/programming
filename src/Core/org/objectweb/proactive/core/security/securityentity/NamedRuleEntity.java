package org.objectweb.proactive.core.security.securityentity;

import java.security.KeyStore;
import java.security.KeyStoreException;

import org.objectweb.proactive.core.security.KeyStoreTools;

public class NamedRuleEntity extends RuleEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2724922813544854253L;

	private String name;

	public NamedRuleEntity(int type, KeyStore keystore, String name)
			throws KeyStoreException {
		super(type);
		this.name = name;
		this.level = KeyStoreTools.getApplicationLevel(keystore) + 1;
	}

	@Override
	protected int match(Entities e) {
		for (Entity entity : e) {
			if (match(entity) == RuleEntity.MATCH_OK) {
				return RuleEntity.MATCH_OK;
			}
		}
		return RuleEntity.MATCH_FAILED;
	}

	@Override
	protected int match(Entity e) {
		if (e.getType() == this.getType() && e.getName().equals(this.name)) {
			return RuleEntity.MATCH_OK;
		}

		return RuleEntity.MATCH_FAILED;
	}

	@Override
	public String toString() {
		return super.toString() + "\n\tName : " + this.name;
	}

	@Override
	public String getName() {
		return this.name;
	}
}
