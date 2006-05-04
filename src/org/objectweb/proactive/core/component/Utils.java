package org.objectweb.proactive.core.component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;

public class Utils {

    // returns null if clientItfName does not begin with the name of a
    // collection interface
    // returns the name of the collection interface otherwise
    public static String pertainsToACollectionInterface(String clientItfName, Component owner) {
    	
    	
        InterfaceType[] itfTypes = (((ComponentType)owner.getFcType()).getFcInterfaceTypes());
        for (int i = 0; i < itfTypes.length; i++) {
            if (itfTypes[i].isFcCollectionItf()) {
                if (clientItfName.startsWith(itfTypes[i].getFcItfName())) {
                    return itfTypes[i].getFcItfName();
                }
            }
        }
        return null;
    }
    
    public static InterfaceType getItfType(String itfName, Component owner) {
    	InterfaceType[] itfTypes = (((ComponentType)owner.getFcType()).getFcInterfaceTypes());
        for (int i = 0; i < itfTypes.length; i++) {
            if (itfTypes[i].isFcCollectionItf()) {
                if (itfName.startsWith(itfTypes[i].getFcItfName()) && !itfName.equals(itfTypes[i].getFcItfName())) {
                    return itfTypes[i];
                }
            } else {
            	if (itfName.equals(itfTypes[i].getFcItfName())) {
            		return itfTypes[i];
            	}
            }
        }
        return null;
    }

    public static boolean existsWithSingleCardinality(String itfName, Component owner) {
        Iterator it = Arrays.asList(owner.getFcInterfaces()).iterator();
        while (it.hasNext()) {
            ProActiveInterfaceType itfType = ((ProActiveInterfaceType) ((Interface)it.next()).getFcItfType()); 
            if (itfType.getFcItfName().equals(itfName) && itfType.isFcSingleItf()) {
                return true;
            }
        }
        return false;
    
    }

    public static boolean hasMulticastCardinality(String itfName, Component owner) {
        
        InterfaceType[] itfTypes = ((ComponentType) owner.getFcType()).getFcInterfaceTypes();

        for (InterfaceType type : itfTypes) {

            if (type.getFcItfName().equals(itfName)
                    && ((ProActiveInterfaceType) type).isFcMulticastItf()) {
                    return true;
            }
        }
        return false;
    }
    
    
    public static String getMethodSignatureWithoutReturnTypeAndModifiers(Method m) {
        String result = m.toString();
        result = result.substring(result.indexOf(m.getName()));
        return result;
    }

}
