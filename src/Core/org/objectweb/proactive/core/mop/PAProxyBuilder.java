package org.objectweb.proactive.core.mop;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.core.mop.lock.RemoteLocksManager;
import org.objectweb.proactive.core.mop.proxy.PAProxy;

import functionalTests.activeobject.creation.A;

public class PAProxyBuilder {

    /**
     * @param args
     * @throws NotFoundException 
     * @throws CannotCompileException 
     */
    public static void main(String[] args) throws NotFoundException, CannotCompileException {
      ClassPool pool = ClassPool.getDefault();
        
      
      String superClazzName =  A.class.getName();
      
        CtClass generatedCtClass = pool.makeClass("PAProxy_" + superClazzName);
        
        
        
        CtClass superCtClass = pool.get(superClazzName);
        
        if (superCtClass.isInterface()) {
            generatedCtClass.addInterface(superCtClass);
            generatedCtClass.setSuperclass(pool.get(Object.class.getName()));
        } else {
            generatedCtClass.setSuperclass(superCtClass);
        }
        
        if (!generatedCtClass.subtypeOf(pool.get(Serializable.class.getName()))) {
            generatedCtClass.addInterface(pool.get(Serializable.class.getName()));
        }
        
        generatedCtClass.addInterface(pool.get(PAProxy.class.getName()));
        generatedCtClass.addInterface(pool.get(RemoteLocksManager.class.getName()));
        generatedCtClass.addInterface(pool.get(InitActive.class.getName()));
        
        
        //constructor 
        
        
        
        
        
        java.util.Map<String, Method> temp = new HashMap<String, Method>();
        List<String> classesIndexer = new Vector<String>();
        
        temp = JavassistByteCodeStubBuilder.methodsIndexer(superCtClass,classesIndexer);
             // TODO Auto-generated method stub

        CtClass rLockManagerClazz = pool.get(RemoteLocksManager.class.getName());
        
        java.util.Map<String, Method> rLockManagerMethods = JavassistByteCodeStubBuilder.methodsIndexer(rLockManagerClazz,classesIndexer);
        
        Iterator<Entry<String, Method>> iterateLockManagerMethod = rLockManagerMethods.entrySet().iterator();
        
        CtClass hashtableClass = pool.get(Hashtable.class.getName());
        
        CtField locksField = new CtField(hashtableClass,
                "locks", generatedCtClass);
        
        generatedCtClass.addField(locksField);
        
        while (iterateLockManagerMethod.hasNext()) {
            Entry<String, Method> entry = iterateLockManagerMethod.next();
            Method m = entry.getValue();
            
            CtMethod ctMethod =  m.getCtMethod();
            CtClass returnType = ctMethod.getReturnType();
            String preWrap = "";
            if (returnType != CtClass.voidType) {
                if (!returnType.isPrimitive()) {
                    preWrap = "($r)";
                } else {
                    preWrap = "($w)";
                }
            
            }
            
            String body = " { Object o = ($r) ($w) this.locks.get($1). " + ctMethod.getName() + "($$); return ($r) o ;} ";
            
            CtMethod methodToGenerate = null;

            //                                    System.out
            //                                          .println("JavassistByteCodeStubBuilder.createReifiedMethods() body " + reifiedMethods[i].getName() + " = " + body);
            try {
                methodToGenerate = CtNewMethod.make(ctMethod.getReturnType(),
                        ctMethod.getName(), ctMethod
                                .getParameterTypes(), ctMethod.getExceptionTypes(), body
                                .toString(), generatedCtClass);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            generatedCtClass.addMethod(methodToGenerate);
            
        }
        
        
        
        generatedCtClass.debugWriteFile();
        
    }

}
