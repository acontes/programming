package org.objectweb.proactive.examples.components.sca.securityintent;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


public class DecryptionIntentHandler extends IntentHandler {
    public Object invoke(IntentJoinPoint ijp) throws Throwable {
        byte[] rawData = (byte[]) ijp.getArgs()[0];
        //KeyGenerator kg = KeyGenerator.getInstance("DES");
        byte[] keyBytes = new byte[8];
        byte[] encrytedData = new byte[rawData.length - 8];
        System.arraycopy(rawData, 0, keyBytes, 0, keyBytes.length);
        System.arraycopy(rawData, 8, encrytedData, 0, encrytedData.length);
        SecretKey key = new SecretKeySpec(keyBytes, "DES");
        Cipher c1 = Cipher.getInstance("DES/ECB/PKCS5Padding");
        c1.init(Cipher.DECRYPT_MODE, key);
        //byte[] algo = key.getAlgorithm().getBytes();
        byte[] result = c1.doFinal(encrytedData);
        ijp.setArgs(new Object[] { (Object) result });
        System.err.println("ENCRYPTED DATA : " + new String(rawData));
        System.err.println("DECRYPTED DATA : " + new String(result));
        Object ret = ijp.proceed();
        return ret;
    }
}
