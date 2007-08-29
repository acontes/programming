package org.objectweb.proactive.core.security;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.cert.X509Certificate;

public abstract class KeyStoreTools {

	private static final String KEYSTORE_ENTITY_KEY_PATH = "entityCertificate";

	private static final String KEYSTORE_NODE_PATH = "nodeEntry_";

	private static final String KEYSTORE_RUNTIME_PATH = "runtimeEntry_";

	private static final String KEYSTORE_APPLICATION_KEY_PATH = "applicationKey";

	private static final String KEYSTORE_APPLICATION_PATH = "applicationCertificate_";

	private static final String KEYSTORE_USER_PATH = "userCertificate_";

	private static final String KEYSTORE_DOMAIN_PATH = "domainCertificate_";

	private static final String PRIVATE_KEY_PASSWORD = "weapologisefortheinconvenience";

	public static TypedCertificate getSelfCertificate(KeyStore keystore,
			int type) throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {
		TypedCertificate cert = getCertificate(keystore, KEYSTORE_ENTITY_KEY_PATH);
		cert.setType(type);
		return cert;
	}

	public static TypedCertificateList getSelfCertificateChain(
			KeyStore keystore, int type) throws KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException {
		return getCertificateChain(keystore, getSelfCertificate(keystore, type));
	}

	public static PrivateKey getSelfPrivateKey(KeyStore keystore)
			throws UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException {
		return getPrivateKey(keystore, KEYSTORE_ENTITY_KEY_PATH);
	}

	public static int getSelfLevel(KeyStore keystore) throws KeyStoreException {
		return keystore.getCertificateChain(KEYSTORE_ENTITY_KEY_PATH).length;
	}

	public static TypedCertificate getApplicationCertificate(KeyStore keystore)
			throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {
		return getCertificate(keystore, KEYSTORE_APPLICATION_KEY_PATH);
	}

	public static TypedCertificateList getApplicationCertificateChain(
			KeyStore keystore) throws KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException {
		return getCertificateChain(keystore,
				getApplicationCertificate(keystore));
	}

	public static PrivateKey getApplicationPrivateKey(KeyStore keystore)
			throws UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException {
		return getPrivateKey(keystore, KEYSTORE_APPLICATION_KEY_PATH);
	}

	public static int getApplicationLevel(KeyStore keystore)
			throws KeyStoreException {
		return keystore.getCertificateChain(KEYSTORE_APPLICATION_KEY_PATH).length;
	}

	public static TypedCertificate getCertificate(KeyStore keystore,
			String alias) throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {
		PrivateKey pk = null;
		if (keystore.isKeyEntry(alias)) {
			pk = getPrivateKey(keystore, alias);
		}
		return new TypedCertificate((X509Certificate) keystore
				.getCertificate(alias), pathToType(alias), pk);
	}

	public static TypedCertificate getCertificate(KeyStore keystore, int type,
			String name) throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {

		if (getApplicationCertificate(keystore).getCert() != null
				&& getApplicationCertificate(keystore).getCert()
						.getSubjectX500Principal().getName().equals(name)) {
			return getApplicationCertificate(keystore);
		}

		return getCertificate(keystore, typeToPath(type) + name);
	}

	private static PrivateKey getPrivateKey(KeyStore keystore, String alias)
			throws UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException {
		return (PrivateKey) keystore.getKey(alias, PRIVATE_KEY_PASSWORD
				.toCharArray());
	}

	private static TypedCertificate getParentCertificate(KeyStore keystore,
			TypedCertificate certificate) throws KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException {
		return getCertificate(keystore, SecurityConstants
				.getParentType(certificate.getType()), certificate.getCert()
				.getIssuerX500Principal().getName());
	}

	public static TypedCertificateList getCertificateChain(KeyStore keystore,
			TypedCertificate certificate) throws KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException {
		String issuer = certificate.getCert().getIssuerX500Principal()
				.getName();
		String subject = certificate.getCert().getSubjectX500Principal()
				.getName();
		if (subject.equals(issuer)) {
			TypedCertificateList list = new TypedCertificateList();
			list.add(certificate);
			return list;
		}

		TypedCertificateList list = getCertificateChain(keystore,
				getParentCertificate(keystore, certificate));
		list.add(0, certificate);
		return list;
	}

