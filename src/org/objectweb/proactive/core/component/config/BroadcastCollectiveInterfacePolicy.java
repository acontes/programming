package org.objectweb.proactive.core.component.config;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.plaf.SliderUI;

import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.group.ProxyForComponentInterfaceGroup;
import org.objectweb.proactive.core.mop.MethodCall;


public class BroadcastCollectiveInterfacePolicy extends AbstractCollectiveInterfacePolicy {
    public BroadcastCollectiveInterfacePolicy() {
    }

    public MethodCall distributeParameters(MethodCall mc, ProxyForComponentInterfaceGroup delegatee) {
        // default is to broadcast : do nothing here !
        return mc;
    }

    /**
     * client and server interfaces must have the same methods, except that
     * the client methods always returns a java.util.List, whereas
     * the server methods may return any type.
     * <br>
     * For java 1.5 this could be enhanced with generics
     *
     * @return true if interfaces are compatible for a multicast binding in broadcast mode
     * @see org.objectweb.proactive.core.component.config.CollectiveInterfacePolicy#checkCompatibility(org.objectweb.proactive.core.component.type.ProActiveInterfaceType, org.objectweb.proactive.core.component.type.ProActiveInterfaceType)
     */
    public boolean checkCompatibility(ProActiveInterfaceType clientSideItfType,
        ProActiveInterfaceType serverSideItfType) throws IllegalBindingException {

        try {
            Class clientSideItfClass;
            clientSideItfClass = Class.forName(clientSideItfType.getFcItfSignature());
            Class serverSideItfClass = Class.forName(serverSideItfType.getFcItfSignature());

            Method[] clientSideItfMethods = clientSideItfClass.getMethods();
            Method[] serverSideItfMethods = serverSideItfClass.getMethods();

            if (clientSideItfMethods.length != serverSideItfMethods.length) {
                return false;
            }

            // method names get automatically sorted, which makes comparisons easy
            SortedSet<String> clientSideItfsMethodsStrings = new TreeSet<String>();
            SortedSet<String> serverSideItfsMethodsStrings = new TreeSet<String>();

            for (int i = 0; i < clientSideItfMethods.length; i++) {
                Class<?> clientReturnType = clientSideItfMethods[i].getReturnType();

                if (!(List.class.isAssignableFrom(clientReturnType))
                        && !(Void.TYPE == clientReturnType)) {
                    throw new IllegalBindingException(
                            "methods of a multicast interface can only return void or a List (or subtype), which is "
                            + "not the case for the method " + clientSideItfMethods[i].getName()
                            + " of interface " + clientSideItfClass.getName());
                }

                if (!(clientReturnType == Void.TYPE)) {
                    Class clientSideTypeArgument = (Class) ((ParameterizedType) clientSideItfMethods[i]
                        .getGenericReturnType()).getActualTypeArguments()[0];

                    if (!(
                                clientSideTypeArgument.isAssignableFrom(
                                        serverSideItfMethods[i].getReturnType())
                            )) {
                        throw new IllegalBindingException(
                                "in multicast interface " + clientSideItfType.getFcItfName()
                                + ", method " + clientSideItfMethods[i].getName()
                                + " specifies the multicast interface is expecting "
                                + clientSideTypeArgument.getName()
                                + " as a return type for connected server interfaces, but in the connected server interface, the corresponding method "
                                + "returns " + serverSideItfMethods[i].getReturnType());
                    }
                }

                clientSideItfsMethodsStrings.add(
                        org.objectweb.proactive.core.component.Utils
                        .getMethodSignatureWithoutReturnTypeAndModifiers(clientSideItfMethods[i]));
                serverSideItfsMethodsStrings.add(
                        org.objectweb.proactive.core.component.Utils
                        .getMethodSignatureWithoutReturnTypeAndModifiers(serverSideItfMethods[i]));
            }

            return clientSideItfsMethodsStrings.equals(serverSideItfsMethodsStrings);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalBindingException(
                    "cannot find class corresponding to given signature " + e.getMessage());
        }
    }
}
