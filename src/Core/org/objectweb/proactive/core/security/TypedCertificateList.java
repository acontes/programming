package org.objectweb.proactive.core.security;

import java.security.cert.Certificate;
import java.util.ArrayList;


public class TypedCertificateList extends ArrayList<TypedCertificate> {

    /**
     *
     */
    private static final long serialVersionUID = 4754182179785159674L;

    public Certificate[] certsToArray() {
        Certificate[] array = new Certificate[this.size()];
        for (int i = 0; i < this.size(); i++) {
            array[i] = this.get(i).getCert();
        }
        return array;
    }
}
