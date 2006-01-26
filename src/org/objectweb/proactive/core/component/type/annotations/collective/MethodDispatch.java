package org.objectweb.proactive.core.component.type.annotations.collective;

import java.lang.reflect.Type;
import java.util.Map;

import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;


public interface MethodDispatch {
    
    public Map<Integer, Object>[] dispatch(Object[] inputParameters, int nbOutputReceivers) throws ParameterDispatchException;
    
    public int expectedDispatchSize (Object[] inputParameters, int nbOutputReceivers) throws ParameterDispatchException;
    

}
