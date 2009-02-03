package functionalTests.messagerouting.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.util.Sleeper;
import org.objectweb.proactive.extra.forwardingv2.client.AgentV2Internal;
import org.objectweb.proactive.extra.forwardingv2.client.ForwardingAgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.MessageHandler;
import org.objectweb.proactive.extra.forwardingv2.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DebugMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DebugMessage.DebugType;

import functionalTests.messagerouting.BlackBox;


public class ClientIOException extends BlackBox {
    int port;

    @Before
    public void before() throws IOException {
    }

    @Test
    public void test() throws UnknownHostException, ProActiveException, MessageRoutingException {
        InetAddress localhost = InetAddress.getLocalHost();
        Agent agent = new Agent(localhost, super.router.getLocalPort(), FakeMessageHandler.class);
        AgentID agentId = agent.getAgentID();

        Message message;

        message = new DebugMessage(agentId, 1, DebugType.DEB_NOOP);
        agent.sendMsg(message);
        message = new DebugMessage(agentId, 1, DebugType.DEB_DISCONNECT);
        agent.sendMsg(message);

        new Sleeper(1000).sleep();
        message = new DebugMessage(agentId, 1, DebugType.DEB_NOOP);
        agent.sendMsg(message);

        new Sleeper(500000).sleep();
        System.out.println("toto");
    }

    static public class FakeMessageHandler implements MessageHandler {

        public FakeMessageHandler(AgentV2Internal agentV2Internal) {
        }

        public void pushMessage(DataRequestMessage message) {
            // Mock
        }
    }

    static public class Agent extends ForwardingAgentV2 {

        public Agent(InetAddress routerAddr, int routerPort,
                Class<? extends MessageHandler> messageHandlerClass) throws ProActiveException {
            super(routerAddr, routerPort, messageHandlerClass);
        }

        public void sendMsg(Message message) throws MessageRoutingException {
            super.internalSendMsg(message);
        }

    }
}
