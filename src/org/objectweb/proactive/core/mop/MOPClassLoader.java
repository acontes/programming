/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.mop;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Hashtable;


public class MOPClassLoader extends URLClassLoader {
    static Logger logger = Logger.getLogger(MOPClassLoader.class.getName());

    // retreives the optionnal byteCodeManipulator JVM arg
    // ASM is used by default
    public static String BYTE_CODE_MANIPULATOR = ((System.getProperty(
            "byteCodeManipulator") != null)
        ? ((System.getProperty("byteCodeManipulator").equals("BCEL")) ? "BCEL"
                                                                      : "ASM")
        : "ASM");
    protected static Hashtable classDataCache = new Hashtable();
    protected static MOPClassLoader mopCl = null;

    //    public static synchronized MOPClassLoader getMOPClassLoader(
    //        ClassLoader parent, URL[] urls) {
    //        if (MOPClassLoader.mopCl == null) {
    //            MOPClassLoader.mopCl = new MOPClassLoader(parent, urls);
    //        }
    //        return MOPClassLoader.mopCl;
    //    }

    /**
     * Return the unique MOPClassLoader for the current JVM
     * Create it if it does not exist
     */
    public static synchronized MOPClassLoader getMOPClassLoader() {
        if (MOPClassLoader.mopCl == null) {
            MOPClassLoader.mopCl = MOPClassLoader.createMOPClassLoader();
        }
        return MOPClassLoader.mopCl;
    }

  public MOPClassLoader(){
  	super(new URL[] {});
  }
    
    /**
     * Get the bytecode of a stub given its name. If the stub can not be found
     * the cache, the MOPClassLoader tries to generate it.
     * @param classname The name of the stub class
     * @return An array representing the bytecode of the stub, null if the
     *  stub could not be found or created
     */
    public byte[] getClassData(String classname) {
        byte[] cb = null;
        cb = (byte[]) classDataCache.get(classname);
        if (cb == null) {
            logger.info(
                "MOPClassLoader: class not found, trying to generate it");
            try {
                this.loadClass(classname);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            cb = (byte[]) classDataCache.get(classname);
        }

        //return (byte[]) classDataCache.get(classname);
        return cb;
    }

    private MOPClassLoader(ClassLoader parent, URL[] urls) {
        super(urls, parent);
    }

    public void launchMain(String[] args) throws Throwable {
        try {
            // Looks up the class that contains main
            Class cl = Class.forName(args[0], true, this);

            // Looks up method main
            Class[] argTypes = { args.getClass() };
            Method mainMethod = cl.getMethod("main", argTypes);

            // And calls it
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);

            Object[] mainArgs = { newArgs };
            mainMethod.invoke(null, mainArgs);
        } catch (ClassNotFoundException e) {
            logger.error("Launcher: cannot find class " + args[0]);
        } catch (NoSuchMethodException e) {
            logger.error("Launcher: class " + args[0] +
                " does not contain have method void 'public void main (String[])'");
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return;
    }

    protected static MOPClassLoader createMOPClassLoader() {
        // Gets the current classloader
        ClassLoader currentClassLoader = null;

        try {
            Class c = Class.forName(
                    "org.objectweb.proactive.core.mop.MOPClassLoader");
            currentClassLoader = c.getClassLoader();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        URL[] urls = null;

        // Checks if the current classloader is actually an instance of
        // java.net.URLClassLoader, or of one of its subclasses.
        if (currentClassLoader instanceof java.net.URLClassLoader) {
            // Retrieves the set of URLs from the current classloader
            urls = ((URLClassLoader) currentClassLoader).getURLs();
        } else {
            urls = new URL[0];
            //     System.out.println("Current classloader is of type " +
            //      currentClassLoader.getClass().getName() +
            //    ", which is not compatible with URLClassLoader. Cannot install MOPClassLoader");
            //   return null;
        }

        // Creates a new MOPClassLoader
        return new MOPClassLoader(currentClassLoader, urls);
    }

    protected Class findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, null, false);
    }

    public Class loadClass(String name, ClassLoader cl)
        throws ClassNotFoundException {
        return this.loadClass(name, cl, false);
    }

