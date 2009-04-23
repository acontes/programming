package functionalTests.structuredp2p.message;


import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.CanLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;

public class TestCanMessage {
    
     private Peer srcPeer;
     private Peer myPeer;
     private LookupMessage lMsg;
     private Coordinate cord[];
     private ResponseMessage srcResponse;
     private ResponseMessage myResponse;
     
     
    @Before
    public void init() throws ActiveObjectCreationException, NodeException{
      srcPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
              new Object[] { OverlayType.CAN });  
      
      
      myPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
              new Object[] { OverlayType.CAN });
      
      cord = new Coordinate[2];
      cord[0] = new Coordinate("aaa");
      cord[1] = new Coordinate("bbb");
      lMsg = new CanLookupMessage(cord);     
    }

  
    @Test   
    public void testCreate() {     
        assertNotNull("create a new peer", srcPeer);
        assertNotNull("get new peer" , myPeer);
        assertNotNull("create a new CAN message",lMsg);
        assertNotNull("create a new coordinate table",cord);
    }
     
    @Test   
    public void testSendMessage() {  
         srcResponse = srcPeer.sendMessage(lMsg);
         PAFuture.waitFor(srcResponse);
         assertNotNull("the src response is not null",srcResponse);
         assertArrayEquals("routing by bootStrap succes" ,((CanLookupResponseMessage)srcResponse).getCoordinates(),cord);
         myResponse = myPeer.sendMessage(lMsg);
         PAFuture.waitFor(myResponse);
         assertNotNull("my response is not null",myResponse);
         assertArrayEquals("good routing by a random peer",((CanLookupResponseMessage)srcResponse).getCoordinates(), cord);
    }
    
    @After
    public void clean(){
      srcPeer = null;
      myPeer = null;
      lMsg = null;
      cord = null;
    }
    
    
}
