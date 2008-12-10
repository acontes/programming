package org.objectweb.proactive.extra.forwardingv2.protocol;

/**
 * An object representing a message 
 *
 * A message MUST have the following structure
 * 
 * MSG = LENGHT PROTO-ID  MSG_TYPE SRC_AGENT-ID SRC_ENDPOINT-ID DST_AGENT-ID DST_ENDPOINT-ID ID DATA
 * LENGTH = long
 * PROTO-ID = int
 * MSG_TYPE = int
 * SRC_AGENT-ID = long
 * SRC_ENDPOINT-ID = long
 * DST_AGENT-ID = long
 * DST_ENDOPINT-ID = long
 * ID = long
 * DATA = byte[]
 * 
 */
public class Message {
}
