/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.sca.intentpolicies.confidentiality;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


/**
 * This class defines the encrytion intent handler which is used to encrypt data 
 * transfer. The secret key is in the first 8 byte of data.
 *
 * @author The ProActive Team
 */
public class EncryptionIntentHandler extends IntentHandler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS);

    private Key publicKey;

    public EncryptionIntentHandler(Key key) {
        //can't pass the instance of key directly
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key.getEncoded());
            PublicKey _publicKey = keyFactory.generatePublic(publicKeySpec);
            this.publicKey = _publicKey;
        } catch (Exception e) {
            //should never happen
            e.printStackTrace();
        }
    }

    public Object invoke(IntentJoinPoint ijp) throws Throwable {
        byte[] rawData = (byte[]) ijp.getArgs()[0];
        byte[] result = encrypt(rawData, publicKey);
        ijp.setArgs(new Object[] { result });

        if (logger.isDebugEnabled()) {
            logger.debug("Raw data: " + new String(rawData));
            logger.debug("Encrypted data: " + new String(result));
        }
        Object ret = ijp.proceed();
        return ret;
    }

    private static byte[] encrypt(byte[] inpBytes, Key key) {
        try {
            // get an instance of RSA Cipher
            Cipher cipher = Cipher.getInstance("RSA");
            // init the Cipher in ENCRYPT_MODE and aPK
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // use doFinal on the byte[] and return the ciphered byte[]
            return cipher.doFinal(inpBytes);
        } catch (Exception e) {
            System.out.println("Encryption error");
            e.printStackTrace();
            return null;
        }

    }

}
