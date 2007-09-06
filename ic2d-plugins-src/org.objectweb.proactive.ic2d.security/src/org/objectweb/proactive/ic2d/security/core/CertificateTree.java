package org.objectweb.proactive.ic2d.security.core;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javassist.NotFoundException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.objectweb.proactive.core.security.SecurityConstants;
import org.objectweb.proactive.core.security.TypedCertificate;

public class CertificateTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7966227118791659019L;

	private static KeyPairGenerator keygen;

	private List<CertificateTree> children;

	private CertificateTree parent;

	private TypedCertificate certificate;

	private CertificateTree() {
		children = new ArrayList<CertificateTree>();
		certificate = null;
		parent = null;

		if (keygen == null) {
			if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
				Security.addProvider(new BouncyCastleProvider());
			}
			try {
				keygen = KeyPairGenerator.getInstance("RSA",
						BouncyCastleProvider.PROVIDER_NAME);
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (NoSuchProviderException e1) {
				e1.printStackTrace();
			}
		}
	}

	protected CertificateTree(TypedCertificate certificate) {
		this();
		this.certificate = certificate;
	}

	public CertificateTree(String name, int keySize, int validity, int type) {
		this();

		keygen.initialize(keySize);

		KeyPair kp = keygen.genKeyPair();

		try {
			X509Certificate cert = CertTools.genSelfCert(name, validity, null,
					kp.getPrivate(), kp.getPublic(), true);
			certificate = new TypedCertificate(cert, type, kp.getPrivate());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public List<CertificateTree> getChildren() {
		return children;
	}

	public List<TypedCertificate> getCertChain() {
		List<TypedCertificate> chain;
		if (parent != null) {
			chain = parent.getCertChain();
		} else {
			chain = new ArrayList<TypedCertificate>();
		}
		chain.add(0, certificate);
		return chain;
	}

	private void setParent(CertificateTree parent) {
		this.parent = parent;
	}

	public TypedCertificate getCertificate() {
		return certificate;
	}

	private CertificateTree getChild(TypedCertificate cert) {
		for (CertificateTree child : children) {
			if (child.getCertificate().equals(cert)) {
				return child;
			}
		}
		return null;
	}

	public void add(CertificateTree newChild) {
		if (newChild == null) {
			return;
		}

		CertificateTree existingChild = getChild(newChild.getCertificate());
		if (existingChild == null) {
			children.add(newChild);
			newChild.setParent(this);
		} else {
			existingChild.merge(newChild);
		}
	}

	public boolean merge(CertificateTree tree) {
		if (tree == null
				|| !tree.getCertificate().equals(this.getCertificate())) {
			return false;
		}

		if (certificate.getPrivateKey() == null
				&& tree.getCertificate().getPrivateKey() != null) {
			certificate = tree.getCertificate();
		}
		for (CertificateTree newChild : tree.getChildren()) {
			add(newChild);
		}
		return true;
	}

	public void add(String name, int keySize, int validity, int type) {
		keygen.initialize(keySize);
		KeyPair childKP = keygen.genKeyPair();

		X509Certificate parentCert = certificate.getCert();
		PublicKey parentPublicKey = parentCert.getPublicKey();
		PrivateKey parentPrivateKey = certificate.getPrivateKey();
		String parentName = parentCert.getSubjectX500Principal().getName();

		try {
			X509Certificate cert = CertTools.genCert(name, validity, null,
					childKP.getPublic(), true, parentName, parentPrivateKey,
					parentPublicKey);
			CertificateTree newChild = new CertificateTree(
					new TypedCertificate(cert, type, childKP.getPrivate()));
			newChild.setParent(this);
			add(newChild);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public TypedCertificate search(String name, int type)
			throws NotFoundException {
		if (type == certificate.getType()
				&& certificate.getCert().getSubjectX500Principal().getName()
						.equals(name)) {
			return certificate;
		}

		for (CertificateTree child : children) {
			try {
				return child.search(name, type);
			} catch (NotFoundException nfe) {
				// let's check the other children
			}
		}

		throw new NotFoundException("Certificate " + name + " : " + type
				+ " not found.");
	}

	public boolean remove() {
		if (parent == null) {
			return false;
		}

		return parent.removeChild(this);
	}

	public boolean removeChild(CertificateTree child) {
		return children.remove(child);
	}

	public String getName() {
		String result = SecurityConstants.typeToString(certificate.getType());
		result += ":";
		result += certificate.getCert().getSubjectX500Principal().getName();
		return result;
	}

	public CertificateTree getRoot() {
		if (parent == null) {
			return this;
		}
		return parent.getRoot();
	}

	public static CertificateTree newTree(
			List<TypedCertificate> certificateChain) {
		CertificateTree parentNode = null;
		CertificateTree childNode = null;
		for (TypedCertificate certificate : certificateChain) {
			parentNode = new CertificateTree(certificate);
			parentNode.add(childNode);

			childNode = parentNode;
		}
		CertificateTree thisNode = childNode;
		while (!thisNode.getChildren().isEmpty()) {
			thisNode = thisNode.getChildren().get(0);
		}

		return thisNode;
	}
}
