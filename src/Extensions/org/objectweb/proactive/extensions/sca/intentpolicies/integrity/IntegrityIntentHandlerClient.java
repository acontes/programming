package org.objectweb.proactive.extensions.sca.intentpolicies.integrity;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


/**
 * This class defines the integrity intent handler on the client side which is used to check data 
 * integration during transfer. it use a checksum algorithm.
 * @author mug
 *
 */
public class IntegrityIntentHandlerClient extends IntentHandler {
    public Object invoke(IntentJoinPoint ijp) throws Exception {
        byte[] rawData = (byte[]) ijp.getArgs()[0];
        Checksum checksumEngine = new Adler32();
        checksumEngine.update(rawData, 0, rawData.length);
        long checksum = checksumEngine.getValue();
        checksumEngine.reset();
        ByteBuffer dataBuffer = ByteBuffer.allocate(rawData.length + 8);
        dataBuffer.putLong(checksum);
        dataBuffer.put(rawData);

        ijp.setArgs(new Object[] { (Object) dataBuffer.array() });

        System.err.println("checksum client= " + checksum + " raw data length: " + rawData.length);
        for (byte b : dataBuffer.array()) {
            System.err.format("0x%x ", b);
        }
        System.err.println();
        System.err.println("raw data without checksum : " + new String(rawData));
        Object ret = ijp.proceed();
        return ret;
    }
}
