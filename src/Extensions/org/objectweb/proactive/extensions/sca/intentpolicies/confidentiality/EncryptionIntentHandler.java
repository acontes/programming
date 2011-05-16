package org.objectweb.proactive.extensions.sca.intentpolicies.confidentiality;

import java.nio.ByteBuffer;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


/**
 * This class defines the encrytion intent handler which is used to encrypt data 
 * transferation . The secret key is in the first 8 byte of data.
 * @author mug
 *
 */
public class EncryptionIntentHandler extends IntentHandler {
    public Object invoke(IntentJoinPoint ijp) throws Exception {
        byte[] RawData = (byte[]) ijp.getArgs()[0];
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        SecretKey key = kg.generateKey();
        Cipher c1 = Cipher.getInstance("DES/ECB/PKCS5Padding");
        c1.init(Cipher.ENCRYPT_MODE, key);
        byte[] keyBytes = key.getEncoded();
        byte[] encryptedData = c1.doFinal(RawData);
        ByteBuffer dataBuffer = ByteBuffer.allocate(keyBytes.length + encryptedData.length);
        dataBuffer.put(keyBytes);
        dataBuffer.put(encryptedData);
        ijp.setArgs(new Object[] { (Object) dataBuffer.array() });
        System.err.println("RAW DATA : " + new String(RawData));
        System.err.println("ENCRYPTED DATA : " + new String(dataBuffer.array()));
        Object ret = ijp.proceed();
        return ret;
    }
}
