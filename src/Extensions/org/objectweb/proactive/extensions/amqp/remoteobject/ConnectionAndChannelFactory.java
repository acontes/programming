/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.amqp.remoteobject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.amqp.AMQPConfig;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * Connection and Factory to enable connection and channel caching and reuse
 * A connection creates almost 8 threads and a channel 4 threads so reusing them is a good idea.
 * Tests have shown that reusing a connection is easy while reusing a channel is a bad idea as 
 * channels are closed each time an exception occurs.
 * In the current implementation, we only reuse connections, channels are not reused.
 * @since 5.2.0
 *
 */
public class ConnectionAndChannelFactory {

    final static private Logger logger = ProActiveLogger.getLogger(AMQPConfig.Loggers.AMQP_CHANNEL_FACTORY);

    private static final ConnectionAndChannelFactory instance = new ConnectionAndChannelFactory();

    private final Map<String, Connection> cachedConnections = new HashMap<String, Connection>();

    public static ConnectionAndChannelFactory getInstance() {
        return instance;
    }

    /**
     * provides connection caching and reuse.
     * @param hostname the hostname to the broker
     * @param port the port to the broker
     * @return a Connection to the requested broker
     * @throws IOException is the broker cannot be contacted
     */
    public synchronized Connection getConnection(String hostname, int port) throws IOException {
        String key = generateKey(hostname, port);
        Connection connection = cachedConnections.get(key);

        if (connection != null) {
            if (connection.isOpen()) {
                return connection;
            } else {
                cachedConnections.remove(key);
            }
        }

        logger.debug(String.format("requested connection to %s is close, creating a new one", key));

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        factory.setPort(port);

        connection = factory.newConnection();
        connection.addShutdownListener(new AMQPShutDownListener(connection.toString()));

        logger.debug(String.format("checking new connection to %s, isOpen() %s", connection.toString(),
                connection.isOpen()));

        cachedConnections.put(key, connection);

        return connection;
    }

    /**
     * provide a channel, try to reuse a connection if already exists
     * @param hostname the broker to contact
     * @param port the port of the broker
     * @param reuse if we want to reuse an already opened channel (bad idea so far)
     * @return a channel  
     * @throws IOException if something went wrong
     */
    public Channel getChannel(String hostname, int port) throws IOException {
        Channel channel;

        Connection connection = getConnection(hostname, port);
        channel = connection.createChannel();

        return channel;
    }

    private static String generateKey(String hostname, int port) {
        return hostname + port;
    }

}
