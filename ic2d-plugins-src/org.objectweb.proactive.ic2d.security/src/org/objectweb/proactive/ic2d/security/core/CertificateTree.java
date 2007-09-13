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
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.SecurityConstants.EntityType;

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
		this.children = new ArrayList<CertificateTree>();
		this.certificate = null;
		this.parent = null;

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

	public CertificateTree(String name, int keySize, int validity, EntityType type) {
		this();

		keygen.initialize(keySize);

		KeyPair kp = keygen.genKeyPair();

		try {
			X509Certificate cert = CertTools.genSelfCert(name, validity, null,
					kp.getPrivate(), kp.getPublic(), true);
			this.certificate = new TypedCertificate(cert, type, kp.getPrivate());
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
		return this.children;
	}

	public List<TypedCertificate> getCertChain() {
		List<TypedCertificate> chain;
		if (this.parent != null) {
			chain = this.parent.getCertChain();
		} else {
			chain = new ArrayList<TypedCertificate>();
		}
		chain.add(0, this.certificate);
		return chain;
	}

	private void setParent(CertificateTree parent) {
		this.parent = parent;
	}

	public TypedCertificate getCertificate() {
		return this.certificate;
	}

	private CertificateTree getChild(TypedCertificate cert) {
		for (CertificateTree child : this.children) {
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
			this.children.add(newChild);
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

		if (this.certificate.getPrivateKey() == null
				&& tree.getCertificate().getPrivateKey() != null) {
			this.certificate = tree.getCertificate();
		}
		for (CertificateTree newChild : tree.getChildren()) {
			add(newChild);
		}
		return true;
	}

	public void add(String name, int keySize, int validity, EntityType type) {
		keygen.initialize(keySize);
		KeyPair childKP = keygen.genKeyPair();

		X509Certificate parentCert = this.certificate.getCert();
		PublicKey parentPublicKey = parentCert.getPublicKey();
		PrivateKey parentPrivateKey = this.certificate.getPrivateKey();
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

	public TypedCertificate search(String name, EntityType type)
			throws NotFoundException {
		if (type == this.certificate.getType()
				&& this.certificate.getCert().getSubjectX500Principal().getName()
						.equals(name)) {
			return this.certificate;
		}

		for (CertificateTree child : this.children) {
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
		if (this.parent == null) {
			return false;
		}

		return this.parent.removeChild(this);
	}

	public boolean removeChild(CertificateTree child) {
		return this.children.remove(child);
	}

	public String getName() {
		String result = this.certificate.getType().toString();
		result += ":";
		result += this.certificate.getCert().getSubjectX500Principal().getName();
		return result;
	}

	public CertificateTree getRoot() {
		if (this.parent == null) {
			return this;
		}
		return this.parent.getRoot();
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
