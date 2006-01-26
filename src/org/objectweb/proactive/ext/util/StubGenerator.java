/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.ext.util;

import java.io.File;
import java.io.FileOutputStream;

import org.objectweb.proactive.core.mop.ASMBytecodeStubBuilder;
import org.objectweb.proactive.core.mop.JavassistByteCodeStubBuilder;
import org.objectweb.proactive.core.mop.MOPClassLoader;
import org.objectweb.proactive.core.mop.Utils;


public class StubGenerator {

    /**
     * Turn a file name into a class name if necessary. Remove the ending .class and change all the '/' into '.'
     * @param name
     */
    protected static String processClassName(String name) {
        int i = name.indexOf(".class");
        String tmp = name;
        if (i < 0) {
            return name;
        }
        tmp = name.substring(0, i);

        String tmp2 = tmp.replace(File.separatorChar, '.');

        if (tmp2.indexOf('.') == 0) {
            return tmp2.substring(1);
        }
        return tmp2;
    }

    public static void printUsageAndExit() {
        System.err.println("usage: java " + StubGenerator.class.getName() +
            " <classes> ");
        System.exit(0);
    }

    public static void main(String[] args) {
        // This is the file into which we are about to write the bytecode for the stub
        String fileName = null;

        //the index of the first className
        int classIndex = 0;

        // Check number of arguments
        if (args.length <= 0) {
            printUsageAndExit();
        }

        String directoryName = "./";

        if (args[0].equals("-d")) {
            directoryName = args[1];
            classIndex = 2;
        }

        // If the directory name does not end with a file separator, add one
        if (!directoryName.endsWith(System.getProperty("file.separator"))) {
            directoryName = directoryName +
                System.getProperty("file.separator");
        }

        // Name of the class
        for (int i = classIndex; i < args.length; i++) {
            try {
                generateClass(args[i], directoryName);
            } catch (Throwable e) {
                System.err.println(e);
            }
        }
    }

    /**
     * @param arg
     * @param directoryName
     */
    protected static void generateClass(String arg, String directoryName) {
        String className = processClassName(arg);
        String fileName = null;

        String stubClassName;

        try {
            // Generates the bytecode for the class
            //ASM is now the default bytecode manipulator
            byte[] data;

            if (MOPClassLoader.BYTE_CODE_MANIPULATOR.equals("ASM")) {
                ASMBytecodeStubBuilder bsb = new ASMBytecodeStubBuilder(className);
                data = bsb.create();
                stubClassName = Utils.convertClassNameToStubClassName(className);
            } else if (MOPClassLoader.BYTE_CODE_MANIPULATOR.equals("javassist")) {
                data = JavassistByteCodeStubBuilder.create(className);
                stubClassName = Utils.convertClassNameToStubClassName(className);
            } else {
                // that shouldn't happen, unless someone manually sets the BYTE_CODE_MANIPULATOR static variable
                System.err.println(
                "byteCodeManipulator argument is optionnal. If specified, it can only be set to ASM.");
                System.err.println(
                "Any other setting will result in the use of javassist, the default bytecode manipulator framework");
                stubClassName = null;
                data = null;
            }

            char sep = System.getProperty("file.separator").toCharArray()[0];
            fileName = directoryName + stubClassName.replace('.', sep) +
                ".class";

            // And writes it to a file
            new File(fileName.substring(0, fileName.lastIndexOf(sep))).mkdirs();

            //	String fileName = directoryName + System.getProperty ("file.separator") + 
            File f = new File(fileName);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot find class " + className);
        } catch (Exception e) {
            System.err.println("Cannot write file " + fileName);
            System.err.println("Reason is " + e);
        }
    }
}
