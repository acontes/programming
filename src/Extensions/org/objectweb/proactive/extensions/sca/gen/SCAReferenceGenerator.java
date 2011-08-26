/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.objectweb.proactive.extensions.sca.gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.StringMemberValue;
import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.gen.AbstractInterfaceClassGenerator;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;
import org.osoa.sca.annotations.Reference;


/**
 *
 * @author mug
 */
public class SCAReferenceGenerator extends AbstractInterfaceClassGenerator {

    private static SCAReferenceGenerator instance;

    public static SCAReferenceGenerator instance() {
        if (instance == null) {
            return new SCAReferenceGenerator();
        } else {
            return instance;
        }
    }

    /**
     * Generates a subclass from root class which changes sca Reference annotation to fractal's Requires annotation
     *
     * @param rootClass Name of the class to be set as super class.
     * @return The generated class name.
     * @throws ClassGenerationFailedException If the generation failed.
     */
    public String generateClass(String rootClass) throws ClassGenerationFailedException {
        String generatedClassName = Utils.getReferenceClassName(rootClass);
        try {
            loadClass(generatedClassName);
        } catch (ClassNotFoundException cnfe) {
            try {
                CtClass classToEdit = pool.get(rootClass);
                classToEdit.setName(generatedClassName);
                ClassFile ccFile = classToEdit.getClassFile();

                ConstPool constpool = ccFile.getConstPool();

                List<CtField> fields = new ArrayList<CtField>(Arrays.asList(classToEdit.getDeclaredFields()));

                ArrayList<CtField> referencesFields = new ArrayList<CtField>();
                for (int i = 0; i < fields.size(); i++) {
                    Reference tmp = (Reference) fields.get(i).getAnnotation(Reference.class);
                    if (tmp != null) {
                        if (tmp.required()) {
                            referencesFields.add(fields.get(i));
                        }
                    }
                }
                for (CtField ctField : referencesFields) {
                    AnnotationsAttribute attributeRequire = new AnnotationsAttribute(constpool,
                        AnnotationsAttribute.visibleTag);
                    Annotation requiresAnnotation = new Annotation(
                        org.objectweb.fractal.fraclet.annotations.Requires.class.getName(), constpool);
                    requiresAnnotation.addMemberValue("name", new StringMemberValue(ctField.getName(),
                        constpool));
                    attributeRequire.setAnnotation(requiresAnnotation);
                    attributeRequire.addAnnotation(requiresAnnotation);
                    ctField.getFieldInfo().addAttribute(attributeRequire);
                }
                //
                //                classToEdit.stopPruning(true);
                //                classToEdit.writeFile("generated/");
                //                System.out.println("[JAVASSIST] generated class: " + generatedClassName);
                byte[] bytecode = classToEdit.toBytecode();
                Utils.defineClass(generatedClassName, bytecode);
                classToEdit.defrost();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return generatedClassName;
    }

    @Override
    public PAInterface generateInterface(String interfaceName, Component owner,
            PAGCMInterfaceType interfaceType, boolean isInternal, boolean isFunctionalInterface)
            throws InterfaceGenerationFailedException {
        return null;
    }
}
