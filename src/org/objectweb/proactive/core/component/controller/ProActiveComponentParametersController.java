/* 
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, 
 *            Concurrent computing with Security and Mobility
 * 
 * Copyright (C) 1997-2004 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
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
package org.objectweb.proactive.core.component.controller;

import java.io.Serializable;

import org.apache.log4j.Logger;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;

import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;


/**
 * a controller for accessing configuration parameters of the component.
 * 
 * 
 * @author Matthieu Morel
 *
 */
public class ProActiveComponentParametersController extends ProActiveController
    implements Serializable, ComponentParametersController {
    protected static Logger logger = Logger.getLogger(ProActiveComponentParametersController.class.getName());
    private ComponentParameters componentParameters;

	/**
	 * Constructor
	 * @param owner the super controller
	 */
    public  ProActiveComponentParametersController(Component owner) {
        super(owner);
		try {
			setItfType(ProActiveTypeFactory.instance().createFcItfType(Constants.COMPONENT_PARAMETERS_CONTROLLER,
										ProActiveContentController.class.getName(),
								TypeFactory.SERVER, TypeFactory.MANDATORY,
								TypeFactory.SINGLE));
		} catch (InstantiationException e) {
			throw new ProActiveRuntimeException("cannot create controller " + this.getClass().getName());
		}

    }

	/**
	 * see {@link ComponentParametersController#getComponentParameters()}
	 */
    public ComponentParameters getComponentParameters() {
        return componentParameters;
    }

	/**
	 * see {@link ComponentParametersController#setComponentParameters(ComponentParameters)}
	 */
    public void setComponentParameters(ComponentParameters componentParameters) {
        this.componentParameters = componentParameters;
    }
    
    /**
     * see {@link ComponentParametersController#setComponentName(String)}
     */
    public void setFcName(String componentName) {
    	componentParameters.setName(componentName);
    }
    
    
	/**
	 * see {@link org.objectweb.fractal.api.control.NameController#getFcName()}
	 */
	public String getFcName() {
		return componentParameters.getName();
	}

}