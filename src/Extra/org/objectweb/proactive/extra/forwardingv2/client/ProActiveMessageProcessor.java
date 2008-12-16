package org.objectweb.proactive.extra.forwardingv2.client;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.message.MessageRoutingMessage;


/**
 * Execute the request received and send the response.
 *
 * @since ProActive 4.1.0
 */
public class ProActiveMessageProcessor implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    private final Message _toProcess;
    private final AgentV2 agent;

    public ProActiveMessageProcessor(Message msg, AgentV2 agent) {
        this._toProcess = msg;
        this.agent = agent;
    }

    public void run() {
        ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

            // Handle the message
            MessageRoutingMessage message = (MessageRoutingMessage) HttpMarshaller
                    .unmarshallObject(_toProcess.getData());
            Object result = message.processMessage();
            byte[] resultBytes = HttpMarshaller.marshallObject(result);

            agent.sendReply(_toProcess, resultBytes);
        } catch (Exception e) {
            logger.warn("HTTP Failed to serve a message", e);
        } finally {
            Thread.currentThread().setContextClassLoader(savedClassLoader);
        }
    }

}
