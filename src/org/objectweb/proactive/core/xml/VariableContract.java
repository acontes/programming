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
package org.objectweb.proactive.core.xml;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.descriptor.xml.VariablesHandler;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class provides a Variable Contract between the deployment descriptor and the application program.
 * Variables can be defined of different types, thus inforcing different requirements to the contract.
 * @author The ProActive Team
 */
public class VariableContract implements Serializable {
    static Logger logger = ProActiveLogger.getLogger(Loggers.DEPLOYMENT);
    public static VariableContract xmlproperties = null;
    public static final Lock lock = new Lock();
    private boolean closed;

    private class PropertiesDatas {
        public String value;
        public VariableContractType type;
        public String setFrom; //Descriptor, Program

        public String toString() {
            StringBuffer sb = new StringBuffer();

            sb.append(value).append(" type=").append(type).append(" setFrom=")
              .append(setFrom);
            return sb.toString();
        }
    }

    private HashMap list;

    /**
     * Constructor of the class. Creates a new instance.
     *
     */
    public VariableContract() {
        list = new HashMap();
        closed = false;
    }

    /**
     * Marks the contract as closed. No more variables can be defined or set.
     */
    public void close() {
        closed = true;
    }

    /**
     * Tells wether this contract is closed or not.
     * @return True if it is closed, false otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Method for setting variables value from the deploying application.
     * @param name        The name of the variable.
     * @param value       Value of the variable
     * @throws NullPointerException if the arguments are null.
     * @throws IllegalArgumentException if setting the value breaches the variable (contract) type
     */
    public void setVariableFromProgram(String name, String value,
        VariableContractType type) {
        setVariableFrom(name, value, type, "Program");
    }

    /**
     * Method for setting variables value from the deploying application. If the variable is not defined,
     * this method will also define it.
     * @param name  The name of the variable
     * @param value Value of the variable
     * @param from        Tells the origin of the setting. Ex: Program, Descriptor.
     * @throws NullPointerException if the arguments are null.
     * @throws IllegalArgumentException if setting the value breaches the variable (contract) type
     */
    private void setVariableFrom(String name, String value,
        VariableContractType type, String from) {
        if (logger.isDebugEnabled()) {
            logger.debug("Setting from " + from + ": " + type + " " + name +
                "=" + value);
        }

        if (closed) {
            throw new IllegalArgumentException(
                "Variable Contract is Closed. Variables can no longer be set");
        }

        checkGenericLogic(name, value, type);

        if ((value.length() > 0) && !type.hasSetAbility(from)) {
            throw new IllegalArgumentException("Variable " + name +
                " can not be set from " + from + " for type: " + type);
        }

        if ((value.length() <= 0) && !type.hasSetEmptyAbility(from)) {
            throw new IllegalArgumentException("Variable " + name +
                " can not be set empty from " + from + " for type: " + type);
        }

        if (list.containsKey(name)) {
            PropertiesDatas var = (PropertiesDatas) list.get(name);

            if (!type.hasPriority(var.setFrom, from)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipping, lower priority (" + from + " < " +
                        var.setFrom + ") for type: " + type);
                }
                return;
            }
        }

        if (type.equals(VariableContractType.JavaPropertyVariable)) {
            String prop_value;
            try {
                prop_value = System.getProperty(value);
                if (prop_value == null) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                    "Unable to get System Property: " + value);
            }