    protected synchronized Class loadClass(String name, ClassLoader cl,
        boolean resolve) throws ClassNotFoundException {
        if (this.getParent() != null) {
            try {
                return this.getParent().loadClass(name);
            } catch (ClassNotFoundException e) {
                // proceeding
            }
        }

        try {
            if (cl != null) {
                return cl.loadClass(name);
            } else {
                return Class.forName(name);
            }
        } catch (ClassNotFoundException e) {
            // Test if the name of the class is actually a request for
            // a stub class to be created
            if (Utils.isStubClassName(name)) {
                logger.info("Generating class : " + name);

                String classname = Utils.convertStubClassNameToClassName(name);

                //ASM is now the default bytecode manipulator
                byte[] data = null;
                if (BYTE_CODE_MANIPULATOR.equals("ASM")) {
                    ASMBytecodeStubBuilder bsb = new ASMBytecodeStubBuilder(classname);
                    long start_time = System.currentTimeMillis();
                    data = bsb.create();
                    MOPClassLoader.classDataCache.put(name, data);
                } else if (BYTE_CODE_MANIPULATOR.equals("BCEL")) {
                    BytecodeStubBuilder bsb = new BytecodeStubBuilder(classname);
                    long start_time = System.currentTimeMillis();
                    data = bsb.create();
                    MOPClassLoader.classDataCache.put(name, data);
                } else {
                    // that shouldn't happen, unless someone manually sets the BYTE_CODE_MANIPULATOR static variable
                    logger.error(
                        "byteCodeManipulator argument is optionnal. If specified, it can only be set to BCEL.");
                    logger.error(
                        "Any other setting will result in the use of ASM, the default bytecode manipulator framework");
                }

                // We use introspection to invoke the defineClass method to avoid the normal 
                // class Access checking. This method is supposed to be protected which means 
                // we should not be accessing it but the access policy file allows us to access it freely.
                try {
                    Class clc = Class.forName("java.lang.ClassLoader");
                    Class[] argumentTypes = new Class[5];
                    argumentTypes[0] = name.getClass();
                    argumentTypes[1] = data.getClass();
                    argumentTypes[2] = Integer.TYPE;
                    argumentTypes[3] = Integer.TYPE;
                    argumentTypes[4] = Class.forName(
                            "java.security.ProtectionDomain");

                    Method m = clc.getDeclaredMethod("defineClass",
                            argumentTypes);
                    m.setAccessible(true);

                    Object[] effectiveArguments = new Object[5];
                    effectiveArguments[0] = name;
                    effectiveArguments[1] = data;
                    effectiveArguments[2] = new Integer(0);
                    effectiveArguments[3] = new Integer(data.length);
                    effectiveArguments[4] = this.getClass().getProtectionDomain();

                    //   System.out.println("");
                    if (this.getParent() == null) {
                        return (Class) m.invoke(Thread.currentThread()
                                                      .getContextClassLoader(),
                            effectiveArguments);
                    } else {
                        return (Class) m.invoke(this.getParent(),
                            effectiveArguments);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new ClassNotFoundException(ex.getMessage());
                }
            } else {
                System.out.println("Cannot generate class " + name);
                throw e;
            }
        }
    }
}


//=======
//	
//	static Logger logger = Logger.getLogger(MOPClassLoader.class.getName());
//	
//	// retreives the optionnal byteCodeManipulator JVM arg
//	// ASM is used by default
//	public static String BYTE_CODE_MANIPULATOR =
//		((System.getProperty("byteCodeManipulator") != null)
//			? ((System.getProperty("byteCodeManipulator").equals("BCEL")) ? "BCEL" : "ASM")
//			: "ASM");
//
//	protected static Hashtable classDataCache = new Hashtable();
//
//	public static byte[] getClassData(String classname) {
//		return (byte[]) classDataCache.get(classname);
//	}
//
//	public MOPClassLoader(ClassLoader parent, URL[] urls) {
//		super(urls, parent);
//	}
//
//	public void launchMain(String[] args) throws Throwable {
//		try {
//			// Looks up the class that contains main
//			Class cl = Class.forName(args[0], true, this);
//
//			// Looks up method main
//			Class[] argTypes = { args.getClass()};
//			Method mainMethod = cl.getMethod("main", argTypes);
//
//			// And calls it
//			String[] newArgs = new String[args.length - 1];
//			System.arraycopy(args, 1, newArgs, 0, args.length - 1);
//
//			Object[] mainArgs = { newArgs };
//			mainMethod.invoke(null, mainArgs);
//		} catch (ClassNotFoundException e) {
//			logger.error("Launcher: cannot find class " + args[0]);
//		} catch (NoSuchMethodException e) {
//			logger.error(
//				"Launcher: class " + args[0] + " does not contain have method void 'public void main (String[])'");
//		} catch (InvocationTargetException e) {
//			throw e.getTargetException();
//		}
//
//		return;
//	}
//
//	public static MOPClassLoader createMOPClassLoader() {
//		// Gets the current classloader
//		ClassLoader currentClassLoader = null;
//
//		try {
//			Class c = Class.forName("org.objectweb.proactive.core.mop.MOPClassLoader");
//			currentClassLoader = c.getClassLoader();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		// Checks if the current classloader is actually an instance of
//		// java.net.URLClassLoader, or of one of its subclasses.
//		if (currentClassLoader instanceof java.net.URLClassLoader) {
//			//      System.out.println ("Current classloader is of type "+currentClassLoader.getClass().getName()+", compatible with URLClassLoader");
//		} else {
//			logger.error(
//				"Current classloader is of type "
//					+ currentClassLoader.getClass().getName()
//					+ ", which is not compatible with URLClassLoader. Cannot install MOPClassLoader");
//
//			return null;
//		}
//
//		// Retrieves the set of URLs from the current classloader
//		URL[] urls = ((URLClassLoader) currentClassLoader).getURLs();
//
//		// Creates a new MOPClassLoader
//		return new MOPClassLoader(currentClassLoader, urls);
//	}
//
//	protected Class findClass(String name) throws ClassNotFoundException {
//		return super.findClass(name);
//	}
//
//	public Class loadClass(String name) throws ClassNotFoundException {
//		return this.loadClass(name, null, false);
//	}
//
//
//	public Class loadClass(String name, ClassLoader cl)  throws ClassNotFoundException {		
//		return this.loadClass(name, cl, false);
//	}
//
//	protected synchronized Class loadClass(String name, ClassLoader cl, boolean resolve) throws ClassNotFoundException {
//		if (this.getParent() != null) {
//			try {
//				return this.getParent().loadClass(name);
//			} catch (ClassNotFoundException e) {
//				// proceeding
//			}
//		}
//
//		try {
//			return cl.loadClass(name);
//		} catch (ClassNotFoundException e) {
//			// Test if the name of the class is actually a request for
//			// a stub class to be created
//			if (Utils.isStubClassName(name)) {
//				logger.info("Generating class: " + name);
//
//				String classname = Utils.convertStubClassNameToClassName(name);
//				//ASM is now the default bytecode manipulator
//				byte[] data = null;
//				if (BYTE_CODE_MANIPULATOR.equals("ASM")) {
//					ASMBytecodeStubBuilder bsb = new ASMBytecodeStubBuilder(classname);
//					long start_time = System.currentTimeMillis();
//					data = bsb.create();
//					MOPClassLoader.classDataCache.put(name, data);
//				} else if (BYTE_CODE_MANIPULATOR.equals("BCEL")) {
//					BytecodeStubBuilder bsb = new BytecodeStubBuilder(classname);
//					long start_time = System.currentTimeMillis();
//					data = bsb.create();
//					MOPClassLoader.classDataCache.put(name, data);
//				} else {
//					// that shouldn't happen, unless someone manually sets the BYTE_CODE_MANIPULATOR static variable
//					logger.error(
//						"byteCodeManipulator argument is optionnal. If specified, it can only be set to BCEL.");
//					logger.error(
//						"Any other setting will result in the use of ASM, the default bytecode manipulator framework");
//				}
//
//				// System.out.println ("Classfile created with length "+data.length);
//				// Now, try to define the class
//				// We use the method defineClass, as redefined in class SecureClassLoader,
//				// so that we can specify a SourceCode object
//				//                    Class c = this.defineClass(name, data, 0, data.length, this.getClass().getProtectionDomain().getCodeSource());
//				//   this.getParent().findClass("toto");
//				//		    Class c = this.getParent().defineClass(name, data, 0, data.length, this.getClass().getProtectionDomain());
//				// The following code invokes defineClass on the parent classloader by Reflection
//				try {
//					Class clc = Class.forName("java.lang.ClassLoader");
//					Class[] argumentTypes = new Class[5];
//					argumentTypes[0] = name.getClass();
//					argumentTypes[1] = data.getClass();
//					argumentTypes[2] = Integer.TYPE;
//					argumentTypes[3] = Integer.TYPE;
//					argumentTypes[4] = Class.forName("java.security.ProtectionDomain");
//
//					Method m = clc.getDeclaredMethod("defineClass", argumentTypes);
//					m.setAccessible(true);
//
//					Object[] effectiveArguments = new Object[5];
//					effectiveArguments[0] = name;
//					effectiveArguments[1] = data;
//					effectiveArguments[2] = new Integer(0);
//					effectiveArguments[3] = new Integer(data.length);
//					effectiveArguments[4] = this.getClass().getProtectionDomain();
//
//					return (Class) m.invoke(this.getParent(), effectiveArguments);
//				} catch (Exception ex) {
//					throw new ClassNotFoundException(ex.getMessage());
//				}
//			} else {
//				logger.error("Cannot generate class " + name);
//				throw e;
//			}
//		}
//	}
//}>>>>>>> 1.9
