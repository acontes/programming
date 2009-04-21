package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.response.CanResponseMessage;


/**
 *
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class CanMessage implements Message{
   
    private Coordinate coordinate[];
    public CanMessage(Coordinate cord[]){
        
    }
    @Override
    public CanResponseMessage handle(Peer peer) {
        // TODO Auto-generated method stub
        return null;
        
    }

}
