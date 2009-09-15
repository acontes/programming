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
package org.objectweb.proactive.core.body.ft.internalmsg;

import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.ft.protocols.cic.managers.FTManagerCIC;
import org.objectweb.proactive.core.body.ft.protocols.replay.managers.FTManagerReplay;


/**
 * This event indicates to the receiver that a global state has been completed.
 * @author The ProActive Team
 * @since ProActive 2.2
 */
public class GlobalStateCompletion implements FTMessage {

    /**
     *
     */
    private int index;

    /**
     * Create a non-fonctional message.
     * @param index the index of the completed global state
     */
    public GlobalStateCompletion(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public Object handleFTMessage(FTManager ftm) {
        if (ftm instanceof FTManagerCIC) {
            return ((FTManagerCIC) ftm).handlingGSCEEvent(this);
        } else if (ftm instanceof FTManagerReplay) {
            return ((FTManagerReplay) ftm).handlingGSCEEvent(this);
        } else {
            // FIXME evallett throw exception
            return null;
        }
    }
}
