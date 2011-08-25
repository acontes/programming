/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.objectweb.proactive.extensions.sca.intentpolicies.authentification;

import java.security.PrivateKey;
import java.security.PublicKey;
import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


/**
 *
 * @author mug
 */
public class AuthentificationIntentHandler extends IntentHandler {

    public PublicKey selfPk;
    public PrivateKey selfPrk;
    public PublicKey pkFromServer;

    //public PrivateKey prk;

    public AuthentificationIntentHandler() {
        java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());
        java.security.KeyPairGenerator kpg = null;
        try {
            kpg = java.security.KeyPairGenerator.getInstance("RSA");
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        kpg.initialize(1024);
        java.security.KeyPair kp = kpg.genKeyPair();
        selfPk = kp.getPublic();
        selfPrk = kp.getPrivate();
    }

    public void setServerPublicKey(PublicKey pk) {
        pkFromServer = pk;
    }

    public PublicKey getPublicKey() {
        return selfPk;
    }

    @Override
    public Object invoke(IntentJoinPoint ijp) throws Throwable {
        return ijp.proceed();
    }
}
