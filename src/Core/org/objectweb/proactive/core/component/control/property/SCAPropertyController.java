package org.objectweb.proactive.core.component.control.property;

import org.objectweb.fractal.api.control.AttributeController;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.julia.type.BasicInterfaceType;
import org.objectweb.proactive.core.component.control.AbstractPAController;
import org.objectweb.proactive.core.component.control.PAPropertyController;


/**
 * Property control interface for SCA primitive components.
 * Take the interface from Frascati
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
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
     * @since 1.1.1
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
     * @since 1.1.1
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
     * 
     * @since 1.1.1
     */
    public String[] getPropertyNames();

    /**
     * Return <code>true</code> if the specified property can be injected in the
     * content class.
     * 
     * @param name  the property name
     * @return      <code>true</code> if the property can be injected,
     *              <code>false</code> otherwise
     * @since 1.1.2
     */
    public boolean containsDeclaredPropertyName(String name);

    /**
     * Return the names of the properties which can be injected in the content
     * class.
     * 
     * @since 1.1.1
     */
    public String[] getDeclaredPropertyNames();

    /**
     * Return the type of the specified property, provided that this property
     * can be injected in the content class.
     * 
     * @param name  the property name
     * @return      the property type
     * @since 1.1.2
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