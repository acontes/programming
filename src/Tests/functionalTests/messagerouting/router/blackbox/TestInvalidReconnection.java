package functionalTests.messagerouting.router.blackbox;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage.ErrorType;

import functionalTests.messagerouting.BlackBox;


public class TestInvalidReconnection extends BlackBox {

    /*
     * Send an invalid Registration Request with an AgentID.
     * Since this AgentID is not know by the router, an error message
     * if expected in response
     *
     * An error message is expected is expected
     */
    @Test
    public void test() throws IOException {
        AgentID agentID = new AgentID(0xcafe);

        Message message = new RegistrationRequestMessage(agentID, ProActiveRandom.nextPosLong());
        tunnel.write(message.toByteArray());

        byte[] resp = tunnel.readMessage();
        ErrorMessage error = (ErrorMessage) Message.constructMessage(resp, 0);
        Assert.assertEquals(ErrorType.ERR_INVALID_AGENT_ID, error.getErrorType());
        Assert.assertNull(error.getRecipient()); // Not set since it was invalid
        Assert.assertNull(error.getSender());
    }
}
