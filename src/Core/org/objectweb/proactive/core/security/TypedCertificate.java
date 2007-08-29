package org.objectweb.proactive.core.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class TypedCertificate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3389269734930919276L;
	private X509Certificate cert;
	private int type;
	private PrivateKey privateKey;
	private byte[] encodedCert;
	
	public TypedCertificate(X509Certificate cert, int type, PrivateKey privateKey) {
		this.cert = cert;
		this.type = type;
		this.privateKey = privateKey;
		this.encodedCert = null;
	}

	public X509Certificate getCert() {
		return cert;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		String typeString = new String();
		if (SecurityConstants.typeToString(getType()) != null) {
			typeString = SecurityConstants.typeToString(getType()) + ":";
		}
		return typeString + getCert().getSubjectX500Principal().getName();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		if (getCert() != null) {
            try {
                encodedCert = cert.getEncoded();
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        }

        cert = null;
        out.defaultWriteObject();
        cert = ProActiveSecurity.decodeCertificate(encodedCert);
        encodedCert = null;
	}
	

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (encodedCert != null) {
            cert = ProActiveSecurity.decodeCertificate(encodedCert);
        }

        encodedCert = null;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof TypedCertificate)) {
    		return false;
    	}
		TypedCertificate otherCert = (TypedCertificate) obj;
			
		if (otherCert.getType() != this.getType()) {
			return false;
		}
		if (!otherCert.getCert().equals(this.getCert())) {
			return false;
		}
		
    	return true;
    }

}
