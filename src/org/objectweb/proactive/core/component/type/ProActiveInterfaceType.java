package org.objectweb.proactive.core.component.type;

import org.objectweb.fractal.api.type.InterfaceType;

public interface ProActiveInterfaceType extends InterfaceType {
    
    /**
     * Returns the cardinality of this interface. The possible cardinalities are :
     * <ul>
     *  <li> {@link ProActiveTypeFactory#SINGLE_CARDINALITY SINGLE}</li>
     *  <li> {@link ProActiveTypeFactory#COLLECTION_CARDINALITY COLLECTION}</li>
     *  <li> {@link ProActiveTypeFactory#MULTICAST_CARDINALITY MULTICAST}</li>
     *  <li> {@link ProActiveTypeFactory#GATHERCAST_CARDINALITY GATHERCAST}</li>
     *  </ul>
     *
     * @return the cardinality of the interface
     */
    public String getFcCardinality();
    
    public boolean isFcSingleItf();
    
    public boolean isFcMulticastItf();
    
    public boolean isFcGatherCastItf();
    
    

}