            value = prop_value;
        }

        unsafeAdd(name, value, type, from);
    }

    /**
     * Method for setting a group of variables from the program.
     * @see #setVariableFromProgram(String name, String value, VariableContractType type)
     * @throws NullPointerException if the arguments are null.
     * @throws IllegalArgumentException if setting the value breaches the variable (contract) type
     */
    public void setVariableFromProgram(HashMap map, VariableContractType type)
        throws NullPointerException {
        if ((map == null) || (type == null)) {
            throw new NullPointerException("Null arguments");
        }

        String name;
        java.util.Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            name = (String) it.next();
            setVariableFromProgram(name, (String) map.get(name), type);
        }
    }

    /**
     * Method for setting variables value from the deploying application.
     * @param name        The name of the variable.
     * @param value       Value of the variable
     * @throws NullPointerException if the arguments are null.
     * @throws IllegalArgumentException if setting the value breaches the variable (contract) type
     */
    public void setDescriptorVariable(String name, String value,
        VariableContractType type) {
        setVariableFrom(name, value, type, "Descriptor");
    }

    /**
     * Loads the variable contract from a Java Properties file format
     * @param file The file location.
     * @throws org.xml.sax.SAXException
     */
    public void load(String file) throws org.xml.sax.SAXException {
        Properties properties = new Properties();
        if (logger.isDebugEnabled()) {
            logger.debug("Loading propeties file:" + file);
        }

        // Open the file
        try {
            FileInputStream stream = new FileInputStream(file);
            properties.load(stream);
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Curret Working Directory: " +
                    System.getProperty("user.dir"));
            }

            throw new org.xml.sax.SAXException(
                "Tag property cannot open file : [" + file + "]");
        }

        String name;
        String value;
        Iterator it = properties.keySet().iterator();
        while (it.hasNext()) {
            name = (String) it.next();
            value = properties.getProperty(name);
            setDescriptorVariable(name, value,
                VariableContractType.DescriptorVariable);
        }
    }

    /**
     * Loads a file with Variable Contract tags into the this instance.
     * @param file
     */
    public void loadXML(String file) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading XML variable file:" + file);
        }
        VariablesHandler.createVariablesHandler(file, this);
    }

    /**
     * Returns the value of the variable name passed as parameter.
     * @param name The name of the variable.
     * @return The value of the variable.
     */
    public String getValue(String name) {
        if (list.containsKey(name)) {
            PropertiesDatas var = (PropertiesDatas) list.get(name);

            return var.value;
        }

        return null;
    }

    /**
     * TODO
     * Method to transform a variable to it's value.
     * @param         text        Text with properties inside to translates.
     * @return        string with properties swapped to their text value.
     */
    public String transform(String text) {
        //if ( text==null || text.equals("")) return text;
        do {
            // try to find the begining of property
            int begin = text.indexOf("${");
            if (begin < 0) {
                break;
            }
            begin += 2;
            // find the end of proprety name
            int end = text.indexOf("}");
            if (end < 0) {
                break;
            }
            String endText = text.substring(end + 1, text.length());

            // the name is empty ?
            if (begin == end) {
                if (begin > 2) {
                    text = text.substring(0, begin - 2) + endText;
                } else {
                    text = endText;
                }
                continue;
            }

            // build the name of the proterty
            String name = text.substring(begin, end);

            // if the property name does'n exist just return.
            if (list.containsKey(name) != true) {
                break;
            }
            PropertiesDatas datas = (PropertiesDatas) list.get(name);

            // check if there are some chars to keep at the begining of text.  
            if (begin > 2) {
                text = text.substring(0, begin - 2) + datas.value + endText;
            } else {
                text = datas.value + endText;
            }
        } while (true);

        return text;
    }

    /**
     * Checks common parameters.
     * @param name Must be not null or empty
     * @param value Must be not null
     * @param type Checks if variable is already defined with different type
     */
    private void checkGenericLogic(String name, String value,
        VariableContractType type) {

        /*
         * Generic Logical checks
         */
        if (name == null) {
            throw new NullPointerException("Variable Name is null.");
        }

        if (name.length() <= 0) {
            throw new IllegalArgumentException("Variable Name is empty.");
        }

        if (value == null) {
            throw new NullPointerException("Variable Value is null.");
        }

        if (list.containsKey(name) &&
                !((PropertiesDatas) list.get(name)).type.equals(type)) {
            throw new IllegalArgumentException("Variable " + name +
                " is already defined with type: " +
                ((PropertiesDatas) list.get(name)).type);
        }

        if (type == null) {
            throw new NullPointerException("Variable Type is null.");
        }
    }

    /**
     * Unsafe adding to the list of variables. No logical checking is done.
     * @param name Name of the variable
     * @param value Value of the variable
     * @param type The type of the variable
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    private void unsafeAdd(String name, String value,
        VariableContractType type, String setFrom)
        throws NullPointerException, IllegalArgumentException {
        if (name == null) {
            throw new NullPointerException("XML Variable Name is null.");
        }
        if (name.length() <= 0) {
            throw new IllegalArgumentException("XML Variable Name is empty.");
        }
        if (value == null) {
            throw new NullPointerException("XML Variable Value is null.");
        }

        PropertiesDatas data;
        if (list.containsKey(name)) {
            data = (PropertiesDatas) list.get(name);
            if (logger.isDebugEnabled()) {
                logger.debug("...Modifying variable registry");
            }
        } else {
            data = new PropertiesDatas();
            if (logger.isDebugEnabled()) {
                logger.debug("...Creating new registry for variable");
            }
        }

        data.type = type;
        data.value = value;
        data.setFrom = setFrom;
        list.put(name, data);
    }

    /**
     * Checks if there are empty values in the contract. All errors are printed through
     * the logger.
     * @return True if the contract has no empty values.
     */
    public boolean checkContract() {
        boolean retval = true;
        String name;
        java.util.Iterator it = list.keySet().iterator();
        while (it.hasNext()) {
            name = (String) it.next();
            PropertiesDatas data = (PropertiesDatas) list.get(name);

            if (data.type.equals(VariableContractType.DescriptorVariable) &&
                    (data.value.length() <= 0)) {
                logger.error("Contract breached. Variable " + name +
                    " has no value. The value must be set in the Descriptor.");
                retval = false;
            }
            if (data.type.equals(VariableContractType.ProgramVariable) &&
                    (data.value.length() <= 0)) {
                logger.error("Contract breached. Variable " + name +
                    " has no value. The value must be set in the Program.");
                retval = false;
            }
            if (data.type.equals(VariableContractType.ProgramDefaultVariable) &&
                    (data.value.length() <= 0)) {
                logger.error("Contract breached. Variable " + name +
                    " has no value. The value should be set in the Program.");
                retval = false;
            }
            if (data.type.equals(VariableContractType.DescriptorDefaultVariable) &&
                    (data.value.length() <= 0)) {
                logger.error("Contract breached. Variable " + name +
                    " has no value. The value should be set in the Descriptor.");
                retval = false;
            }
        }

        return retval;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        PropertiesDatas var;
        String name;
        java.util.Iterator it = list.keySet().iterator();
        while (it.hasNext()) {
            name = (String) it.next();
            var = (PropertiesDatas) list.get(name);

            sb.append(name).append("=").append(var).append("\n");
        }

        return sb.toString();
    }
