/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.security.securityentity;

import java.io.Serializable;
import java.security.cert.X509Certificate;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.TypedCertificateList;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * @author acontes
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Entity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5134177585663270596L;
	static Logger logger = ProActiveLogger.getLogger(Loggers.SECURITY);
    protected TypedCertificateList certChain;
    
    public Entity() {
    	//
	}

    public Entity(TypedCertificateList certChain) {
        this.certChain = certChain;
    }

    public int getType() {
        return certChain.get(0).getType();
    }

    public String getName() {
        return certChain.get(0).getCert().getSubjectX500Principal()
		.getName();
    }

    public TypedCertificateList getCertificateChain() {
        return certChain;
    }

    public TypedCertificate getCertificate() {
        if (certChain == null) {
            return null;
        }
        return certChain.get(0);
    }

    @Override
	public String toString() {
        X509Certificate certificate = getCertificate().getCert();
        String string = new String();
        string = "\nType : " + getCertificate().getType();
        string += "\nCertificate : ";

        if (certificate != null) {
            string += certificate.toString();
        } else {
            string += "*";
        }
        return string;
    }
}
