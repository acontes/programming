package org.objectweb.proactive.core.body.ft.protocols.replay.servers;

import org.objectweb.proactive.core.body.ft.internalmsg.FTMessage;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.ft.protocols.replay.managers.FTManagerReplay;


public class FTMessageGetIncarnation implements FTMessage {

    private static final long serialVersionUID = 7126902155290159113L;

    @Override
    public Object handleFTMessage(FTManager ftm) {
        return ((FTManagerReplay) ftm).getIncarnation();
    }

}
