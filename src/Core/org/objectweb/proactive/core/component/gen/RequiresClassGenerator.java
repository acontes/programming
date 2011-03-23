package org.objectweb.proactive.core.component.gen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotations.Requires;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.gen.AbstractInterfaceClassGenerator;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.util.ClassDataCache;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;


public class RequiresClassGenerator extends AbstractInterfaceClassGenerator{

	private static RequiresClassGenerator instance;

	public static RequiresClassGenerator instance() {
		if (instance == null) {
			return new RequiresClassGenerator();
		} else {
			return instance;
		}
	}
	
	
	/**
	 * Generates a subclass from root class, if it contains org.osoa.sca.annotations.Property annotation. It adds to
	 * the subclass the getter/setter corresponding to the properties.
	 *
	 * @param classToExtend Name of the class to be set as super class.
	 * @param classToHerit Name of class contains the methods we want to inherit
	 * @return The generated class name.
	 * @throws ClassGenerationFailedException If the generation failed.
	 * @throws NotFoundException 
	 */
	public String generateClass(String classToExtend,String classToHerit) throws InterfaceGenerationFailedException{
		String generatedClassName = Utils.getRequiresClassName(classToExtend);
		try {
			loadClass(generatedClassName);
		} catch (ClassNotFoundException cnfe) {
			try{
				CtClass generatedCtClass = pool.makeClass(generatedClassName);
				generatedCtClass.defrost();
				CtClass superClass = pool.get(classToExtend);
				generatedCtClass.setSuperclass(superClass);
				
				CtClass interfaceToimplement = pool.get("org.objectweb.fractal.api.control.BindingController");
				generatedCtClass.addInterface(interfaceToimplement);
				
				CtClass superClassToHerit = pool.get(classToHerit);
				
				// Get property fields of superclass
				List<CtField> fields = new ArrayList<CtField>(Arrays.asList(superClassToHerit.getFields()));
	            do{
	            	superClassToHerit = superClassToHerit.getSuperclass();
	            	List<CtField> asList = Arrays.asList(superClassToHerit.getDeclaredFields());
					fields.addAll(asList);
	            }while(!superClassToHerit.getName().equals(Object.class.getName()));

                ArrayList<CtField> requiresFields = new ArrayList<CtField>();
                for (int i = 0; i < fields.size(); i++) {
                    if (fields.get(i).hasAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class)){
                        requiresFields.add(fields.get(i));
                    }
                }
                
				String listFCBody = "return new String[] {";
				int i = 0;
				for (i = 0 ; i < requiresFields.size()-1 ; i++) {
					org.objectweb.fractal.fraclet.annotations.Requires tmp = (Requires) requiresFields.get(i).getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
					listFCBody+= "\""+tmp.name() + "\",";
				}
				org.objectweb.fractal.fraclet.annotations.Requires tmp = (Requires) requiresFields.get(i).getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
				listFCBody+="\""+ tmp.name() + "\"};";
                CtMethod listFC = CtNewMethod.make("public String[] listFc() {"+ listFCBody +"}", generatedCtClass);
                generatedCtClass.addMethod(listFC);
                
                String lookupFcBody = "";
                for (i = 0 ; i < requiresFields.size() ; i++) {
                	org.objectweb.fractal.fraclet.annotations.Requires tmp2 = (Requires) requiresFields.get(i).getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
                	lookupFcBody+="if (clientItfName.equals(\""+ tmp2.name() +"\")) {"+
                		"return "+ requiresFields.get(i).getName() +"; }";
                }
                lookupFcBody+="else { return null; }";
                CtMethod lookupFc = CtNewMethod.make("public Object lookupFc(String clientItfName) {"+ lookupFcBody +"}", generatedCtClass);
                generatedCtClass.addMethod(lookupFc);
                
                String bindFcBody = "";
                for (i = 0 ; i < requiresFields.size() ; i++) {
                	org.objectweb.fractal.fraclet.annotations.Requires tmp3 = (Requires) requiresFields.get(i).getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
                	bindFcBody+="if (clientItfName.equals(\""+ tmp3.name() +"\")) {"+
                		requiresFields.get(i).getName() +" = ("+ requiresFields.get(i).getType().getName() +")serverItf; }";
                }
                CtMethod bindFc = CtNewMethod.make("public void bindFc(String clientItfName, Object serverItf) {"+bindFcBody+"}", generatedCtClass);
                generatedCtClass.addMethod(bindFc);
                
                String unbindFcBody = "";
                for (i = 0 ; i < requiresFields.size() ; i++) {
                	org.objectweb.fractal.fraclet.annotations.Requires tmp4 = (Requires) requiresFields.get(i).getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
                	unbindFcBody+="if (clientItfName.equals(\""+ tmp4.name() +"\")) {"+
                		requiresFields.get(i).getName() +" = null ; }";
                }
                CtMethod unbindFc = CtNewMethod.make("public void unbindFc(String clientItfName) {"+unbindFcBody+"}", generatedCtClass);
                generatedCtClass.addMethod(unbindFc);
                
				// Add constructors
				CtConstructor defaultConstructor = CtNewConstructor.defaultConstructor(generatedCtClass);
				generatedCtClass.addConstructor(defaultConstructor);
//
//				generatedCtClass.stopPruning(true);
//				generatedCtClass.writeFile("generated/");
//				System.out.println("[JAVASSIST] generated class: " + generatedClassName);

				// Generate and add to cache the generated class
				byte[] bytecode = generatedCtClass.toBytecode();
				ClassDataCache.instance().addClassData(generatedClassName, bytecode);
				if (logger.isDebugEnabled()) {
					logger.debug("added " + generatedClassName + " to cache");
					logger.debug("generated classes cache is: " + ClassDataCache.instance().toString());
				}
				Utils.defineClass(generatedClassName, bytecode);

				// Defrost the generated class
				generatedCtClass.defrost();
			} catch (Exception e) {
				logger.error("Cannot generate subClass of [" + classToExtend + "] with javassist: " +
						e.getMessage());
				throw new InterfaceGenerationFailedException("Cannot generate subClass of [" + classToExtend +
						"] with javassist", e);
			}

		}
		return generatedClassName;
	}
	

	@Override
	public PAInterface generateInterface(String interfaceName, Component owner,
			PAGCMInterfaceType interfaceType, boolean isInternal,
			boolean isFunctionalInterface)
			throws InterfaceGenerationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

}
