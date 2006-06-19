package org.objectweb.proactive.p2p.service.messages;

import java.io.Serializable;
import java.util.Vector;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.p2p.service.P2PService;
import org.objectweb.proactive.p2p.service.util.UniversalUniqueID;


public class AcquaintanceRequest extends Message implements Serializable {
    public AcquaintanceRequest(int i) {
        super(i);
    }

    /**
     * Generates an acquaintance reply
     */
    public void execute(P2PService target) {
        if (!target.stubOnThis.equals(this.sender)) {
            Vector<String> result = target.acquaintanceManager.add(this.sender);
            result = (Vector<String>) ProActive.getFutureValue(result);

            if (result == null) {
                //we have accepted the acquaintance request
                logger.info("Register request from " +
                    ProActive.getActiveObjectNodeUrl(this.sender) +
                    " accepted");
                this.sender.message(new AcquaintanceReply(1,
                        target.generateUuid(), target.stubOnThis,
                        ProActive.getActiveObjectNodeUrl(target.stubOnThis)));
                //service.registerAnswer(ProActive.getActiveObjectNodeUrl(target.stubOnThis),target.stubOnThis);
            } else {
                logger.info("Register request from " +
                    ProActive.getActiveObjectNodeUrl(this.sender) +
                    " rejected");
                //service.registerAnswer(ProActive.getActiveObjectNodeUrl(target.stubOnThis), result);
                this.sender.message(new AcquaintanceReply(1,
                        target.generateUuid(), target.stubOnThis,
                        ProActive.getActiveObjectNodeUrl(target.stubOnThis),
                        result));
            }
        }
    }

    /**
     * This is message should not be forwarded
     */
    public void transmit(P2PService acq) {
    }
}
