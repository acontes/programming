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
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;

public class TestPingMessage {
    
    
    private Peer canPeer;
    private Message msg;
    private ResponseMessage response1;
    private ResponseMessage response2;
    private Peer destPeer;
    
    @Before
    public void init() throws ActiveObjectCreationException, NodeException{
        
    this.canPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN }); 
    
      destPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
              new Object[] { OverlayType.CAN });
     
      msg = new PingMessage();
    }
    
   
    @Test   
    public void testCreate() {     
        assertNotNull("create a new peer", canPeer);
        assertNotNull("create a new CAN message",msg);
        assertNotNull("create a new coordinate table",destPeer);
    }
    @Test   
    public void testSendMessageTo() {      
         response1 = canPeer.sendMessageTo(destPeer, msg);
         response2 = destPeer.sendMessageTo(canPeer, msg);
         assertNotNull("Ping success by bootstrap and random peer",response1);
         assertNotNull("Ping success by random peer and bootstrap",response2);                    
    }
    
    @After
    public void clean(){
      canPeer = null;
      msg = null;
     destPeer = null;
    }
    

}
