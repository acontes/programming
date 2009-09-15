/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.core.body.ft.protocols.cic.managers;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.ft.protocols.cic.infos.MessageInfoCIC;
import org.objectweb.proactive.core.body.ft.protocols.gen.managers.HalfFTManagerGen;


/**
 * This class implements a Communication Induced Checkpointing protocol for ProActive.
 * This FTManager is linked non active object communicating with fault-tolerant active objects.
 * @author The ProActive Team
 * @since ProActive 2.2
 */
public class HalfFTManagerCIC extends HalfFTManagerGen {

    /**
     * @see org.objectweb.proactive.core.body.ft.protocols.FTManager#init(org.objectweb.proactive.core.body.AbstractBody)
     */
    @Override
    public int init(AbstractBody owner) throws ProActiveException {
        super.init(owner);
        this.forSentMessage = new MessageInfoCIC();
        this.forSentMessage.fromHalfBody = true;
        return 0;
    }

}
