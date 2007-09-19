package org.objectweb.proactive.core.security.securityentity;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.objectweb.proactive.core.security.KeyStoreTools;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.SecurityConstants.EntityType;

public class CertificatedRuleEntity extends RuleEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6616484696945476576L;

	protected TypedCertificate certificate;

	public CertificatedRuleEntity(EntityType type, KeyStore keystore, String name)
			throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		super(type);
		this.certificate = KeyStoreTools.getCertificate(keystore, type, name);
		this.level = KeyStoreTools.getLevel(keystore, this.certificate) + levelIncrement();
	}

	@Override
	protected Match match(Entity e) {
		for (TypedCertificate cert : e.getCertificateChain()) {
			if (this.certificate.equals(cert)) {
				return Match.OK;
			}
		}
		return Match.FAILED;
	}

	@Override
	public String toString() {
		return super.toString() + "\n\tCertificate : " + this.certificate.toString();
	}

	@Override
	public String getName() {
		return this.certificate.toString();
	}
}
