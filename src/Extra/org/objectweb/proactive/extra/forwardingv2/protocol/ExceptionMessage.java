package org.objectweb.proactive.extra.forwardingv2.protocol;

public class ExceptionMessage extends ForwardedMessage {

    //TODO: modifier le constructeur pour qu'il prenne une exception en parametre au lieu d'un byte array de data, puis qu'il la serialize et ensuite appelle super() avec le byte array construit en paramètre, risque de ne pas marcher car super doit etre appelé avant => utiliser set data après avoir construit le message avec null pour la data dans super puis avoir sérializé l'exception

    public ExceptionMessage(MessageType type, AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
        super(type, srcAgentID, dstAgentID, msgID, data);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public ExceptionMessage(byte[] byteArray, int offset) {
        super(byteArray, offset);
    }

}
