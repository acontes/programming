package org.objectweb.proactive.core.security.securityentity;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.objectweb.proactive.core.security.KeyStoreTools;
import org.objectweb.proactive.core.security.TypedCertificate;

public class CertificatedRuleEntity extends RuleEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6616484696945476576L;

	protected TypedCertificate certificate;

	public CertificatedRuleEntity(int type, KeyStore keystore, String name)
			throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		super(type);
		certificate = KeyStoreTools.getCertificate(keystore, type, name);
		level = KeyStoreTools.getLevel(keystore, certificate);
	}

	@Override
	protected int match(Entity e) {
		for (TypedCertificate cert : e.getCertificateChain()) {
			if (this.certificate.equals(cert)) {
				return RuleEntity.MATCH_OK;
			}
		}
		return RuleEntity.MATCH_FAILED;
	}

	@Override
	public String toString() {
		return super.toString() + "\n\tCertificate : " + certificate.toString();
	}

	@Override
	public String getName() {
		return certificate.toString();
	}
}
