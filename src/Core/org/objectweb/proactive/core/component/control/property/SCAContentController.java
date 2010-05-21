/***
 * OW2 FraSCAti Tinfi
 * Copyright (C) 2007-2010 INRIA, USTL
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Lionel Seinturier
 */
package org.objectweb.proactive.core.component.control.property;

import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.julia.type.BasicInterfaceType;
import org.osoa.sca.RequestContext;
import org.osoa.sca.annotations.Destroy;


/**
 * Content control interface for SCA primitive components.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public interface SCAContentController {

    /** <code>NAME</code> of the content controller. */
    final public static String NAME = "/sca-content-controller";

    /** <code>TYPE</code> of the content controller. */
    final public static InterfaceType TYPE = new BasicInterfaceType(NAME, SCAContentController.class
            .getName(), false, false, false);

    /**
     * Declare the content class which should be used.
     * This operation can only be performed if the component is stopped.
     * 
     * @param c  the content class
     * @throws IllegalLifeCycleException  if the component is not stopped
     * @throws ContentInstantiationException
     *      if the content can not be instantiated
     * @since 1.0
     */
    public void setFcContentClass(Class<?> c) throws IllegalLifeCycleException, ContentInstantiationException;

    /**
     * Return a content instance according to the scope policy defined for this
     * component.
     * 
     * @throws ContentInstantiationException
     *      if the content can not be instantiated
     */
    public Object getFcContent() throws ContentInstantiationException;

    /**
     * Notify the content controller that the specified content instance is no
     * longer needed. If relevant with the the scope policy, this gives the
     * controller the opportunity to call the {@link Destroy} annotated
     * method on the content instance.
     * 
     * @param content  the content instance which is released
     * @param isEndMethod
     *      <code>true</code> if the method which releases the content instance
     *      is annotated with {@link org.osoa.sca.annotations.EndsConversation}
     */
    public void releaseFcContent(Object content, boolean isEndMethod);

    /**
     * Push a new request context on the request context stack. This request
     * context is returned by {@link
     * org.osoa.sca.ComponentContext#getRequestContext()}. The stack is needed
     * in case of reentrant component operation invocations.
     * 
     * @param rc  the request context to be pushed
     * @since 1.1.1
     */
    public void pushRequestContext(RequestContext rc);

    /**
     * Pop the previously pushed request context.
     * 
     * @since 1.1.1
     */
    public void popRequestContext();

    /**
     * Return the request context on top of the request context stack.
     * 
     * @return  the request context on top of the request context stack
     * @since 1.1.1
     */
    public RequestContext peekRequestContext();

    /**
     * Eager initialize the content instance associated with this component.
     * Relevant only for composite-scoped components.
     * 
     * @throws ContentInstantiationException
     *      if the content can not be instantiated or if the component is not
     *      composite-scoped
     */
    public void eagerInit() throws ContentInstantiationException;

    /**
     * Invoke the @{@link org.ow2.frascati.tinfi.annotations.Start} annotated
     * method on all current content instances associated with the component.
     * 
     * @throws ContentInstantiationException
     * 				in case of exception when invoking the method
     * @since 1.3
     */
    public void start() throws ContentInstantiationException;

    /**
     * Invoke the @{@link org.ow2.frascati.tinfi.annotations.Stop} annotated
     * method on all current content instances associated with the component.
     * 
     * @throws ContentInstantiationException
     * 				in case of exception when invoking the method
     * @since 1.3
     */
    public void stop() throws ContentInstantiationException;

    /**
     * Return <code>true</code> if the specified property is declared by the
     * content class managed by this controller.
     * 
     * @param name  the property name
     * @return      <code>true</code> if the property is declared,
     *              <code>false</code> otherwise
     * @since 1.1.2
     */
    public boolean containsPropertyName(String name);

    /**
     * Return the names of the properties declared by the content class
     * managed by this controller.
     * 
     * @return  the propery names
     * @since 1.1.1
     */
    public String[] getPropertyNames();

    /**
     * Return the type of the specified property declared by the content class
     * managed by this controller.
     * 
     * @param name  the property name
     * @return      the property type
     * @since 1.1.2
     */
    public Class<?> getPropertyType(String name);

    /**
     * Inject on all current content instances associated with the component,
     * the value of the specified property.
     *  
     * @param name   the property name
     * @param value  the property value
     * @since 1.2.1
     */
    public void setPropertyValue(String name, Object value);
}
