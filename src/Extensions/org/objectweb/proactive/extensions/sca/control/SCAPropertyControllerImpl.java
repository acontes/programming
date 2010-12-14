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
package org.objectweb.proactive.extensions.sca.control;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.control.AbstractPAController;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactoryImpl;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.sca.Constants;
import org.objectweb.proactive.extensions.sca.exceptions.IncompatiblePropertyTypeException;
import org.objectweb.proactive.extensions.sca.exceptions.NoSuchPropertyException;
import org.osoa.sca.annotations.Property;


/**
 * Implementation of the {@link SCAPropertyController} interface. 
 *
 * @author The ProActive Team
 * @see SCAPropertyController
 */
public class SCAPropertyControllerImpl extends AbstractPAController implements SCAPropertyController {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_CONTROLLERS);

    /** A mapping of known wrapper classes. */
    private static final Map<Class<?>, Class<?>> wrapperClasses;

    static {
        wrapperClasses = new HashMap<Class<?>, Class<?>>();
        wrapperClasses.put(boolean.class, Boolean.class);
        wrapperClasses.put(byte.class, Byte.class);
        wrapperClasses.put(char.class, Character.class);
        wrapperClasses.put(double.class, Double.class);
        wrapperClasses.put(float.class, Float.class);
        wrapperClasses.put(int.class, Integer.class);
        wrapperClasses.put(long.class, Long.class);
        wrapperClasses.put(short.class, Short.class);
    }

    /** Boolean to indicate if private members have been initialized. */
    private boolean init;

    /** Name of all declared properties. */
    private List<String> declaredPropertyNames;

    /** Type of all declared properties. */
    private Map<String, Class<?>> declaredPropertyTypes;

    /** Getter and setter methods for all declared properties. */
    private Map<String, Method> propertyMethods;

    /** Name of all initialized properties. */
    private List<String> initilizedProperties;

    /** Current type of properties. */
    private Map<String, Class<?>> propertyTypes;

    /**
     * Default constructor.
     *
     * @param owner Owner component.
     */
    public SCAPropertyControllerImpl(Component owner) {
        super(owner);
        init = false;
        declaredPropertyNames = new ArrayList<String>();
        declaredPropertyTypes = new HashMap<String, Class<?>>();
        propertyMethods = new HashMap<String, Method>();
        initilizedProperties = new ArrayList<String>();
        propertyTypes = new HashMap<String, Class<?>>();
    }

    @Override
    protected void setControllerItfType() {
        try {
            setItfType(PAGCMTypeFactoryImpl.instance().createFcItfType(Constants.SCA_PROPERTY_CONTROLLER,
                    SCAPropertyController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                    TypeFactory.SINGLE));
        } catch (InstantiationException ie) {
            throw new ProActiveRuntimeException("cannot create controller " + this.getClass().getName(), ie);
        }
    }

    /*
     * Initializes private members. Done only one time.
     */
    private void init() {
        if (!init) {
            try {
                Class<?> baseClass = owner.getReferenceOnBaseObject().getClass();
                Class<?> currentClass = baseClass;
                while (!currentClass.equals(Object.class)) {
                    Field[] fields = currentClass.getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        if (fields[i].isAnnotationPresent(Property.class)) {
                            declaredPropertyNames.add(fields[i].getName());
                            Class<?> type = fields[i].getType();
                            if (type.isPrimitive()) {
                                type = wrapperClasses.get(type);
                            }
                            declaredPropertyTypes.put(fields[i].getName(), type);
                            String getterName = getGetterName(fields[i].getName());
                            propertyMethods.put(getterName, baseClass
                                    .getMethod(getterName, new Class<?>[] {}));
                            String setterName = getSetterName(fields[i].getName());
                            propertyMethods.put(setterName, baseClass.getMethod(setterName,
                                    new Class<?>[] { fields[i].getType() }));
                        }
                    }
                    currentClass = currentClass.getSuperclass();
                }
                init = true;
            } catch (SecurityException se) {
                logger.error("Cannot initialize SCAPropertyController: " + se.getMessage());
                ProActiveRuntimeException pare = new ProActiveRuntimeException(
                    "Cannot initialize SCAPropertyController: " + se.getMessage());
                pare.initCause(se);
                throw pare;
            } catch (NoSuchMethodException nsme) {
                logger.error("Cannot initialize SCAPropertyController: " + nsme.getMessage());
                ProActiveRuntimeException pare = new ProActiveRuntimeException(
                    "Cannot initialize SCAPropertyController: " + nsme.getMessage());
                pare.initCause(nsme);
                throw pare;
            }
        }
    }

    public boolean containsDeclaredPropertyName(String name) {
        init();
        return declaredPropertyNames.contains(name);
    }

    public String[] getDeclaredPropertyNames() {
        init();
        return declaredPropertyNames.toArray(new String[] {});
    }

    public Class<?> getDeclaredPropertyType(String name) throws NoSuchPropertyException {
        assertPropertyExist(name);
        return declaredPropertyTypes.get(name);
    }

    public boolean containsPropertyName(String name) {
        return initilizedProperties.contains(name);
    }

    public String[] getPropertyNames() {
        return initilizedProperties.toArray(new String[] {});
    }

    public Class<?> getType(String name) throws NoSuchPropertyException {
        assertPropertyExist(name);
        return propertyTypes.get(name);
    }

    public void setType(String name, Class<?> type) throws NoSuchPropertyException,
            IncompatiblePropertyTypeException {
        assertPropertyExist(name);
        if (!declaredPropertyTypes.get(name).isAssignableFrom(type)) {
            logger.error("The given type is not assignable to the declared type of the property \"" + name +
                "\"");
            throw new IncompatiblePropertyTypeException(
                "The given type is not assignable to the declared type of the property \"" + name + "\"");
        }
        propertyTypes.put(name, type);
    }

    public Object getValue(String name) throws NoSuchPropertyException {
        assertPropertyExist(name);
        if (containsPropertyName(name)) {
            Object contentClass = owner.getReferenceOnBaseObject();
            Method getter = propertyMethods.get(getGetterName(name));
            try {
                return getter.invoke(contentClass, new Object[] {});
            } catch (IllegalArgumentException iae) {
                throw propertyValueError(name, true, iae);
            } catch (IllegalAccessException iae) {
                throw propertyValueError(name, true, iae);
            } catch (InvocationTargetException ite) {
                throw propertyValueError(name, true, ite);
            }
        } else {
            return null;
        }
    }

    /*
     * Gets the getter method name for the given property.
     *
     * @param name Name of the property.
     * @return The getter method name.
     */
    private String getGetterName(String name) {
        return "get" + nameUp(name);
    }

    public void setValue(String name, Object value) throws NoSuchPropertyException,
            IncompatiblePropertyTypeException {
        assertPropertyExist(name);
        Class<?> currentType = null;
        if (propertyTypes.containsKey(name)) {
            currentType = propertyTypes.get(name);
        } else {
            currentType = declaredPropertyTypes.get(name);
            propertyTypes.put(name, value.getClass());
        }
        if (!currentType.isInstance(value)) {
            logger.error("Cannot set value of property \"" + name +
                "\" because the given value is not assignable to the type of the property");
            throw new IncompatiblePropertyTypeException("Cannot set value of property \"" + name +
                "\" because the given value is not assignable to the type of the property");
        }
        Object contentClass = owner.getReferenceOnBaseObject();
        Method setter = propertyMethods.get(getSetterName(name));
        try {
            setter.invoke(contentClass, new Object[] { value });
        } catch (IllegalArgumentException iae) {
            throw propertyValueError(name, false, iae);
        } catch (IllegalAccessException iae) {
            throw propertyValueError(name, false, iae);
        } catch (InvocationTargetException ite) {
            throw propertyValueError(name, false, ite);
        }
        initilizedProperties.add(name);
    }

    /*
     * Gets the setter method name for the given property.
     *
     * @param name Name of the property.
     * @return The setter method name.
     */
    private String getSetterName(String name) {
        return "set" + nameUp(name);
    }

    /*
     * Converts a string to another string which the first letter become capital.
     *
     * @param name Name to convert.
     * @return String which first letter is capitalized.
     */
    private String nameUp(String name) {
        char[] nameUpper = name.toCharArray();
        nameUpper[0] = Character.toUpperCase(nameUpper[0]);
        String nameUp = new String(nameUpper);
        return nameUp;
    }

    /*
     * Writes an error message on logger and returns an exception for errors occurring when trying
     * to get or set the value of a property.
     *
     * @param name Name of the property.
     * @param onGet Boolean indicating if error occurred when trying to get the value of a property.
     * @return ProActiveRuntimeException for errors occurring when trying to get or set the value of
     * a property.
     */
    private ProActiveRuntimeException propertyValueError(String name, boolean onGet, Exception e) {
        String message = "Cannot " + (onGet ? "get" : "set") + " value of property \"" + name + "\": " +
            e.getMessage();
        logger.error(message);
        ProActiveRuntimeException pare = new ProActiveRuntimeException(message);
        pare.initCause(e);
        return pare;
    }

    /*
     * Asserts that a property exists.
     *
     * @param name Name of the property.
     * @throws NoSuchPropertyException If the property does not exist.
     */
    private void assertPropertyExist(String name) throws NoSuchPropertyException {
        if (!containsDeclaredPropertyName(name)) {
            throw new NoSuchPropertyException("The property \"" + name + "\" does not exist");
        }
    }
}
