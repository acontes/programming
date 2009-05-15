package functionalTests.structuredp2p.message;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.LoadBalancingMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;

public class TestLoadBalancingMessage {
    
    private Peer srcPeer;
    private Message msg;
    private ResponseMessage response1;
    private ResponseMessage response2;
    private Peer destPeer;
    
    @Before
    public void init() throws ActiveObjectCreationException, NodeException{ 
        this.srcPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN }); 
    
      destPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
              new Object[] { OverlayType.CAN });
      msg = new LoadBalancingMessage();
    }
    
    @After
    public void clean(){
      srcPeer = null;
      msg = null;
     destPeer = null;
    }
    
    @Test
    public void create(){
        assertNotNull("create a new peer", srcPeer);
        assertNotNull("create a new CAN message",msg);
        assertNotNull("create a new coordinate table",destPeer);
    }
    @Test
    public void testSendMessageTo(){
        // TODO   test case ??
    }

}
