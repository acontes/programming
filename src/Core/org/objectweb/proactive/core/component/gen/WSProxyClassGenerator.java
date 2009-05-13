/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.gen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Modifier;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.ProActiveInterfaceImpl;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.component.type.WSComponent;
import org.objectweb.proactive.core.component.webservices.ProActiveWSCaller;
import org.objectweb.proactive.core.component.webservices.WSInfo;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.util.ClassDataCache;


/**
 * This class generates a proxy for binding a client interface of a component to a web service.
 * <br>
 * The generated class implements the Java interface corresponding to the client interface and
 * extends the {@link ProActiveInterfaceImpl} class.
 * <br>
 * Its {@link InterfaceType} is the server side {@link InterfaceType} corresponding to the
 * {@link InterfaceType} of the client interface.
 * <br>
 * Its owner is a {@link WSComponent} component containing the URL and the name of the class
 * to use to call the web service.
 * <br>
 * The functional interface implementation returned by calling {@link ProActiveInterface#getFcItfImpl()}
 * is the URL of the web service.
 *
 * @author The ProActive Team
 * @see ProActiveInterfaceImpl
 * @see InterfaceType
 * @see WSComponent
 */
public class WSProxyClassGenerator extends AbstractInterfaceClassGenerator {
    private static WSProxyClassGenerator instance;

    public static WSProxyClassGenerator instance() {
        if (instance == null) {
            return new WSProxyClassGenerator();
        } else {
            return instance;
        }
    }

    /**
     * Generate a proxy for binding a client interface of a component to a web service.
     *
     * @param interfaceName Name of the component interface of the generated class.
     * @param owner Component owner of the generated class. Should always be an instance of {@link WSComponent}.
     * @param interfaceType {@link InterfaceType} of the generated class.
     * @param isInternal Specify if the generated class is a component internal interface. Should always be false.
     * @param isFunctionalInterface Specify if the generated class is a component functional interface. Should
     * always be true.
     * @return The generated class.
     * @see InterfaceType
     * @see WSComponent
     */
    public ProActiveInterface generateInterface(String interfaceName, Component owner,
            ProActiveInterfaceType interfaceType, boolean isInternal, boolean isFunctionalInterface)
            throws InterfaceGenerationFailedException {
        try {
            WSInfo wsInfo = ((WSComponent) owner).getWSInfo();
            String wsProxyClassName = Utils.getWSProxyClassName(interfaceName, interfaceType
                    .getFcItfSignature(), wsInfo.getWSCallerClassName());
            Class<?> generatedClass;

            // Check whether class has already been generated
            try {
                generatedClass = loadClass(wsProxyClassName);
            } catch (ClassNotFoundException cnfe) {
                // Create class
                CtClass generatedCtClass = pool.makeClass(wsProxyClassName);

                // Set super class
                generatedCtClass.setSuperclass(pool.get(ProActiveInterfaceImpl.class.getName()));

                // Set interfaces to implement
                List<CtClass> itfs = new ArrayList<CtClass>();
                itfs.add(pool.get(interfaceType.getFcItfSignature()));
                itfs.add(pool.get(Serializable.class.getName()));
                generatedCtClass.setInterfaces(itfs.toArray(new CtClass[0]));
                addSuperInterfaces(itfs);

                // Add private fields
                CtField wsCallerField = new CtField(pool.get(ProActiveWSCaller.class.getName()), "wsCaller",
                    generatedCtClass);
                wsCallerField.setModifiers(Modifier.PRIVATE);
                generatedCtClass.addField(wsCallerField);
                CtField wsUrlField = new CtField(pool.get(Object.class.getName()), "wsUrl", generatedCtClass);
                wsUrlField.setModifiers(Modifier.PRIVATE);
                generatedCtClass.addField(wsUrlField);
                CtField proxyField = new CtField(pool.get(Proxy.class.getName()), "proxy", generatedCtClass);
                proxyField.setModifiers(Modifier.PRIVATE);
                generatedCtClass.addField(proxyField);

                // Add constructor
                CtConstructor constructorNoParam = CtNewConstructor.make("public " +
                    generatedCtClass.getSimpleName() + "() {\nwsCaller = new " +
                    wsInfo.getWSCallerClassName() + "();\n}", generatedCtClass);
                generatedCtClass.addConstructor(constructorNoParam);

                // Add getter and setter for private fields
                CtMethod getterFcItfName = CtNewMethod.getter("getFcItfImpl", wsUrlField);
                generatedCtClass.addMethod(getterFcItfName);
                CtMethod setterFcItfName = CtNewMethod.setter("setFcItfImpl", wsUrlField);
                generatedCtClass.addMethod(setterFcItfName);
                CtMethod getterProxy = CtNewMethod.getter("getProxy", proxyField);
                generatedCtClass.addMethod(getterProxy);
                CtMethod setterProxy = CtNewMethod.setter("setProxy", proxyField);
                generatedCtClass.addMethod(setterProxy);

                //  Add methods from implemented interfaces
                for (CtClass itf : itfs) {
                    CtMethod[] methods = itf.getDeclaredMethods();
                    for (CtMethod method : methods) {
                        String methodName = method.getName();
                        CtClass[] parametersType = method.getParameterTypes();
                        CtClass returnType = method.getReturnType();
                        String body = "{\nif (wsUrl != null) {\n";
                        if (returnType != CtClass.voidType) {
                            body += "return ($r) ";
                        }
                        body += "wsCaller.callWS((String) wsUrl, \"" + methodName + "\", $args";
                        if (returnType == CtClass.voidType) {
                            body += ", null);\n";
                        } else {
                            body += ", new Class[] { $type })[0];\n";
                        }
                        body += "}\nelse {\nSystem.err.println(\"No URL defined, cannot invoke web service\");\n";
                        if (returnType != CtClass.voidType) {
                            body += "return " + (returnType.isPrimitive() ? "0" : "null") + ";\n";
                        }
                        body += "}\n}";
                        CtMethod methodToGenerate = CtNewMethod.make(returnType, methodName, parametersType,
                                method.getExceptionTypes(), body, generatedCtClass);
                        generatedCtClass.addMethod(methodToGenerate);
                    }
                }

                //                generatedCtClass.stopPruning(true);
                //                generatedCtClass.writeFile("generated/");
                //                System.out.println("[JAVASSIST] generated class: " + wsProxyClassName);

                // Generate and add to cache the generated class
                byte[] bytecode = generatedCtClass.toBytecode();
                ClassDataCache.instance().addClassData(wsProxyClassName, bytecode);
                if (logger.isDebugEnabled()) {
                    logger.debug("added " + wsProxyClassName + " to cache");
                    logger.debug("generated classes cache is: " + ClassDataCache.instance().toString());
                }
                generatedClass = Utils.defineClass(wsProxyClassName, bytecode);
            }

            // Instantiate class
            ProActiveInterfaceImpl reference = (ProActiveInterfaceImpl) generatedClass.newInstance();
            reference.setFcItfName(interfaceName);
            reference.setFcItfOwner(owner);
            reference.setFcType(interfaceType);
            reference.setFcIsInternal(isInternal);
            reference.setFcItfImpl(wsInfo.getWSUrl());
            reference.setProxy(null);

            // Set up owner
            ((WSComponent) owner).setFcInterfaceType(interfaceType);
            ((WSComponent) owner).setFcInterfaceImpl(reference);

            return reference;
        } catch (Exception e) {
            throw new InterfaceGenerationFailedException(
                "Cannot generate web service proxy [" + interfaceName + "] with signature [" +
                    interfaceType.getFcItfSignature() + "] with javassist", e);
        }
    }
}
