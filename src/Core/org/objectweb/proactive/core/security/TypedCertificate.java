package org.objectweb.proactive.core.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.objectweb.proactive.core.security.SecurityConstants.EntityType;

public class TypedCertificate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3389269734930919276L;
	private transient X509Certificate cert;
	private EntityType type;
	private PrivateKey privateKey;
	private byte[] encodedCert;
	
	public TypedCertificate(X509Certificate cert, EntityType type, PrivateKey privateKey) {
		this.cert = cert;
		this.type = type;
		this.privateKey = privateKey;
		this.encodedCert = null;
	}

	public X509Certificate getCert() {
		return this.cert;
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public EntityType getType() {
		return this.type;
	}

	public void setType(EntityType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return getType() + ":" + getCert().toString();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		if (getCert() != null) {
            try {
            	this.encodedCert = this.cert.getEncoded();
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        }

        out.defaultWriteObject();
        this.encodedCert = null;
	}
	

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.encodedCert != null) {
        	this.cert = ProActiveSecurity.decodeCertificate(this.encodedCert);
        }

        this.encodedCert = null;
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
    
    @Override
    public int hashCode() {
    	return this.cert.hashCode();
    }

}
