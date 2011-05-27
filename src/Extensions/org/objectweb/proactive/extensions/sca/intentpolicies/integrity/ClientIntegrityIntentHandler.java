/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.sca.intentpolicies.integrity;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


/**
 * This class defines the integrity intent handler on the client side which is used to check data 
 * integration during transfer. It uses a checksum algorithm.
 *
 * @author The ProActive Team
 */
public class ClientIntegrityIntentHandler extends IntentHandler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS);

    public Object invoke(IntentJoinPoint ijp) throws Throwable {
        byte[] rawData = (byte[]) ijp.getArgs()[0];
        Checksum checksumEngine = new Adler32();
        checksumEngine.update(rawData, 0, rawData.length);
        long checksum = checksumEngine.getValue();
        checksumEngine.reset();
        ByteBuffer dataBuffer = ByteBuffer.allocate(rawData.length + 8);
        dataBuffer.putLong(checksum);
        dataBuffer.put(rawData);

        ijp.setArgs(new Object[] { dataBuffer.array() });

        if (logger.isDebugEnabled()) {
            logger.debug("Checksum client = " + checksum);
        }

        Object ret = ijp.proceed();
        return ret;
    }
}
