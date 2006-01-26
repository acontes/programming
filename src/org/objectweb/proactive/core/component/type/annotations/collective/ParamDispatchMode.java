/**
 *
 */
package org.objectweb.proactive.core.component.type.annotations.collective;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
public enum ParamDispatchMode implements ParamDispatch, Serializable {BROADCAST, ONE_TO_ONE, 
    ROUND_ROBIN, CUSTOM;
    /*
     *
     * @see org.objectweb.proactive.core.component.type.annotations.ParametersDispatch#dispatch(java.util.List, int, int)
     */
    private Map<Integer, Object> dispatch(List<?> inputParameter, int nbOutputReceivers)
        throws ParameterDispatchException {
        Map<Integer, Object> result = new HashMap<Integer, Object>();

        switch (this) {
        case BROADCAST:
            for (int i = 0; i < nbOutputReceivers; i++) {
                result.put(i, inputParameter);
            }
            break;
        case ONE_TO_ONE:
            if (inputParameter.size() != nbOutputReceivers) {
                throw new ParameterDispatchException(
                    "in a one-to-one distribution, the list of parameters on the client side" +
                    "must have a size equal to the number of connected receivers");
            }
            for (int i = 0; i < nbOutputReceivers; i++) {
                result.put(
                    i,
                    inputParameter.get(i));
            }

            break;
        case ROUND_ROBIN:
            for (int i = 0; i < inputParameter.size(); i++) {
                result.put(
                    i,
                    inputParameter.get(i % nbOutputReceivers));
            }
            break;
        default:
            result = BROADCAST.dispatch(inputParameter, nbOutputReceivers);
            break;
        }

        return result;
    }

    public Map<Integer, Object> dispatch(Object inputParameter, int nbOutputReceivers)
        throws ParameterDispatchException {
        if (inputParameter instanceof List) {
            return dispatch((List) inputParameter, nbOutputReceivers);
        }

        // no dispatch in case of non-list parameters
        Map<Integer, Object> result = new HashMap<Integer, Object>();

        for (int i = 0; i < nbOutputReceivers; i++) {
            result.put(i, inputParameter);
        }

        return result;
    }

    /*
     * @see org.objectweb.proactive.core.component.type.annotations.collective.ParamDispatch#getDispatchSize(java.util.List, int)
     */
    private int expectedDispatchSize(List<?> inputParameter, int nbOutputReceivers)
        throws ParameterDispatchException {
        int result = 0;

        switch (this) {
        case BROADCAST:
            result = nbOutputReceivers;
            break;
        case ONE_TO_ONE:
            if (inputParameter.size() != nbOutputReceivers) {
                throw new ParameterDispatchException(
                    "in a one-to-one distribution, the list of parameters on the client side" +
                    "must have a size equal to the number of connected receivers");
            }
            result = nbOutputReceivers;
            break;
        case ROUND_ROBIN:
            result = inputParameter.size();
            break;
        default:
            result = BROADCAST.expectedDispatchSize(inputParameter, nbOutputReceivers);
        }

        return result;
    }

    /*
     * @see org.objectweb.proactive.core.component.type.annotations.collective.ParamDispatch#getDispatchSize(java.lang.Object, int)
     */
    public int expectedDispatchSize(Object inputParameter, int nbOutputReceivers)
        throws ParameterDispatchException {
        if (inputParameter instanceof List) {
            return expectedDispatchSize((List) inputParameter, nbOutputReceivers);
        }

        return nbOutputReceivers;
    }

    /*
     * @see org.objectweb.proactive.core.component.type.annotations.collective.ParamDispatch#matchesClientSideParameterType(java.lang.reflect.Type)
     */
    public boolean match(Type clientSideInputParameterType, Type serverSideInputParameterType)
        throws ParameterDispatchException {
        boolean result = false;
        boolean clientSideParamTypeIsParameterizedType = (clientSideInputParameterType instanceof ParameterizedType);
        boolean serverSideParamTypeIsParameterizedType = (serverSideInputParameterType instanceof ParameterizedType);
        Class clientSideClass = null;
        Class clientSideElementsType = null;
        Class clientSideRawType = null;

        Class serverSideClass = null;
        Class serverSideElementsType = null;
        Class serverSideRawType = null;

        if (clientSideParamTypeIsParameterizedType) {
            clientSideRawType = (Class) ((ParameterizedType) clientSideInputParameterType).getRawType();
            if (!(((ParameterizedType)clientSideInputParameterType).getActualTypeArguments().length == 1)) {
                throw new ParameterDispatchException("client side input parameter type " +
                                                     clientSideInputParameterType + " can only be parameterized with one type");
            }
            clientSideElementsType = ((Class) ((ParameterizedType) clientSideInputParameterType).getActualTypeArguments()[0]);
        } else {
            if (clientSideInputParameterType instanceof Class) {
                clientSideClass = (Class) clientSideInputParameterType;
            } else {
                throw new ParameterDispatchException("client side input parameter type " +
                    clientSideInputParameterType + " can only be either a parameterized type or a class");
            }
        }

        if (serverSideParamTypeIsParameterizedType) {
            serverSideRawType = ((Class) ((ParameterizedType) serverSideInputParameterType).getRawType());
            if (!(((ParameterizedType)serverSideInputParameterType).getActualTypeArguments().length == 1)) {
                throw new ParameterDispatchException("server side input parameter type " +
                                                     serverSideInputParameterType + " can only be parameterized with one type");
            }
            serverSideElementsType = ((Class) ((ParameterizedType) serverSideInputParameterType).getActualTypeArguments()[0]);

            serverSideElementsType = ((Class) ((ParameterizedType) serverSideInputParameterType).getOwnerType());
        } else {
            if (serverSideInputParameterType instanceof Class) {
                serverSideClass = (Class) serverSideInputParameterType;
            } else {
                throw new ParameterDispatchException("server side input parameter type " +
                    serverSideInputParameterType + " is incompatible with " +
                    "client side input parameter type " + clientSideInputParameterType);
            }
        }

        switch (this) {
        case BROADCAST:
            if (clientSideParamTypeIsParameterizedType) {
                if (serverSideParamTypeIsParameterizedType) {
                    result = clientSideRawType.isAssignableFrom(serverSideRawType) && clientSideElementsType.isAssignableFrom(clientSideElementsType);
                } else {
                    result = false; // maybe this constraint should be softened
                }
            } else {
                result = clientSideClass.isAssignableFrom(serverSideClass);
            }
            break;
        case ONE_TO_ONE:
            if (clientSideParamTypeIsParameterizedType) {
                if (clientSideElementsType.isAssignableFrom(serverSideClass)) {
                    result = true;
                } else if (serverSideParamTypeIsParameterizedType) {
                    result = clientSideElementsType.isAssignableFrom(serverSideRawType);
                }
            } else {
                result = false; // maybe this constraint should be softened
            }
            break;
        case ROUND_ROBIN:
            result = ONE_TO_ONE.match(clientSideInputParameterType, serverSideInputParameterType);
            break;
        default:
            result = BROADCAST.match(clientSideInputParameterType, serverSideInputParameterType);
        }

        return result;
    }
}
