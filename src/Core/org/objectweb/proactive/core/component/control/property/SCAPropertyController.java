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
package org.objectweb.proactive.core.component.control.property;

import org.objectweb.fractal.api.control.AttributeController;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.julia.type.BasicInterfaceType;
import org.objectweb.proactive.core.component.control.AbstractPAController;
import org.objectweb.proactive.core.component.control.PAPropertyController;


public interface SCAPropertyController {

    /** <code>NAME</code> of the content controller. */
    final public static String NAME = "sca-property-controller";

    /** <code>TYPE</code> of the content controller. */
    final public static InterfaceType TYPE = new BasicInterfaceType(NAME, SCAPropertyController.class
            .getName(), false, false, false);

    public void init();

    /**
     * Set the type of the specified property. If the property has already been
     * set, the old value is lost, and the new value is recorded.
     * 
     * @param type   the property type
     * @param value  the property value
     */
    public void setType(String name, Class<?> type);

    /**
     * Set the value of the specified property. If the property has already been
     * set, the old value is lost, and the new value is recorded.
     * 
     * @param name   the property name
     * @param value  the property value
     */
    public void setValue(String name, Object value);

    /**
     * Return the type of the specified property. Return <code>null</code> if
     * the property type has not been set.
     * 
     * @param name  the property name
     * @return      the property value
     */
    public Class<?> getType(String name);

    /**
     * Return the value of the specified property. Return <code>null</code> if
     * the property value has not been set.
     * 
     * @param name  the property name
     * @return      the property value
     */
    public Object getValue(String name);

    /**
     * Return <code>true</code> if the specified property has been set.
     * 
     * @param name  the property name
     * @return      <code>true</code> if the property has been set,
     *              <code>false</code> otherwise
     */
    public boolean containsPropertyName(String name);

    /**
     * Return the names of the properties whose values have been set by invoking
     * {@link #setValue(String, Object)}.
     */
    public String[] getPropertyNames();

    /**
     * Return <code>true</code> if the specified property can be injected in the
     * content class.
     * 
     * @param name  the property name
     * @return      <code>true</code> if the property can be injected,
     *              <code>false</code> otherwise
     */
    public boolean containsDeclaredPropertyName(String name);

    /**
     * Return the names of the properties which can be injected in the content
     * class.
     */
    public String[] getDeclaredPropertyNames();

    /**
     * Return the type of the specified property, provided that this property
     * can be injected in the content class.
     * 
     * @param name  the property name
     * @return      the property type
     */
    public Class<?> getDeclaredPropertyType(String name);

    /**
     * Set the reference of the property controller which promotes the specified
     * property to the current property controller.
     * 
     * @param name      the promoter property name
     * @param promoter  the promoter component or
     *                  <code>null</code> to unregister the promoter
     * @throws IllegalPromoterException
     *      thrown when attempting to set a cycle between property promoters
     * @since 0.4.3
     */
    //public void setPromoter( String name, SCAPropertyController promoter )
    //throws Exception;
    /**
     * Return the reference of the property controller which promotes the
     * specified property. Return <code>null</code> if the property is managed
     * locally by the current property controller.
     * 
     * @param name  the promoter property name
     * @return      the promoter component or <code>null</code>
     * @since 0.4.3
     */
    //public SCAPropertyController getPromoter( String name );
}