/*
    public void setDescriptorVariableOLD(String name, String value,
        VariableContractType type) {
        if (logger.isDebugEnabled()) {
            logger.debug("Setting from descriptor: " + type + " " + name + "=" +
                value + ".");
        }

        checkGenericLogic(name, value, type);

        /* DescriptorVariable
         *  -Priority: XML
         *  -Set Ability: XML
         *  -Default: XML
     
        if (type.equals(VariableContractType.DescriptorVariable) &&
                (value.length() <= 0)) {
            throw new IllegalArgumentException("Variable " + name +
                " value must be specified for type: " + type);
        }

        /* ProgramVariable
         *  -Priority: Program
         *  -Set Ability: Program
         *  -Default: Program
       
        if (type.equals(VariableContractType.ProgramVariable)) {
            if (value.length() > 0) {
                throw new IllegalArgumentException("Variable " + name +
                    " can not be set from descriptor for type: " + type);
            }

            if (list.containsKey(name)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipping " + type + " " + name + "=" + value +
                        ". Program already set variable with value.");
                }
                return;
            }
        }

        /* DescriptorDefaultVariable
         *  -Priority: Program
         *  -Set Ability: Program, XML
         *  -Default: XML
      
        if (type.equals(VariableContractType.DescriptorDefaultVariable)) {
            if (value.length() < 0) {
                throw new IllegalArgumentException("Variable " + name +
                    " value must be specified for type: " + type);
            }

            //Priority is lost if Program set the variable
            if (list.containsKey(name)) {
                PropertiesDatas var = (PropertiesDatas) list.get(name);

                //skipe if program already set a value
                if (var.setFrom.equals("Program") && (var.value.length() > 0)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Skipping variable " + name + " type: " +
                            type +
                            ". Program already set variable with higher priority");
                    }
                    return;
                }
            }
        }

        /* ProgramDefaultVariable
         *  -Priority: XML
         *  -Set Ability: Program, XML
         *  -Default: Program
     
        if (type.equals(VariableContractType.ProgramDefaultVariable) &&
                (value.length() <= 0)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Variable " + name +
                    ", not setting to empty value");
            }
            return;
        }

        /* JavaPropertyVariable
         *  -Priority: JavaProperty
         *  -Set Ability: JavaProperty
         *  -Default: JavaProperty

        if (type.equals(VariableContractType.JavaPropertyVariable)) {
            String prop_value = null;

            if (value.length() <= 0) {
                throw new IllegalArgumentException("Variable " + name +
                    " value can not be empty for type " + type);
            }

            try {
                prop_value = System.getProperty(value);
                if (prop_value == null) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                    "Unable to get System Property: " + value);
            }

            value = prop_value;
        }

        //defineVariable(type, name);
        unsafeAdd(name, value, type, "Descriptor");
    }
    */
