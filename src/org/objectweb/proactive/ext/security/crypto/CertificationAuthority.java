/*
* ################################################################
*
* ProActive: The Java(TM) library for Parallel, Distributed,
*            Concurrent computing with Security and Mobility
*
* Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
* Contact: proactive-support@inria.fr
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
package org.objectweb.proactive.ext.security.crypto;

import org.bouncycastle.jce.provider.JDKKeyPairGenerator;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;


public class CertificationAuthority {
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    public CertificationAuthority() {
        generateKeyPair();
    }

    public static PublicKey get_PublicKey() {
        return publicKey;
    }

    public static PrivateKey get_PrivateKey() {
        return privateKey;
    }

    public static void writeKeys() {
        try {
            System.out.println("Generating AC publicKey...");

            FileOutputStream fout = new FileOutputStream("acPublicKey");
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(publicKey);
            out.flush();
            out.close();
            System.out.println("Generating AC privateKey...");
            fout = new FileOutputStream("acPrivateKey");
            out = new ObjectOutputStream(fout);
            out.writeObject(privateKey);
            out.flush();
            out.close();
            System.out.println("The KeyPair has been correctly generated.");
            System.out.println("The AC publicKey  is saved in : acPublicKey");
            System.out.println("The AC privateKey is saved in : acPrivateKey");
        } catch (Exception e) {
            System.out.println("Exception in AC key serialization :" + e);
        }
    }

    public static void main(String[] args) {
        new CertificationAuthority();
        writeKeys();
    }

    private static void generateKeyPair() {
        //   Provider myProvider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
        // Tester ici si ca n'a pas ete deja fait : cf mail...
        //  Security.addProvider(myProvider);
        // Key Pair Generation...
        SecureRandom rand = new SecureRandom();
        JDKKeyPairGenerator.RSA keyPairGen = new JDKKeyPairGenerator.RSA();
        keyPairGen.initialize(512, rand);

        KeyPair keyPair = keyPairGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }
}
