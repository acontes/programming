package org.objectweb.proactive.extra.forwardingv2.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataRequestMessage;


/**
 * Execute the request received and send the response.
 *
 * @since ProActive 4.1.0
 */
public class ProActiveMessageHandler implements MessageHandler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT);

    final private ExecutorService tpe;

    private AgentV2Internal agent;

    public ProActiveMessageHandler(AgentV2Internal agent) {
        this.agent = agent;

        int workers = PAProperties.PA_NET_ROUTER_CLIENT_HANDLER_THREADS.getValueAsInt();
        logger.debug("ProActiveMessageHandler threadpool has " + workers + " workers");
        tpe = Executors.newFixedThreadPool(workers);

    }

    public void pushMessage(DataRequestMessage message) {
        if (logger.isTraceEnabled()) {
            logger.trace("pushing message " + message + " into the executor queue");
        }
        ProActiveMessageProcessor pmp = new ProActiveMessageProcessor(message, agent);
        tpe.submit(pmp);
    }

}
