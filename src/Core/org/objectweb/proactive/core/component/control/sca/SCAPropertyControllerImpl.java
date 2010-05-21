/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.control.sca;

import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.etsi.uri.gcm.util.GCM;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.AttributeController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.control.AbstractPAController;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactoryImpl;


public class SCAPropertyControllerImpl extends AbstractPAController implements SCAPropertyController {

    private AttributeController buildInAttributeController;
    /**
     * properties which have been injected into components
     */
    private Map<String, Object> values = new HashMap<String, Object>();
    /**
     * declared properties' types
     */
    private Map<String, Class<?>> types = new HashMap<String, Class<?>>();
    /**
     * name of all declared properties
     */
    private List<String> propertyNames = new ArrayList<String>();
    /**
     * all getter and setter methodes from user build propertyController
     */
    private Map<String, Method> ListMethodes = new HashMap<String, Method>();

    /**
     * initialize all private fields .
     * @param owner owner component
     * @throws NoSuchInterfaceException
     */
    public SCAPropertyControllerImpl(Component owner) {
        super(owner);
        /*try {
        	buildInAttributeController=(AttributeController)owner.getFcInterface(Constants.ATTRIBUTE_CONTROLLER);
        } catch (NoSuchInterfaceException e) {
        	e.printStackTrace();
        }
        propertyNames=getDeclaredPropertyNamesInList();
        for (String element : propertyNames) {
        	types.put(element, getDeclaredPropertyType(element));
        }*/
    }

    public void init() {
        try {
            buildInAttributeController = GCM.getAttributeController(owner);
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        }
        propertyNames = getDeclaredPropertyNamesInList();
        for (String element : propertyNames) {
            types.put(element, getDeclaredPropertyType(element));
        }
    }

