package org.objectweb.proactive.extensions.sca.intentpolicies.authentification;

import java.security.PublicKey;


/**
 * The interface that a server component need to implement to enable authentication feature 
 * @author mug
 *
 */
public interface AuthentificationItf {

    public static final String CLIENT_ITF_NAME = "auth-client";
    public static final String SERVER_ITF_NAME = "auth-server";

    /**
     * return the public key of the client component, as a service
     * @return
     */
    public PublicKey sendPublicKey();

    /**
     * return the public key of the server component
     * @return
     */
    public PublicKey getPublicKeyFromServer();

}
