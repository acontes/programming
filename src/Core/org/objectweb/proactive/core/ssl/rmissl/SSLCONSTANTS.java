package org.objectweb.proactive.core.ssl.rmissl;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class SSLCONSTANTS {

    static final public Logger logger = ProActiveLogger.getLogger(Loggers.SSL);

    public static String CIPHERS_SEPARATOR = ",";
    public static String[] enabled_ciphers;

    static {
        enabled_ciphers = PAProperties.PA_SSL_CIPHER_SUITES.getValue().split(CIPHERS_SEPARATOR);
        if (logger.isDebugEnabled()) {
            for (String cipher : enabled_ciphers) {
                logger.debug("enabled cipher for SSL connections " + cipher);
            }
        }

    }
}
