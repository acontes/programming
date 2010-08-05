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
package org.objectweb.proactive.extensions.component.sca.control;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extensions.component.sca.exceptions.IncompatiblePropertyTypeException;
import org.objectweb.proactive.extensions.component.sca.exceptions.NoSuchPropertyException;


/**
 * Component interface to control SCA properties of the SCA/GCM component to which it belongs.
 *
 * @author The ProActive Team
 */
@PublicAPI
public interface SCAPropertyController {
    /**
     * Indicates if the given property is declared as property in the content class.
     *
     * @param name The property name.
     * @return True, if the given property is declared as property in the content class, false otherwise.
     */
    public boolean containsDeclaredPropertyName(String name);

    /**
     * Returns the names of the properties declared in the content class.
     *
     * @return The names of the properties declared in the content class.
     */
    public String[] getDeclaredPropertyNames();

    /**
     * Returns the type of the given property as declared in the content class.
     *
     * @param name The property name.
     * @return The type of the given property as declared in the content class.
     * @throws NoSuchPropertyException If the property does not exist.
     */
    public Class<?> getDeclaredPropertyType(String name) throws NoSuchPropertyException;

    /**
     * Indicates if the given property has been set.
     *
     * @param name The property name.
     * @return True, if the property has been set, false otherwise.
     */
    public boolean containsPropertyName(String name);

    /**
     * Returns the names of the properties whose values have been set by invoking {@link #setValue(String, Object)}.
     *
     * @return The names of the properties whose values have been set by invoking {@link #setValue(String, Object)}.
     */
    public String[] getPropertyNames();

    /**
     * Returns the current type of the given property or null if the type has not been set by invoking
     * {@link #setType(String, Class)}.
     *
     * @param name The property name.
     * @return The current type of the given property or null if the type has not been set by invoking
     * {@link #setType(String, Class)}.
     * @throws NoSuchPropertyException If the property does not exist.
     */
    public Class<?> getType(String name) throws NoSuchPropertyException;

    /**
     * Sets the type of the given property to the given type which must be a sub type (or same) of the declared type.
     *
     * @param name The property name.
     * @param type The property type.
     * @throws NoSuchPropertyException If the property does not exist.
     * @throws IncompatiblePropertyTypeException If the given type is not a sub type (or same) of the declared type.
     */
    public void setType(String name, Class<?> type) throws NoSuchPropertyException,
            IncompatiblePropertyTypeException;

    /**
     * Returns the value of the given property or null if the value has not been set by invoking
     * {@link #setValue(String, Object)}.
     *
     * @param name The property name.
     * @return The property value.
     * @throws NoSuchPropertyException If the property does not exist.
     */
    public Object getValue(String name) throws NoSuchPropertyException;

    /**
     * Sets the value of the given property to the given value which must be a sub type (or same) of the declared
     * type.
     *
     * @param name The property name.
     * @param value The property value.
     * @throws NoSuchPropertyException If the property does not exist.
     * @throws IncompatiblePropertyTypeException If the given value is not a sub type (or same) of the current type.
     */
    public void setValue(String name, Object value) throws NoSuchPropertyException,
            IncompatiblePropertyTypeException;
}