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
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


/**
 * This class defines the decryption intent handler which is used to decrypt data 
 * transfer. The secret key is in the first 8 byte of data.
 *
 * @author The ProActive Team
 */
public class DecryptionIntentHandler extends IntentHandler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS);

    private Key privateKey;

    public DecryptionIntentHandler(Key privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            PrivateKey _privateKey = keyFactory.generatePrivate(privateKeySpec);
            this.privateKey = _privateKey;
        } catch (Exception e) {
            //should never happen
            e.printStackTrace();
        }
    }

    public Object invoke(IntentJoinPoint ijp) throws Throwable {
        byte[] rawData = (byte[]) ijp.getArgs()[0];
        byte[] result = decrypt(rawData, privateKey);
        ijp.setArgs(new Object[] { result });

        if (logger.isDebugEnabled()) {
            logger.debug("Encrypted data: " + new String(rawData));
            logger.debug("Decrypted data: " + new String(result));
        }

        Object ret = ijp.proceed();
        return ret;
    }

    private static byte[] decrypt(byte[] inpBytes, Key key) throws Exception {
        try {
            // get an instance of RSA Cipher
            Cipher cipher = Cipher.getInstance("RSA");
            // init the Cipher in DECRYPT_MODE and aPK
            cipher.init(Cipher.DECRYPT_MODE, key);
            // use doFinal on the byte[] and return the deciphered byte[]
            return cipher.doFinal(inpBytes);
        } catch (Exception e) {
            System.out.println("decryption error");
            e.printStackTrace();
            return null;
        }

    }

}
