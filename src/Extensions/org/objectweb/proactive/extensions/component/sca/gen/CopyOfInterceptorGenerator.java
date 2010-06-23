package org.objectweb.proactive.extensions.component.sca.gen;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.objectweb.proactive.core.component.control.AbstractPAController;
import org.objectweb.proactive.core.component.interception.InputInterceptor;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.util.ClassDataCache;
import org.objectweb.proactive.extensions.component.sca.exceptions.ClassGenerationFailedException;


public class CopyOfInterceptorGenerator extends AbstractClassGenerator {
    private static CopyOfInterceptorGenerator instance;

    public static CopyOfInterceptorGenerator instance() {
        if (instance == null) {
            return new CopyOfInterceptorGenerator();
        } else {
            return instance;
        }
    }

    /**
     * 
     */
    public String generateClass(String className) throws ClassGenerationFailedException {
        // TODO Auto-generated method stub
        String CName = Utils.getInterceptorClassName(className);
        Class<?> generatedClass = null;
        try {
            generatedClass = loadClass(CName);
        } catch (ClassNotFoundException cnfe) {
            CtClass generatedCtClass = pool.makeClass(CName);
            try {
                generatedCtClass.setSuperclass(pool.get(AbstractPAController.class.getName()));
                generatedCtClass.addInterface(pool.get(InputInterceptor.class.getName()));
                /* initialize fields */
                CtClass intentHanderlerInstanceClass = pool.get(className);
                CtField intentHanderlerInstance = new CtField(intentHanderlerInstanceClass,
                    "intentHanderlerInstance", generatedCtClass);
                generatedCtClass.addField(intentHanderlerInstance, "new " +
                    intentHanderlerInstanceClass.getName() + "()");
                /* make constructor */
                CtConstructor constructor = CtNewConstructor.make("public " + CName +
                    " (org.objectweb.fractal.api.Component owner){super(owner);}", generatedCtClass);
                CtConstructor constructorDefault = CtNewConstructor.defaultConstructor(generatedCtClass);
                /* make setControllerItfType methode */
                String setControllerStr = "protected void setControllerItfType() {\n" +
                    "try {\n" +
                    "setItfType(org.objectweb.proactive.core.component.type.PAGCMTypeFactoryImpl.instance().createFcItfType(\n" +
                    "\"" +
                    CName +
                    "\", org.objectweb.proactive.core.component.interception.InputInterceptor.class.getName(),\n" +
                    "org.objectweb.fractal.api.type.TypeFactory.SERVER, org.objectweb.fractal.api.type.TypeFactory.MANDATORY, org.objectweb.fractal.api.type.TypeFactory.SINGLE));\n" +
                    "} catch (org.objectweb.fractal.api.factory.InstantiationException e) {\n" +
                    "throw new org.objectweb.proactive.core.ProActiveRuntimeException(\"cannot create controller \" + this.getClass().getName());\n" +
                    "}\n" + "}";
                System.err.println(className);
                CtMethod setControllerItfType = CtNewMethod.make(setControllerStr, generatedCtClass);
                String afterMethodInvocationStr = "public void afterInputMethodInvocation(org.objectweb.proactive.core.mop.MethodCall methodCall) {\n"
                    + "intentHanderlerInstance.AfterInvoke();\n" + "}\n";
                String beforeMethodInvocationStr = "public void beforeInputMethodInvocation(org.objectweb.proactive.core.mop.MethodCall methodCall) {\n"
                    + "intentHanderlerInstance.BeforeInvoke();\n" + "}\n";
                CtMethod beforeMethodInvocation = CtNewMethod.make(beforeMethodInvocationStr,
                        generatedCtClass);
                CtMethod afterMethodInvocation = CtNewMethod.make(afterMethodInvocationStr, generatedCtClass);
                generatedCtClass.addConstructor(constructor);
                generatedCtClass.addConstructor(constructorDefault);
                generatedCtClass.addMethod(setControllerItfType);
                generatedCtClass.addMethod(beforeMethodInvocation);
                generatedCtClass.addMethod(afterMethodInvocation);
                //CtMethod afterInputMethodInvocation = CtNewMethod.make
                generatedCtClass.stopPruning(true);
                generatedCtClass.writeFile("generated/");
                System.out.println("[JAVASSIST] generated class: " + CName);
                // Generate and add to cache the generated class
                byte[] bytecode = generatedCtClass.toBytecode();
                ClassDataCache.instance().addClassData(CName, bytecode);
                if (logger.isDebugEnabled()) {
                    logger.debug("added " + CName + " to cache");
                    logger.debug("generated classes cache is: " + ClassDataCache.instance().toString());
                }
                generatedClass = Utils.defineClass(CName, bytecode);
                generatedCtClass.defrost(); // defrost the generated class                			
            } catch (NotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CannotCompileException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return CName;
    }

}
