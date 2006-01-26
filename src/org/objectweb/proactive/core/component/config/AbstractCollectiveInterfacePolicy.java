package org.objectweb.proactive.core.component.config;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import nonregressiontest.component.Message;

import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.group.ProxyForComponentInterfaceGroup;
import org.objectweb.proactive.core.mop.MethodCall;


public abstract class AbstractCollectiveInterfacePolicy implements
        CollectiveInterfacePolicy {

    private String itfName;
    private ProActiveInterfaceType clientItfType;
    private ProActiveInterfaceType serverItfType;
    private Class serverItfClass;

    public final static String BROADCAST_COMPATIBLE_SUFFIX = "_broadcastCompatible";
    
    

    
    /**
     * @param serverItfClass The serverItfClass to set.
     */
    public void setServerItfSignature(String serverItfSignature) {
        try {
            this.serverItfClass = Class.forName(serverItfSignature);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ProActiveRuntimeException("invalid signature for server interface : " + serverItfSignature);
        }
    }

    public void setServerItfType(ProActiveInterfaceType serverItfType)
            throws ClassNotFoundException {

        this.serverItfType = serverItfType;
    }

    /**
     * @param serverItfType
     */
//    private Class extractMatchingClass(ProActiveInterfaceType serverItfType) {
//
//        try {
//            serverItfClass = Class.forName(serverItfType.getFcItfSignature());
//            // default broadcast behaviour
//            Method[] broadcastCompatibleMethods = serverItfClass.getMethods();
//            ClassPool pool = ClassPool.getDefault();
//            CtClass compatibleItf = pool.makeInterface(serverItfType
//                    .getFcItfSignature()
//                    + BROADCAST_COMPATIBLE_SUFFIX);
//            for (Method m : broadcastCompatibleMethods) {
//                if (!(Void.class.isAssignableFrom(m.getReturnType()) && !(List.class
//                        .isAssignableFrom(m.getReturnType())))) {
//                    throw new ProActiveRuntimeException(
//                            "method "
//                                    + m.getName()
//                                    + " in class "
//                                    + serverItfType.getFcItfSignature()
//                                    + " returns "
//                                    + m.getReturnType().getName()
//                                    + " whereas for multicast interfaces, methods can only return void or List<T>");
//                }
//                Type returnType = m.getGenericReturnType();
//                if (!(returnType instanceof ParameterizedType)) {
//                    throw new ProActiveRuntimeException(
//                            "method "
//                                    + m.getName()
//                                    + " in class "
//                                    + serverItfType.getFcItfSignature()
//                                    + " does not return a parameterized type : "
//                                    + "it is not possible to infer a matching method in a server interface connected to the multicast interface with such signature");
//                }
//
//                CtClass[] ctParametersTypes = new CtClass[m.getParameterTypes().length];
//                Class[] parametersTypes = m.getParameterTypes();
//                for (int i = 0; i < parametersTypes.length; i++) {
//                    ctParametersTypes[i] = pool.get(parametersTypes[i]
//                            .getName());
//                }
//
//                CtClass[] ctExceptionsTypes = new CtClass[m.getExceptionTypes().length];
//                Class[] exceptionsTypes = m.getExceptionTypes();
//                for (int i = 0; i < exceptionsTypes.length; i++) {
//                    ctExceptionsTypes[i] = pool.get(exceptionsTypes[i]
//                            .getName());
//                }
//                Type[] parameterizingTypes = ((ParameterizedType) returnType)
//                        .getActualTypeArguments();
//
//                CtMethod compatible = CtNewMethod
//                        .make(
//                              pool.get(m.getGenericReturnType().getClass()
//                                      .getTypeParameters()[0].getName()), m
//                                      .getName(), ctParametersTypes,
//                              ctExceptionsTypes, null, compatibleItf);
//                compatibleItf.addMethod(compatible);
//            }
//            return compatibleItf.toClass(Thread.currentThread().getContextClassLoader());
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ProActiveRuntimeException(e.getMessage());
//        }
//    }

    /**
     * @param mc
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     */
    protected MethodCall adapt(MethodCall mc) throws ClassNotFoundException,
            NoSuchMethodException {

        // should probably do the reverse transformation for gathercast
        // interfaces

        
        // 1. adapt method signatures
        Method matchingMethod = getMatchingMethod(mc.getReifiedMethod().toString(),serverItfClass);
        MethodCall adaptedMc = MethodCall
                .getComponentMethodCall(
                                        matchingMethod, mc
                                                .getEffectiveArguments(), mc
                                                .getComponentInterfaceName());
        return adaptedMc;
    }

    public MethodCall adaptAndDistribute(MethodCall mc, ProxyForComponentInterfaceGroup delegatee) {

        try {
            MethodCall adaptedMc = adapt(mc);
            distributeParameters(adaptedMc, delegatee);
            return adaptedMc;
        } catch (SecurityException e) {
            throw new ProActiveRuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new ProActiveRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new ProActiveRuntimeException(e);
        }
    }

    /**
     * Extracts method from a given class and method signature
     * 
     * @param methodSignature
     *            signature (as given by
     *            {@link Method#toString() Method#toString()}
     * @param clazz
     *            the class in which the method is looked for
     * @return a corresponding Method object
     * @throws ClassNotFoundException
     *             if the class of a parameter is not found
     * @throws SecurityException
     *             if there is an invalid class loading
     * @throws NoSuchMethodException
     *             if the method is not found
     */
    public static Method getMatchingMethod(String methodSignature, Class clazz)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException {

        String tmp = methodSignature.substring(
                                               0, methodSignature.indexOf(')'));
        tmp = tmp.substring(
                                          tmp.lastIndexOf(" ") + 1, tmp.indexOf('('));
        String methodName = tmp.replace(tmp.substring(0, tmp.lastIndexOf('.'))+'.', "");
        String paramTypesString = methodSignature
                .substring(
                           methodSignature.indexOf('(') + 1, methodSignature
                                   .lastIndexOf(')'));
        paramTypesString.replaceAll(
                                    " ", "");
        String[] paramTypesStringArray = paramTypesString.split(",");
        Class[] paramTypes = new Class[paramTypesStringArray.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Class.forName(Message.class.getName());
            paramTypes[i] = Class.forName(paramTypesStringArray[i]);
        }
        return clazz.getMethod(
                               methodName, paramTypes);
    }

}
