package org.objectweb.proactive.extra.forwardingv2.router.processor;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public abstract class Processor {

    final static protected Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    abstract public void process();
}
