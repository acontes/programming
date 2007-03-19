/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
package org.objectweb.proactive.core.classloader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class defines a class loader that can fetch classes from other referenced ProActive runtimes.
 * It is able to look for classes in the classpath and, according to the classloader delegation model, and because
 * it is the system classloader, it will load *all* application classes except the system classes.
 * When asked to load a class, this classloader successively tries to :
 * 1. check if the class is a system class, in that case delegates to the parent classloader (primordial class loader)
 * 2. delegate the loading to the super class URLClassLoader, which looks into the classpath.
 * 3. delegate the search of the class data to a ProActiveClassLoaderHelper, then defines the class from
 * the retreived data (bytecode) (if the local ProActiveRuntime has been created)
 * The ProActiveClassLoaderHelper looks for the given class in other runtimes.
 *
 * @author Matthieu Morel
 *
 */
public class ProActiveClassLoader extends URLClassLoader {
    boolean runtimeReady = false;
    private Object helper;
    private Method helper_getClassData;
    private final static Logger CLASSLOADER_LOGGER = ProActiveLogger.getLogger(Loggers.CLASSLOADING);

    public ProActiveClassLoader() {
        super(pathToURLs(System.getProperty("java.class.path")));
    }

    /*
     * @see ClassLoader#ClassLoader(java.lang.ClassLoader)
     * @see ClassLoader#getSystemClassLoader()
     */
    public ProActiveClassLoader(ClassLoader parent) {
        super(pathToURLs(System.getProperty("java.class.path")), parent);
        try {
            // use a helper class so that the current class does not include any other proactive or application type 
            Class proActiveClassLoaderHelper = loadClass(
                    "org.objectweb.proactive.core.classloader.ProActiveClassLoaderHelper");
            helper = proActiveClassLoaderHelper.newInstance();
            helper_getClassData = proActiveClassLoaderHelper.getDeclaredMethod("getClassData",
                    new Class[] { String.class });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Looks for the given class in parents, classpath, and if not found delegates
     * the search to a ProActiveClassLoaderHelper
     * @see ClassLoader#findClass(java.lang.String)
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class c = null;
        try {
            // 1. look in parents and classpath
            c = super.findClass(name);
        } catch (ClassNotFoundException e) {
            if (runtimeReady) {
                byte[] class_data = null;
                try {
                    // 2. search for class data using helper
                    class_data = (byte[]) helper_getClassData.invoke(helper,
                            new Object[] { name });
                    if (class_data != null) {
                        c = defineClass(name, class_data, 0, class_data.length,
                                getClass().getProtectionDomain());
                    }
                } catch (Exception e1) {
                    throw new ClassNotFoundException(name, e1);
                }
            } else {
                throw e;
            }
        }
        if (c != null) {
            return c;
        } else {
            throw new ClassNotFoundException(name);
        }
    }

    /*
     * see ClassLoader#loadClass(java.lang.String)
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class c = null;
        if ((c = findLoadedClass(name)) != null) {
            return c;
        }
        if (name.startsWith("java.") || name.startsWith("javax.") ||
                name.startsWith("sun.") || name.startsWith("com.sun.") ||
                name.startsWith("org.xml.sax") || name.startsWith("org.omg") ||
                name.startsWith("org.ietf.jgss") ||
                name.startsWith("org.w3c.dom")) {
            return getParent().loadClass(name);
        }
        if (name.equals("org.objectweb.proactive.core.ssh.http.Handler")) {
            // class does not exist
            throw new ClassNotFoundException(name);
        }

        // FIXME temporary walkaround
        if (name.endsWith("_Skel")) {
            // do not attempt to download any 1.2- rmi skeleton
            throw new ClassNotFoundException(name);
        }
        c = findClass(name);
        if (name.equals(
                    "org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl")) {
            runtimeReady = true;
        }
        if (c != null) {
            // System.out.println("ProActiveClassloader loaded class : " + name);
        } else {
            throw new ClassNotFoundException(name);
        }
        return c;
    }

    /**
     * Transforms the string classpath to and URL array based classpath.
     *
     * The classpath string must be separated with the filesystem path separator.
     *
     * @param _classpath
     *          a classpath string
     * @return URL[] array of wellformed URL's
     * @throws MalformedURLException
     *           if a malformed URL has occurred in the classpath string.
     */
    public static URL[] pathToURLs(String _classpath) {
        StringTokenizer tok = new StringTokenizer(_classpath, File.pathSeparator);
        ArrayList<String> pathList = new ArrayList<String>();

        while (tok.hasMoreTokens()) {
            pathList.add(tok.nextToken());
        }

        URL[] urlArray = new URL[pathList.size()];

        int count = 0;
        for (int i = 0; i < pathList.size(); i++) {
            try {
                urlArray[i] = (new File((String) pathList.get(i))).toURI()
                               .toURL();
                count++;
            } catch (MalformedURLException e) {
                CLASSLOADER_LOGGER.info("MalformedURLException occured for " +
                    urlArray[i].toString() +
                    " during the ProActiveClassLoader creation");
            }
        }

        if (count != pathList.size()) {
            // A MalformedURLException occured
            URL[] tmpUrlArray = new URL[count];
            System.arraycopy(urlArray, 0, tmpUrlArray, 0, count);
        }

        return urlArray;
    }
}