    @Override
    protected void setControllerItfType() {
        try {
            setItfType(PAGCMTypeFactoryImpl.instance().createFcItfType(Constants.SCA_PROPERTY_CONTROLLER,
                    SCAPropertyController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                    TypeFactory.SINGLE));
        } catch (InstantiationException e) {
            throw new ProActiveRuntimeException("cannot create controller " + this.getClass().getName(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Implementation of the SCAPropertyController interface
    // -------------------------------------------------------------------------

    /**
     * Set the type of the specified property. is not proper implemented yet
     * 
     * @param type   the property type
     * @param value  the property value
     */
    public void setType(String name, Class<?> type) {
        types.put(name, type);
    }

    /**
     * convert a String to another string which the first case become capital
     * "cool" --> "Cool"
     * @param name
     * @return String which first case is capital letter
     */
    private String NameUp(String name) {
        char[] nameUpper = name.toCharArray();
        nameUpper[0] = Character.toUpperCase(nameUpper[0]);
        String nameUp = new String(nameUpper);
        //System.out.println("Debug Nameup : "+nameUp);
        return nameUp;
    }

    /**
     * convert a String to another string which the first case become lower-case letter
     * "Cool" --> "cool"
     * @param name
     * @return String with lower-case letter on first case
     */
    private String NameLow(String name) {
        char[] nameLower = name.toCharArray();
        nameLower[0] = Character.toLowerCase(nameLower[0]);
        String nameLow = new String(nameLower);
        //System.out.println("Debug nameLow: "+nameLow);
        return nameLow;
    }

    /**
     * Set the value of the specified property. If the property has already been
     * set, the old value is lost, and the new value is recorded.
     * 
     * @param name   the property name
     * @param value  the property value
     */
    public void setValue(String name, Object value) {
        String NameUp = NameUp(name);
        String setterName = "set" + NameUp;
        Class<?> typeAttribute = types.get(name);
        System.out.println("DEBUG setvalue " + setterName + " type: " + typeAttribute.getName());
        try {
            Object args[] = new Object[1];
            args[0] = value;
            Method setter = buildInAttributeController.getClass().getMethod(setterName, typeAttribute);//ListMethodes.get(setterName);
            System.out.println("DEBUGG SETTER NAME  " + setter.getName());
            try {
                setter.invoke(buildInAttributeController, args);
                values.put(name, value);
            } catch (IllegalArgumentException e) {
                System.err.println("problem on invoking arguments!! " + args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                System.err.println("problem on invoking object !!" +
                    buildInAttributeController.getClass().getName());
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Return the type of the specified property. Return <code>null</code> if
     * the property type has not been set.
     * 
     * @param name  the property name
     * @return      the property value
     */
    public Class<?> getType(String name) {
        return types.get(name);
    }

    /**
     * Return the value of the specified property. Return <code>null</code> if
     * the property value has not been set.
     * 
     * @param name  the property name
     * @return      the property value
     */
    public Object getValue(String name) {
        return values.get(name);
    }

    /**
     * Return <code>true</code> if the specified property has been set.
     * 
     * @param name  the property name
     * @return      <code>true</code> if the property has been set,
     *              <code>false</code> otherwise
     */
    public boolean containsPropertyName(String name) {
        return values.containsKey(name);
    }

    /**
     * Return the names of the properties whose values have been set by invoking
     * {@link #setValue(String, Object)}.
     */
    public String[] getPropertyNames() {
        Set<String> keys = values.keySet();
        String[] names = keys.toArray(new String[keys.size()]);
        return names;
    }

    /**
     * Return <code>true</code> if the specified property can be injected in the
     * content class.
     * 
     * @param name  the property name
     * @return      <code>true</code> if the property can be injected,
     *              <code>false</code> otherwise
     */
    public boolean containsDeclaredPropertyName(String name) {
        return propertyNames.contains(name);
    }

    /**
     * Return the names of the properties which can be injected in the content
     * class.
     */
    private List<String> getDeclaredPropertyNamesInList() {
        Method methods[] = buildInAttributeController.getClass().getMethods();
        List<String> namesList = new ArrayList<String>();
        for (int i = 0; i < methods.length; i++) {
            String tmp = methods[i].getName();
            if (tmp.startsWith("set")) {
                //System.out.println(tmp);
                //System.out.println(tmp.substring(3));
                ListMethodes.put(tmp, methods[i]);
                namesList.add(NameLow(tmp.substring(3)));
            }
            if (tmp.startsWith("get")) {
                ListMethodes.put(tmp, methods[i]);
            }
        }

        return namesList;
    }

    /**
     * Return the names of the properties which can be injected in the content
     * class.
     */
    public String[] getDeclaredPropertyNames() {
        String[] names = (String[]) propertyNames.toArray();
        for (int i = 0; i < names.length; i++) {
            System.out.println("DEBUG: " + names[i]);
        }
        return names;
    }

    /**
     * Return the type of the specified property, provided that this property
     * can be injected in the content class.
     * 
     * @param name  the property name
     * @return      the property type
     */
    public Class<?> getDeclaredPropertyType(String name) {
        if (containsDeclaredPropertyName(name)) {
            String setter = "set" + NameUp(name);
            Method med = ListMethodes.get(setter);
            return med.getParameterTypes()[0];
        }
        return null;
    }

    /**
     * Set the reference of the property controller which promotes the specified
     * property to the current property controller.
     * 
     * @param name      the promoter property name
     * @param promoter  the promoter component or
     *                  <code>null</code> to unregister the promoter
     * @throws IllegalPromoterException
     *      thrown when attempting to set a cycle between property promoters
     */
    /*
        public void setPromoter( String name, SCAPropertyController promoter )
        throws IllegalPromoterException {
            
            promoters.put(name,promoter);

            if( promoter != null ) {
                SCAPropertyController peer = promoter.getPromoter(name);
                if( peer == this ) {
                    String compname = _this_weaveableOptNC.getFcName();
                    throw new IllegalPromoterException(compname);
                }
            }
        }*/

    /**
     * Return the reference of the property controller which promotes the
     * specified property. Return <code>null</code> if the property is managed
     * locally by the current property controller.
     * 
     * @param name  the promoter property name
     * @return      the promoter component or <code>null</code>
     */
    /*public SCAPropertyController getPromoter( String name ) {
        return promoters.get(name);
    }*/

}
