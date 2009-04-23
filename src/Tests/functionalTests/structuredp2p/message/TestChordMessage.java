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
import org.objectweb.proactive.extensions.structuredp2p.message.ChordLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;

public class TestChordMessage {
    private Peer srcPeer;
    private Peer myPeer;
    private LookupMessage lMsg;
    private String id;
    private ResponseMessage response;
    
    
   @Before
   public void init() throws ActiveObjectCreationException, NodeException{
       srcPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
               new Object[] { OverlayType.CAN });  
       
       
       myPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
               new Object[] { OverlayType.CAN });
     id = "55";  
     lMsg = new ChordLookupMessage(id);
   }

   @After
   public void clean(){
     srcPeer = null;
     myPeer = null;
     lMsg = null;
     id = null;
   }
   
   @Test   
   public void testCreate() {     
       assertNotNull("create a new peer", srcPeer);
       assertNotNull("create a new CAN message",lMsg);
       assertNotNull("create a new coordinate table",id);
   }
   
   @Test
   public void testSendMessage(){
       
       response = srcPeer.sendMessage(lMsg);
       assertNotNull("the response is not null",response);
    // TODO test a chord message
   }
    
}
