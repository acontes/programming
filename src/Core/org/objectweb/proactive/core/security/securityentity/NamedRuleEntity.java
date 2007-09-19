package org.objectweb.proactive.core.security.securityentity;

import java.security.KeyStore;
import java.security.KeyStoreException;

import org.objectweb.proactive.core.security.KeyStoreTools;
import org.objectweb.proactive.core.security.SecurityConstants.EntityType;

public class NamedRuleEntity extends RuleEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2724922813544854253L;

	private String name;

	public NamedRuleEntity(EntityType type, KeyStore keystore, String name)
			throws KeyStoreException {
		super(type);
    	this.name = name;
		this.level = KeyStoreTools.getApplicationLevel(keystore) + levelIncrement();
	}

	@Override
	protected Match match(Entities e) {
		for (Entity entity : e) {
			if (match(entity) == Match.OK) {
				return Match.OK;
			}
		}
		return Match.FAILED;
	}

	@Override
	protected Match match(Entity e) {
		if (e.getType() == this.getType() && e.getName().equals(this.name)) {
			return Match.OK;
		}

		return Match.FAILED;
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
