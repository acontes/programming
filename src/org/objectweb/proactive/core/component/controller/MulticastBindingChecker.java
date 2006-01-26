package org.objectweb.proactive.core.component.controller;

import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
import org.objectweb.proactive.core.component.type.annotations.collective.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.lang.annotation.Annotation;

/**
 * @author Matthieu Morel
 */
public class MulticastBindingChecker {
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
                Class clientSideTypeArgument = (Class) ((ParameterizedType) clientSideMethod.getGenericReturnType()).getActualTypeArguments()[0];

                if (!(clientSideTypeArgument.isAssignableFrom(
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
                try {
                    if (!(paramDispatchModes[i].match(clientSideParametersTypes[i], serverSideParametersTypes[i]))) {
                        continue serverSideMethodsLoop;
                    }
                } catch (NullPointerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
                    ((CustomParamDispatch) a).mode(),
                    e);
            } catch (IllegalAccessException e) {
                throw new ParameterDispatchException(
                    "custom annotation refers to a class containing the dispatch algorithm, but this class that cannot be instantiated : " +
                    ((CustomParamDispatch) a).mode(),
                    e);
            }
        }

        return mode;
    }

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