/*
    public void setVariableFromProgramOLD(String name, String value,
        VariableContractType type)
        throws NullPointerException, IllegalArgumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("Setting from program:    " + type + " " + name + "=" +
                value);
        }

        checkGenericLogic(name, value, type);

        // DescriptorVariable        *  -Priority: XML         *  -Set Ability: XML        *  -Default: XML         
        if (type.equals(VariableContractType.DescriptorVariable)) {
            if (value.length() > 0) {
                throw new IllegalArgumentException("Variable " + name +
                    " can not be set from program for type: " + type);
            }

            if (list.containsKey(name)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipping " + type + " " + name + "=" + value +
                        ". Descriptor already set variable with value.");
                }
                return;
            }
        }

        // ProgramVariable         *  -Priority: Program         *  -Set Ability: Program         *  -Default: Program         
        if (type.equals(VariableContractType.ProgramVariable) &&
                (value.length() <= 0)) {
            throw new IllegalArgumentException("Variable " + name +
                " value must be specified for type: " + type);
        }

        // DescriptorDefaultVariable         *  -Priority: Program         *  -Set Ability: Program, XML         *  -Default: XML         
        if (type.equals(VariableContractType.DescriptorDefaultVariable) &&
                (value.length() <= 0)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Variable " + name +
                    ", not setting to empty value");
            }
            return;
        }

        // ProgramDefaultVariable          *  -Priority: XML         *  -Set Ability: Program, XML         *  -Default: Program         
        if (type.equals(VariableContractType.ProgramDefaultVariable)) {
            if (value.length() < 0) {
                throw new IllegalArgumentException("Variable " + name +
                    " value must be specified for type: " + type);
            }

            if (list.containsKey(name)) {
                PropertiesDatas var = (PropertiesDatas) list.get(name);

                //skipe if descriptor already set a value
                if (var.setFrom.equals("Descriptor") &&
                        (var.value.length() > 0)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Skipping variable " + name + " type: " +
                            type +
                            ". Descriptor already set variable with higher priority");
                    }
                    return;
                }
            }
        }

        // JavaPropertyVariable         *  -Priority: JavaProperty         *  -Set Ability: JavaProperty         *  -Default: JavaProperty         
        if (type.equals(VariableContractType.JavaPropertyVariable)) {
            String prop_value = null;

            if (value.length() <= 0) {
                throw new IllegalArgumentException("Variable " + name +
                    " value can not be empty for type " + type);
            }

            try {
                prop_value = System.getProperty(value);
                if (prop_value == null) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                    "Unable to get System Property: " + value);
            }

            value = prop_value;
        }

        //defineVariable(type, name);
        unsafeAdd(name, value, type, "Program");
    }
*/
    /**
     * Class used for exclusive acces to global static variable:
     * org.objectweb.proactive.core.xml.XMLProperties.xmlproperties
     *
     * @author The ProActive Team
     */
    static public class Lock {
        private boolean locked;

        private Lock() {
            locked = false;
        }

        /**
         * Call this method to release the lock on the XMLProperty variable.
         * This method will also clean the variable contents.
         */
        public synchronized void release() {
            locked = false;
            notify();
        }

        /**
         * Call this method to get the lock on the XMLProperty object instance.
         */
        public synchronized void aquire() {
            while (locked) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }

            locked = true;
        }
    }
}
