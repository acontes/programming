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
package org.objectweb.proactive.core.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.JDKKeyPairGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.objectweb.proactive.core.security.SecurityConstants.EntityType;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class DefaultProActiveSecurityManager extends ProActiveSecurityManager
    implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6885646803779608858L;
	private transient Logger logger;

    
    public DefaultProActiveSecurityManager(EntityType type)
        throws Exception {
        super(type, new DefaultPolicyServer());
        
        this.logger = ProActiveLogger.getLogger(Loggers.SECURITY);

        Provider myProvider = new BouncyCastleProvider();
        Security.addProvider(myProvider);

        /* generation of a default certificate */
        KeyPair keyPair = null;
        SecureRandom rand = new SecureRandom();

        JDKKeyPairGenerator.RSA keyPairGen = new JDKKeyPairGenerator.RSA();

        keyPairGen.initialize(1024, rand);

        keyPair = keyPairGen.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        X509V3CertificateGenerator certifGenerator = new X509V3CertificateGenerator();

        DateFormat convert = DateFormat.getDateInstance();

        certifGenerator.setPublicKey(publicKey);
        String subjectCN = "CN=Generic Certificate" + new Random().nextLong() +
            ", OU=Generic Certificate, EmailAddress=none";

        //  System.out.println("DefaultCertificate subjectCN " + subjectCN);
        X509Name subject = new X509Name(subjectCN);
        X509Name issuer = new X509Name(
                "CN=Generic Certificate, OU=Generic Certificate, EmailAddress=none");

        certifGenerator.setSubjectDN(subject);
        certifGenerator.setIssuerDN(issuer);
        certifGenerator.setSignatureAlgorithm("MD5withRSA");

        //    GregorianCalendar start = new GregorianCalendar(2002, Calendar.JUNE, 13);
        //  GregorianCalendar end = new GregorianCalendar(2004, Calendar.JUNE, 31);
        Date start = new Date(System.currentTimeMillis() - 50000);
        Date stop = new Date(System.currentTimeMillis() + 50000);

        certifGenerator.setNotAfter(stop);
        certifGenerator.setNotBefore(start);
          certifGenerator.setPublicKey(publicKey);
        certifGenerator.setSerialNumber(new BigInteger("1"));

        X509Certificate certificate = certifGenerator.generate(privateKey, "BC");
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        //System.out.println("reconstruit un DPSM");
        this.logger = ProActiveLogger.getLogger(Loggers.SECURITY);
    }
}
