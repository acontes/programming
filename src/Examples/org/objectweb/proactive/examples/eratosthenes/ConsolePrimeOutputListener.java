/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.examples.eratosthenes;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;


/**
 * @author The ProActive Team
 * Serves to print newly found prime numbers to the console.
 */
@ActiveObject
public class ConsolePrimeOutputListener implements PrimeOutputListener, java.io.Serializable {
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);
    private long startTime;
    private int numberCounter;

    /**
     * Constructor for ConsolePrimeOutputListener.
     */
    public ConsolePrimeOutputListener() {
        super();
    }

    public void newPrimeNumberFound(long n) {
        numberCounter++;
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        String time = Long.toString((System.currentTimeMillis() - startTime) / 1000);
        String counter = Integer.toString(numberCounter);
        StringBuffer line = new StringBuffer(50);
        line.append("    ");
        line.append("Prime number ");
        for (int i = counter.length(); i < 6; i++)
            line.append(' ');
        line.append('#');
        line.append(counter);
        line.append(" found with value ");
        line.append(n);
        line.append("\t (");
        for (int i = time.length(); i < 6; i++)
            line.append('0');
        line.append(time);
        line.append("s)\n");
        logger.info(line);
    }
}