	public static int getLevel(KeyStore keystore, TypedCertificate certificate)
			throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {
		String issuer = certificate.getCert().getIssuerX500Principal()
				.getName();
		String subject = certificate.getCert().getSubjectX500Principal()
				.getName();
		if (subject.equals(issuer)) {
			return 1;
		}
		return getLevel(keystore, getParentCertificate(keystore, certificate)) + 1;
	}

	public static void newCertificate(KeyStore keystore,
			TypedCertificate certificate) throws KeyStoreException {
		TrustedCertificateEntry certificateEntry = new TrustedCertificateEntry(
				certificate.getCert());
		String alias = typeToPath(certificate.getType())
				+ certificate.getCert().getSubjectX500Principal().getName();
		if (keystore.containsAlias(alias)) {
			keystore.deleteEntry(alias);
		}

		keystore.setEntry(alias, certificateEntry, null);
	}

	public static void newPrivateKey(KeyStore keystore, TypedCertificate certificate)
			throws UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException {
		String path = typeToPath(certificate.getType())
				+ certificate.getCert().getSubjectX500Principal().getName();
		PrivateKeyEntry keyEntry = new PrivateKeyEntry(certificate
				.getPrivateKey(), getCertificateChain(keystore, certificate)
				.certsToArray());
		if (!keystore.containsAlias(path)) {
			ProtectionParameter pp = new PasswordProtection(
					PRIVATE_KEY_PASSWORD.toCharArray());
			keystore.setEntry(path, keyEntry, pp);
		}
	}

	public static void newEntity(KeyStore keystore, TypedCertificate certificate)
			throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {
		PrivateKeyEntry keyEntry = new PrivateKeyEntry(certificate
				.getPrivateKey(), getCertificateChain(keystore, certificate)
				.certsToArray());
		if (keystore.containsAlias(KEYSTORE_ENTITY_KEY_PATH)) {
			keystore.deleteEntry(KEYSTORE_ENTITY_KEY_PATH);
		}

		ProtectionParameter pp = new PasswordProtection(PRIVATE_KEY_PASSWORD
				.toCharArray());
		keystore.setEntry(KEYSTORE_ENTITY_KEY_PATH, keyEntry, pp);
	}

	public static void newApplicationPrivateKey(KeyStore keystore,
			TypedCertificate certificate) throws KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException {
		PrivateKeyEntry keyEntry = new PrivateKeyEntry(certificate
				.getPrivateKey(), getCertificateChain(keystore, certificate)
				.certsToArray());
		if (keystore.containsAlias(KEYSTORE_ENTITY_KEY_PATH)) {
			keystore.deleteEntry(KEYSTORE_ENTITY_KEY_PATH);
		}

		ProtectionParameter pp = new PasswordProtection(PRIVATE_KEY_PASSWORD
				.toCharArray());
		keystore.setEntry(KEYSTORE_APPLICATION_KEY_PATH, keyEntry, pp);
	}

	public static String typeToPath(int type) {
		switch (type) {
		case SecurityConstants.ENTITY_TYPE_NODE:
			return KEYSTORE_NODE_PATH;
		case SecurityConstants.ENTITY_TYPE_RUNTIME:
			return KEYSTORE_RUNTIME_PATH;
		case SecurityConstants.ENTITY_TYPE_APPLICATION:
			return KEYSTORE_APPLICATION_PATH;
		case SecurityConstants.ENTITY_TYPE_USER:
			return KEYSTORE_USER_PATH;
		case SecurityConstants.ENTITY_TYPE_DOMAIN:
			return KEYSTORE_DOMAIN_PATH;
		default:
			System.out.println("Unknown type");
			return null;
		}
	}

	public static int pathToType(String path) {
		if (path.contains(KEYSTORE_NODE_PATH)) {
			return SecurityConstants.ENTITY_TYPE_NODE;
		}
		if (path.contains(KEYSTORE_RUNTIME_PATH)) {
			return SecurityConstants.ENTITY_TYPE_RUNTIME;
		}
		if (path.contains(KEYSTORE_APPLICATION_PATH)
				|| path.equals(KEYSTORE_APPLICATION_KEY_PATH)) {
			return SecurityConstants.ENTITY_TYPE_APPLICATION;
		}
		if (path.contains(KEYSTORE_USER_PATH)) {
			return SecurityConstants.ENTITY_TYPE_USER;
		}
		if (path.contains(KEYSTORE_DOMAIN_PATH)) {
			return SecurityConstants.ENTITY_TYPE_DOMAIN;
		}
		return SecurityConstants.ENTITY_TYPE_UNKNOWN;
	}
}
