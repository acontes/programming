package org.objectweb.proactive.extensions.sca.intentpolicies.integrity;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


/**
 * This class defines the integrity intent handler on the server side which is used to check data 
 * integration during transfer. it use a checksum algorithm.
 * @author mug
 *
 */
public class IntegrityIntentHandlerServer extends IntentHandler {
    public Object invoke(IntentJoinPoint ijp) throws Exception {
        byte[] rawData = (byte[]) ijp.getArgs()[0];
        byte[] result = new byte[rawData.length - 8];

        ByteBuffer dataBuffer = ByteBuffer.wrap(rawData);

        for (byte b : dataBuffer.array()) {
            System.err.format("0x%x ", b);
        }
        System.err.println();

        long checksum = dataBuffer.getLong();
        dataBuffer.get(result);

        Checksum checksumEngine = new Adler32();
        checksumEngine.update(result, 0, result.length);
        System.err.println("checksum from client = " + checksum + " checksum from server" +
            checksumEngine.getValue() + " raw data length: " + rawData.length);
        if (checksum == checksumEngine.getValue()) {
            checksumEngine.reset();
            ijp.setArgs(new Object[] { (Object) result });
            Object ret = ijp.proceed();
            return ret;
        } else
            throw new Exception("checksum error");
    }
}