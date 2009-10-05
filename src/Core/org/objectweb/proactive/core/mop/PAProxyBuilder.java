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
import javassist.Modifier;
import javassist.NotFoundException;

import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.core.mop.lock.AbstractRemoteLocksManager;
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
      
        CtClass generatedCtClass = pool.makeClass(superClazzName+"_PAProxy");
        
        
        
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
        
        // extends base class
        
        java.util.Map<String, Method> temp = new HashMap<String, Method>();
        List<String> classesIndexer = new Vector<String>();
        temp = JavassistByteCodeStubBuilder.methodsIndexer(superCtClass,classesIndexer);
       
        
        
        CtField proxyField = new CtField(superCtClass,
                "proxiedModel", generatedCtClass);
        
        generatedCtClass.addField(proxyField);
        
        java.util.Map<String, Method> filtered = new HashMap<String, Method>();
        
        Iterator<String> is = temp.keySet().iterator();
        
        while (is.hasNext()) {
        	
        	String key = is.next();
        	Method m = temp.get(key);
        	  CtMethod ctMethod =  m.getCtMethod();
        	  if (!Modifier.isPrivate(ctMethod.getModifiers())) {
        		  filtered.put(key,m);
        	  }
        }
        
        temp  = filtered;
        while (is.hasNext()) {
        	Method m = temp.get(is.next());
        	   CtMethod ctMethod =  m.getCtMethod();
               CtClass returnType = ctMethod.getReturnType(); 
               String body = "";
               if (returnType != CtClass.voidType) {
                 body = " { return ($r) this.proxiedModel." + ctMethod.getName() + "($$); }"; 
               } else {
                	 body = " {this.proxiedModel." + ctMethod.getName() + "($$); }";
                 }
               CtMethod methodToGenerate = null;
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
        
        
        // RemoteLockManager 
        
        
       
        temp = JavassistByteCodeStubBuilder.methodsIndexer(superCtClass,classesIndexer);
        

        CtClass rLockManagerClazz = pool.get(AbstractRemoteLocksManager.class.getName());
        
        java.util.Map<String, Method> rLockManagerMethods = JavassistByteCodeStubBuilder.methodsIndexer(rLockManagerClazz,classesIndexer);
        
        Iterator<Entry<String, Method>> iterateLockManagerMethod = rLockManagerMethods.entrySet().iterator();
        
        CtClass hashtableClass = pool.get(Hashtable.class.getName());
        
        CtField locksField = new CtField(hashtableClass,
                "locks", generatedCtClass);
        
        generatedCtClass.addField(locksField);
        
        CtMethod[] rlmDeclaredMethods = rLockManagerClazz.getDeclaredMethods();
        
        for (CtMethod ctMethod2 : rlmDeclaredMethods) {
			
		
        
//        while (iterateLockManagerMethod.hasNext()) {
//            Entry<String, Method> entry = iterateLockManagerMethod.next();
//            Method m = entry.getValue();
//            
//            CtMethod ctMethod =  m.getCtMethod();
//            CtClass returnType = ctMethod.getReturnType();
//            String preWrap = "";
//            if (returnType != CtClass.voidType) {
//                if (!returnType.isPrimitive()) {
//                    preWrap = "($r)";
//                } else {
//                    preWrap = "($w)";
//                }
//            
//            }
//            
//            String body = " { Object o = ($r) ($w) this.locks.get(($w)$1). " + ctMethod.getName() + "($$); return ($r) o ;} ";
//            
            CtMethod methodToGenerate = null;

            //                                    System.out
            //                                          .println("JavassistByteCodeStubBuilder.createReifiedMethods() body " + reifiedMethods[i].getName() + " = " + body);
            try {
                methodToGenerate = CtNewMethod.copy(ctMethod2, generatedCtClass, null);
//                (ctMethod.getReturnType(),
//                        ctMethod.getName(), ctMethod
//                                .getParameterTypes(), ctMethod.getExceptionTypes(), body
//                                .toString(), generatedCtClass);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            generatedCtClass.addMethod(methodToGenerate);
            
        }
        
        
        
        generatedCtClass.debugWriteFile();
        
    }

}
