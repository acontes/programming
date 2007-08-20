/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extensions.calcium.examples.blast;

import java.net.URL;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.calcium.exceptions.EnvironmentException;
import org.objectweb.proactive.extensions.calcium.exceptions.MuscleException;
import org.objectweb.proactive.extensions.calcium.muscle.Execute;


public class ExecuteBlast extends AbstractExecuteCommand implements Execute<BlastParameters, BlastParameters> {
    static Logger logger = ProActiveLogger.getLogger(Loggers.SKELETONS_APPLICATION);

    public BlastParameters execute(BlastParameters param)
        throws EnvironmentException {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing Blast. Database=" +
                param.getDatabaseFile().getAbsolutePath() + " Query=" +
                param.getQueryFile().getAbsoluteFile());
        }

        super.execProcess(param.getBlastParemeterString(),
            param.getWorkingDirectory());

        return param;
    }

    @Override
    public URL getProgramURL() throws EnvironmentException {

        /*
        String osName=System.getProperty("os.name");
        String osArch=System.getProperty("os.arch");
        String osVersion=System.getProperty("os.version");
        */
        String osName = System.getProperty("os.name");

        if (!osName.equals("Linux")) {
            throw new EnvironmentException("Linux machines are required");
        }

        URL url = Blast.class.getClass()
                             .getResource("/org/objectweb/proactive/calcium/examples/blast/bin/linux/blastall");

        if (url == null) {
            throw new MuscleException("Unable to find formatdb binary");
        }

        return url;
    }
}
