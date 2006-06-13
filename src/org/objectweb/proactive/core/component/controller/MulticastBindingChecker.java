package org.objectweb.proactive.core.component.controller;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
import org.objectweb.proactive.core.component.type.annotations.multicast.ClassDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.MethodDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatch;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMode;

/**
 * This class is a utility class for checking that a method invocation can be
 * passed from a client multicast interface to a connected server interface.<br>
 * <p>
 * A request can be transferred from a client multicast interface to a server
 * interface if there is a match between the signature of the invoked method in
 * the (annotated) multicast interface and a method in the server interface.
 * 
 * @author Matthieu Morel
 * 
 */
public class MulticastBindingChecker implements Serializable {
    
    /**
     * client method List<A> foo (B, List<C>) throws E; <br>
     * must be matched by <br>
     * server method A foo(B, C (or list<C>, depending on the dispatch mode) ) throws E;
     * @param clientSideMethod
     * @param serverSideMethods
     * @return
     * @throws ParameterDispatchException
     * @throws NoSuchMethodException
     */
    public static Method searchMatchingMethod(Method clientSideMethod, Method[] serverSideMethods) throws ParameterDispatchException, NoSuchMethodException {
        Method result = null;
        Type clientSideReturnType = clientSideMethod.getGenericReturnType();
        Type[] clientSideParametersTypes = clientSideMethod.getGenericParameterTypes();
        Class[] clientSideExceptionTypes = clientSideMethod.getExceptionTypes();
        ParamDispatch[] paramDispatchModes = getDispatchModes(clientSideMethod);


        serverSideMethodsLoop:
        for (Method serverSideMethod : serverSideMethods) {
            // 1. check names
            if (! serverSideMethod.getName().equals(clientSideMethod.getName())) {
                continue serverSideMethodsLoop;
            }

            // 2. check return types
            if (!(clientSideReturnType == Void.TYPE)) {
            	Type cType = ((ParameterizedType) clientSideMethod.getGenericReturnType()).getActualTypeArguments()[0];
            	Class clientSideReturnTypeArgument = null; 
            	if (cType instanceof ParameterizedType) { 
            		clientSideReturnTypeArgument = (Class)((ParameterizedType)cType).getRawType();
            	} else {
            		clientSideReturnTypeArgument = (Class) cType;	
            	}
                if (!(clientSideReturnTypeArgument.isAssignableFrom(
                            serverSideMethod.getReturnType()))) {
                    continue serverSideMethodsLoop;
                }
            } else {
                if (!(serverSideMethod.getReturnType() == Void.TYPE)) {
                    continue serverSideMethodsLoop;
                }
            }

            // 3. check parameters types
            Type[] serverSideParametersTypes = serverSideMethod.getGenericParameterTypes();

            for (int i=0; i<serverSideMethod.getGenericParameterTypes().length; i++) {
                    if (!(paramDispatchModes[i].match(clientSideParametersTypes[i], serverSideParametersTypes[i]))) {
                        continue serverSideMethodsLoop;
                    }
            }

            // 4. check exception types
            Class[] serverSideExceptionTypes = serverSideMethod.getExceptionTypes();
            for (Class clientExceptionType : clientSideExceptionTypes) {
                boolean match = false;
                for (Class serverExceptionType : serverSideExceptionTypes) {
                    if (clientExceptionType.isAssignableFrom(serverExceptionType)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    throw new NoSuchMethodException("found a matching method in server interface for " + clientSideMethod.toGenericString() + " but the types of thrown exceptions do not match");
                }
            }

            if (result != null) {
                throw new NoSuchMethodException("cannot find matching method for " + clientSideMethod.toGenericString() + " because there are several matches in the server interface ");
            } else {
                result = serverSideMethod;
            }

        }

        if (result == null) {
            throw new NoSuchMethodException("cannot find matching method for " + clientSideMethod.toGenericString());
        }

        return result;
    }

    /**
     * Returns the parameter dispatch mode specified by a {@link ParamDispatchMetadata ParamDispatchMetadata} annotation
     * @param a annotation
     * @return the parameters dipatch mode
     * @throws ParameterDispatchException 
     */
    public static ParamDispatch getParamDispatchMode(ParamDispatchMetadata a)
        throws ParameterDispatchException {
        ParamDispatch mode = null;
        if (a == null) {
            return ParamDispatchMode.BROADCAST;
        }

        mode = a.mode();

        if (mode.equals(ParamDispatchMode.CUSTOM)) {
            try {
                mode = (ParamDispatch) ((ParamDispatchMetadata) a).customMode().newInstance();
            } catch (InstantiationException e) {
                throw new ParameterDispatchException(
                    "custom annotation refers to a class containing the dispatch algorithm, but this class that cannot be instantiated : " +
                    ((ParamDispatchMetadata) a).customMode(),
                    e);
            } catch (IllegalAccessException e) {
                throw new ParameterDispatchException(
                    "custom annotation refers to a class containing the dispatch algorithm, but this class that cannot be instantiated : " +
                    ((ParamDispatchMetadata) a).customMode(),
                    e);
            }
        }

        return mode;
    }

    /**
     * Returns the dispatch modes for the parameters of a method, as specified for each annotated parameter
     * @param matchingMethodInClientInterface a method of a multicast interface
     * @return an array of dispatch modes (default for non-annotated parameters is broadcast)
     * @throws ParameterDispatchException
     */
    public static ParamDispatch[] getDispatchModes(Method matchingMethodInClientInterface) throws ParameterDispatchException {
        ParamDispatch[] result = new ParamDispatch[matchingMethodInClientInterface.getParameterTypes().length];

        Annotation[] classAnnotations = matchingMethodInClientInterface.getDeclaringClass()
                                                                       .getAnnotations();
        Annotation[] methodAnnotations = matchingMethodInClientInterface.getAnnotations();
        Annotation[][] paramsAnnotations = matchingMethodInClientInterface.getParameterAnnotations();

        // class annotation
        for (Annotation annotation : classAnnotations) {
            if (ClassDispatchMetadata.class.isAssignableFrom(annotation.annotationType())) {
                for (int i = 0; i < matchingMethodInClientInterface.getParameterTypes().length;
                     i++) {
                    result[i] = getParamDispatchMode(((ClassDispatchMetadata) annotation).mode());
                    if (result[i] == null) {
                        result[i] = ParamDispatchMode.BROADCAST;
                    }
                }
                return result;
            }
        }

        // method annotation
        for (Annotation annotation : methodAnnotations) {
            if (MethodDispatchMetadata.class.isAssignableFrom(annotation.annotationType())) {
                for (int i = 0; i < matchingMethodInClientInterface.getParameterTypes().length;
                     i++) {
                    result[i] = getParamDispatchMode(((MethodDispatchMetadata) annotation).mode());
                    if (result[i] == null) {
                        result[i] = ParamDispatchMode.BROADCAST;
                    }
                }
                return result;
            }
        }

        // param annotation
        for (int i = 0; i < matchingMethodInClientInterface.getParameterTypes().length; i++) {
            Annotation[] currentParamAnnotations = paramsAnnotations[i];
            for (int j = 0; j < currentParamAnnotations.length; j++) {
                if ((currentParamAnnotations[j] != null) &&
                        ParamDispatchMetadata.class.isAssignableFrom(
                            currentParamAnnotations[j].annotationType())) {
                        result[i] = getParamDispatchMode((ParamDispatchMetadata) currentParamAnnotations[j]);
                        if (result[i] == null) {
                            result[i] = ParamDispatchMode.BROADCAST;
                        }
                }
            }
            if (result[i] == null) {
                    result[i] = ParamDispatchMode.BROADCAST; // default mode
            }

        }

        return result;
    }
}
