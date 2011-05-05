package org.objectweb.proactive.examples.components.sca.securityintent;

import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


public class EncryptionIntentHandler extends IntentHandler {
    public Object invoke(IntentJoinPoint ijp) throws Throwable {
        byte[] RawData = (byte[]) ijp.getArgs()[0];
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        SecretKey key = kg.generateKey();
        Cipher c1 = Cipher.getInstance("DES/ECB/PKCS5Padding");
        c1.init(Cipher.ENCRYPT_MODE, key);
        byte[] keyBytes = key.getEncoded();
        //byte[] algo = key.getAlgorithm().getBytes();
        byte[] encryptedData = c1.doFinal(RawData);
        byte[] result = new byte[keyBytes.length + encryptedData.length];
        System.arraycopy(keyBytes, 0, result, 0, keyBytes.length);
        System.arraycopy(encryptedData, 0, result, keyBytes.length, encryptedData.length);
        ijp.setArgs(new Object[] { (Object) result });
        System.err.println("RAW DATA : " + new String(RawData));
        System.err.println("ENCRYPTED DATA : " + new String(result));
        Object ret = ijp.proceed();
        return ret;
    }
}
