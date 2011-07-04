/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.sca.gen;

import java.security.PrivateKey;
import java.security.PublicKey;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import org.oasisopen.sca.annotation.Property;
import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.gen.AbstractInterfaceClassGenerator;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;
import org.objectweb.proactive.extensions.sca.intentpolicies.authentification.AuthentificationItf;


/**
 * Defines {@link #generateClass(String)} method which generates a subclass based on original one. It generate
 * necessary elements for a server's authentication service.
 *
 * @author The ProActive Team
 */
public class BusinessClassWithAuthentificationGenerator extends AbstractInterfaceClassGenerator {

    private static BusinessClassWithAuthentificationGenerator instance;

    public static BusinessClassWithAuthentificationGenerator instance() {
        if (instance == null) {
            return new BusinessClassWithAuthentificationGenerator();
        } else {
            return instance;
        }
    }

    /**
     * Generates a modified class from root class to add necessary elements to a server component.
     *
     * @param classToExtend Name of the class to be set as super class.
     * @param classToHerit Name of class contains the methods we want to inherit
     * @return The generated class name.
     * @throws ClassGenerationFailedException If the generation failed.
     */
    public String generateClass(String rootClass) throws ClassGenerationFailedException {
        String generatedClassName = Utils.getBusinessClassWithAuthentificationName(rootClass);
        try {
            loadClass(generatedClassName);
        } catch (ClassNotFoundException cnfe) {
            try {
                CtClass classToEdit = pool.get(rootClass);
                classToEdit.setName(generatedClassName);
                ClassFile ccFile = classToEdit.getClassFile();

                ConstPool constpool = ccFile.getConstPool();

                CtClass privateKeyClass = pool.get(PrivateKey.class.getName());

                CtClass publicKeyClass = pool.get(PublicKey.class.getName());

                CtClass authItf = pool.get(AuthentificationItf.class.getName());

                classToEdit.addInterface(authItf);

                AnnotationsAttribute attributeProperty = new AnnotationsAttribute(constpool,
                    AnnotationsAttribute.visibleTag);

                Annotation propertyAnnotation = new Annotation(constpool, pool.get(Property.class.getName()));

                attributeProperty.addAnnotation(propertyAnnotation);

                CtField pkey = new CtField(publicKeyClass, "publicKey", classToEdit);
                pkey.setModifiers(Modifier.PROTECTED);
                pkey.getFieldInfo().addAttribute(attributeProperty);
                classToEdit.addField(pkey);
                //ccFile.addAttribute(attr);

                CtField skey = new CtField(privateKeyClass, "privateKey", classToEdit);
                skey.setModifiers(Modifier.PROTECTED);
                skey.getFieldInfo().addAttribute(attributeProperty);
                classToEdit.addField(skey);

                AnnotationsAttribute attributeRequire = new AnnotationsAttribute(constpool,
                    AnnotationsAttribute.visibleTag);

                Annotation requiresAnnotation = new Annotation(
                    org.objectweb.fractal.fraclet.annotations.Requires.class.getName(), constpool);
                requiresAnnotation.addMemberValue("name", new StringMemberValue(
                    AuthentificationItf.CLIENT_ITF_NAME, constpool));

                attributeRequire.setAnnotation(requiresAnnotation);
                attributeRequire.addAnnotation(requiresAnnotation);

                CtField auth = new CtField(authItf, "auth", classToEdit);
                auth.setModifiers(Modifier.PROTECTED);
                auth.getFieldInfo().addAttribute(attributeRequire);
                classToEdit.addField(auth);

                String sendPublicKeyBody = "return publicKey;";

                CtMethod sendPublicKey = CtNewMethod.make("public java.security.PublicKey sendPublicKey() {" +
                    sendPublicKeyBody + "}", classToEdit);
                classToEdit.addMethod(sendPublicKey);

                String getPublicKeyFromServerBody = "return auth.sendPublicKey();";
                CtMethod getPublicKeyFromServer = CtNewMethod.make(
                        "public java.security.PublicKey getPublicKeyFromServer() {" +
                            getPublicKeyFromServerBody + "}", classToEdit);
                classToEdit.addMethod(getPublicKeyFromServer);

                String generateKeyBody = ""
                    + "java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());"
                    + "if(privateKey != null){" + "			return;" + "}"
                    + "java.security.KeyPairGenerator kpg = null;" + "try {"
                    + "kpg = java.security.KeyPairGenerator.getInstance(\"RSA\");"
                    + "} catch (java.security.NoSuchAlgorithmException e) {" + "}" + "kpg.initialize(1024);"
                    + "java.security.KeyPair kp = kpg.genKeyPair();" + "publicKey = kp.getPublic();"
                    + "privateKey = kp.getPrivate();";

                CtMethod generateKey = CtNewMethod.make("protected void generateKeys() {" + generateKeyBody +
                    "}", classToEdit);
                classToEdit.addMethod(generateKey);

                // Add constructors
                CtConstructor constructorNoParam = classToEdit.getDeclaredConstructor(null);
                constructorNoParam.insertAfter("{generateKeys();}");

                //				classToEdit.stopPruning(true);
                //				classToEdit.writeFile("generated/");
                //				System.out.println("[JAVASSIST] generated class: " + generatedClassName);
                byte[] bytecode = classToEdit.toBytecode();
                Utils.defineClass(generatedClassName, bytecode);
                classToEdit.defrost();
                //classToEdit.toClass();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return generatedClassName;
    }

    /*
     * Non used.
     */
    public PAInterface generateInterface(String interfaceName, Component owner,
            PAGCMInterfaceType interfaceType, boolean isInternal, boolean isFunctionalInterface)
            throws InterfaceGenerationFailedException {
        return null;
    }
}
