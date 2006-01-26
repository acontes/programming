package org.objectweb.proactive.core.component.type.annotations.collective;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;


public interface ParamDispatch {
    
    
    public Map<Integer, Object> dispatch(Object inputParameter, int nbOutputReceivers) throws ParameterDispatchException;
    
    public int expectedDispatchSize (Object inputParameter, int nbOutputReceivers) throws ParameterDispatchException;
    
    public boolean match(Type clientSideInputParameter, Type serverSideInputParameter) throws ParameterDispatchException;

}
