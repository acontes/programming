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
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
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
package org.objectweb.proactive.extra.scheduler.exception;

import org.objectweb.proactive.core.exceptions.NonFunctionalException;
import org.objectweb.proactive.core.exceptions.manager.NFEListener;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import static org.objectweb.proactive.extra.scheduler.core.SchedulerCore.logger;


public class NFEHandler implements NFEListener, java.io.Serializable {
	
    /** Serial version UID */
	private static final long serialVersionUID = 6341082431127272393L;
	private String source;

    public NFEHandler() {
    }

    public NFEHandler(String NFESource) {
        source = NFESource;
    }

    public boolean handleNFE(NonFunctionalException e) {
        //TODO: An optimal solution would be to handle the exception for eg if it is due to the fact that the user cant be reached from the user api, the result might need to be sent back tot the core or cached in the uaerapi
        logger.info("##" + source + "had an  NFE",e);
//        e.printStackTrace();
        if (logger.isDebugEnabled()) {
            logger.debug(
                "follows is a print out of the stack trace, warning, hte exception is caught , this is just a printout" +
                ProActiveLogger.getStackTraceAsString(e));
        }
        return true;
    }
}
