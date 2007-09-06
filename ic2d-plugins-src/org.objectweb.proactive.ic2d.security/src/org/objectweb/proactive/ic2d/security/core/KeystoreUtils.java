package org.objectweb.proactive.ic2d.security.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.objectweb.proactive.core.security.KeyStoreTools;
import org.objectweb.proactive.core.security.SecurityConstants;

public abstract class KeystoreUtils {

	public static CertificateTreeList loadKeystore(String path, String password)
			throws KeyStoreException, NoSuchProviderException,
			NoSuchAlgorithmException, CertificateException, IOException,
			UnrecoverableKeyException {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		KeyStore store = KeyStore.getInstance("PKCS12",
				BouncyCastleProvider.PROVIDER_NAME);
		store.load(new FileInputStream(new File(path)), password.toCharArray());

		return listKeystore(store);
	}

	public static CertificateTreeList listKeystore(KeyStore store)
			throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {
		CertificateTreeList list = new CertificateTreeList();
		Enumeration<String> aliases = store.aliases();
		while (aliases.hasMoreElements()) {
			list.add(CertificateTree.newTree(
					KeyStoreTools.getCertificateChain(store, KeyStoreTools
							.getCertificate(store, aliases.nextElement())))
					.getRoot());
		}
		return list;
	}

	public static void saveKeystore(String path, String password,
			CertificateTreeList ctl,
			Map<CertificateTree, Boolean> keepPrivateKeyMap)
			throws KeyStoreException, NoSuchProviderException,
			NoSuchAlgorithmException, CertificateException, IOException,
			UnrecoverableKeyException {
		KeyStore store = createKeystore(ctl, keepPrivateKeyMap);

		store.store(new FileOutputStream(new File(path)), password
				.toCharArray());
	}

	public static KeyStore createKeystore(CertificateTreeList ctl,
			Map<CertificateTree, Boolean> keepPrivateKeyMap)
			throws KeyStoreException, NoSuchProviderException,
			NoSuchAlgorithmException, CertificateException, IOException,
			UnrecoverableKeyException {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		KeyStore store = KeyStore.getInstance("PKCS12",
				BouncyCastleProvider.PROVIDER_NAME);
		store.load(null, null);

		int appNumber = 0;
		for (CertificateTree tree : keepPrivateKeyMap.keySet()) {
			if (tree.getCertificate().getType() == SecurityConstants.ENTITY_TYPE_APPLICATION) {
				appNumber++;
			}
		}

		for (CertificateTree tree : ctl) {
			addTreeToKeystore(store, tree, keepPrivateKeyMap, appNumber == 1);
		}

		return store;
	}

	private static void addTreeToKeystore(KeyStore store, CertificateTree tree,
			Map<CertificateTree, Boolean> keepPrivateKeyMap, boolean oneApp)
			throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {
		if (keepPrivateKeyMap.containsKey(tree)) {
			if (oneApp
					&& tree.getCertificate().getType() == SecurityConstants.ENTITY_TYPE_APPLICATION) {
				KeyStoreTools.newApplicationPrivateKey(store, tree
						.getCertificate());
			} else {
				KeyStoreTools.newPrivateKey(store, tree.getCertificate());
			}
		} else {
			KeyStoreTools.newCertificate(store, tree.getCertificate());
		}
		for (CertificateTree subTree : tree.getChildren()) {
			addTreeToKeystore(store, subTree, keepPrivateKeyMap, oneApp);
		}
	}
}
