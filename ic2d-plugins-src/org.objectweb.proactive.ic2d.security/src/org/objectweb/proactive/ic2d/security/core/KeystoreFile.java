package org.objectweb.proactive.ic2d.security.core;

public class KeystoreFile extends CertificateTreeList {

    /**
     *
     */
    private static final long serialVersionUID = 4612887605387014845L;
    private String name;

    public KeystoreFile(String name, CertificateTreeList trees) {
        super(trees);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
