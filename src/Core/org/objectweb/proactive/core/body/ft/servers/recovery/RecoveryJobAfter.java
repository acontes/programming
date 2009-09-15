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
package org.objectweb.proactive.core.body.ft.servers.recovery;

import java.io.IOException;

import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.UniversalBodyRemoteObjectAdapter;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.internalmsg.FTMessage;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.ft.protocols.replay.infos.CheckpointInfoReplay;
import org.objectweb.proactive.core.body.ft.protocols.replay.managers.FTManagerReplay;
import org.objectweb.proactive.core.body.ft.servers.util.ActiveQueueJob;


/**
 * Job for recovering an activity.
 * @author The ProActive Team
 * @since 2.2
 */
public class RecoveryJobAfter implements ActiveQueueJob {
    private UniversalBody target;
    private Checkpoint ci;

    /**
     * @param toSend the checkpoint used for the recovery
     * @param incarnation the incarnation number of this recovery
     * @param receiver the node on which the activity is recovered
     */
    public RecoveryJobAfter(Checkpoint ci, UniversalBody target) {
        super();
        this.target = target;
        this.ci = ci;
    }

    public void doTheJob() {
        try {
            target.receiveFTMessage(new FTMessageForSendLogs(ci));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((UniversalBodyRemoteObjectAdapter) target).startBody();
    }

    public static class FTMessageForSendLogs implements FTMessage {
        private static final long serialVersionUID = 1011929127211012737L;
        private CheckpointInfoReplay cpr;

        public FTMessageForSendLogs(Checkpoint ci) {
            cpr = (CheckpointInfoReplay) ci.getCheckpointInfo();
        }

        @Override
        public Object handleFTMessage(FTManager ftm) {
            ((FTManagerReplay) ftm).sendLogs(cpr);
            return null;
        }
    }
}
