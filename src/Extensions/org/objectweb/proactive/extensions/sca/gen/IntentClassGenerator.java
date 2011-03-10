package org.objectweb.proactive.extensions.sca.gen;

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.gen.AbstractInterfaceClassGenerator;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.util.ClassDataCache;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;

public class IntentClassGenerator extends AbstractInterfaceClassGenerator{

	private static IntentClassGenerator instance;

	public static IntentClassGenerator instance() {
		if (instance == null) {
			return new IntentClassGenerator();
		} else {
			return instance;
		}
	}


	/**
	 * Generates a subclass from root class, if it contains org.osoa.sca.annotations.Property annotation. It adds to
	 * the subclass the getter/setter corresponding to the properties.
	 *
	 * @param className Name of the component content class.
	 * @return The generated class name.
	 * @throws ClassGenerationFailedException If the generation failed.
	 * @throws NotFoundException 
	 */
	public String generateClass(String className) throws ClassGenerationFailedException{
		String generatedClassName = Utils.getIntentClassName(className);
		try {
			loadClass(generatedClassName);
		} catch (ClassNotFoundException cnfe) {
			try{
				CtClass generatedCtClass = pool.makeClass(generatedClassName);
				generatedCtClass.defrost();
				CtClass superClass = pool.get(className);
				generatedCtClass.setSuperclass(superClass);

				CtMethod[] methods = superClass.getDeclaredMethods();
				List<CtMethod> extentedMethodes = new ArrayList<CtMethod>();
				String superClassName = superClass.getName();
				for (CtMethod ctMethod : methods) {
					extentedMethodes.add(ctMethod);
				}
				// Add intentHandlers instance fields
				for (CtMethod ctExtentedMethod : extentedMethodes) {
					CtField intentHandlerArray = CtField
					.make("private java.util.List intentArray"+ctExtentedMethod.getName()+"= new java.util.ArrayList();"
							, generatedCtClass);
					generatedCtClass.addField(intentHandlerArray);
					String IArrayName = intentHandlerArray.getName(); 

					CtField intentHandlerCounter = CtField
					.make("private int counter"+ctExtentedMethod.getName()+"= -1;"
							, generatedCtClass);
					generatedCtClass.addField(intentHandlerCounter);
					String ICounterName = intentHandlerCounter.getName();

					CtMethod newMethod = CtNewMethod.delegator(ctExtentedMethod, generatedCtClass);
					newMethod.setBody("{\n" +
							ICounterName+"++;\n"+
							//"System.out.println("+ ICounterName +");" +
							//"System.out.println("+ IArrayName +".size());" +
							"if("+ICounterName+" < "+IArrayName+".size()){"+
							"return ($r)((org.objectweb.proactive.extensions.sca.control.IntentHandler)"+intentHandlerArray.getName()+
							".get(" + 
							intentHandlerCounter.getName() +
							")).invoke(new org.objectweb.proactive.extensions.sca.control." +
							"IntentJoinPoint(this, \"" + newMethod.getName() + "\", $sig, $args));\n" +
							"}\n"+
							"else{" +
							ICounterName+"= -1;\n" +
							//"//System.out.println("+ ICounterName+ ");" +
							"return ($r)super." +ctExtentedMethod.getName()+"($$);\n"+
							"}\n" +
					"}\n");

					generatedCtClass.addMethod(newMethod);

					generateListUtils(intentHandlerArray,generatedCtClass);

				}
				// Add constructors
				CtConstructor defaultConstructor = CtNewConstructor.defaultConstructor(generatedCtClass);
				generatedCtClass.addConstructor(defaultConstructor);

				generatedCtClass.stopPruning(true);
				generatedCtClass.writeFile("generated/");
				System.out.println("[JAVASSIST] generated class: " + generatedClassName);

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
				logger.error("Cannot generate subClass of [" + className + "] with javassist: " +
						e.getMessage());
				throw new ClassGenerationFailedException("Cannot generate subClass of [" + className +
						"] with javassist", e);
			}

		}
		return generatedClassName;
	}



	private void generateListUtils(CtField intentHandlerArray,CtClass generatedCtClass) throws CannotCompileException {
		String IArrayName = intentHandlerArray.getName(); 
		CtMethod add = CtNewMethod.make("public void addInto"+IArrayName+"(org.objectweb.proactive.extensions.sca.control.IntentHandler ith){" +
				IArrayName+".add(ith);\n" +
				"}"
				, generatedCtClass);	
		generatedCtClass.addMethod(add);
		CtMethod remove = CtNewMethod.make("public void removeFrom"+IArrayName+"(org.objectweb.proactive.extensions.sca.control.IntentHandler ith){" +
				IArrayName+".remove(ith);\n" +
				"}"
				, generatedCtClass);	
		generatedCtClass.addMethod(remove);
		CtMethod list = CtNewMethod.make("public java.util.List list"+IArrayName+"(){" +
				"return " + IArrayName+";" +
				"}"
				, generatedCtClass);	
		generatedCtClass.addMethod(list);

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
