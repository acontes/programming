package org.objectweb.proactive.extensions.sca.intentpolicies.confidentiality;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;

import edu.emory.mathcs.backport.java.util.Arrays;


/**
 * This class defines the decryption intent handler which is used to decrypt data 
 * transferation. The secret key is in the first 8 byte of data.
 * @author mug
 *
 */
public class DecryptionIntentHandler extends IntentHandler {
    public Object invoke(IntentJoinPoint ijp) throws Exception {
        byte[] rawData = (byte[]) ijp.getArgs()[0];
        ByteBuffer databuffer = ByteBuffer.wrap(rawData);
        byte[] keyBytes = new byte[8];
        byte[] encrytedData = new byte[rawData.length - 8];
        databuffer.get(keyBytes);
        databuffer.get(encrytedData);
        SecretKey key = new SecretKeySpec(keyBytes, "DES");
        Cipher c1 = Cipher.getInstance("DES/ECB/PKCS5Padding");
        c1.init(Cipher.DECRYPT_MODE, key);
        byte[] result = c1.doFinal(encrytedData);
        ijp.setArgs(new Object[] { (Object) result });
        System.err.println("ENCRYPTED DATA : " + new String(rawData));
        System.err.println("DECRYPTED DATA : " + new String(result));
        Object ret = ijp.proceed();
        return ret;
    }
}
