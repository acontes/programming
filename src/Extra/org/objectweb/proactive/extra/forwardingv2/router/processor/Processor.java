package org.objectweb.proactive.extra.forwardingv2.router.processor;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;


/** Asynchronous handler for a given {@link MessageType}
 * 
 * @since ProActive 4.1.0
 */
public abstract class Processor {

    final static protected Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    abstract public void process();
}
