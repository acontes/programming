/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.control;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactoryImpl;


/**
 * Implementation of the {@link org.objectweb.fractal.api.control.NameController}
 *
 * @author The ProActive Team
 *
 */
public class PANameControllerImpl extends AbstractPAController implements NameController,
        ControllerStateDuplication {
    /**
     * 
     */
    private static final long serialVersionUID = 500L;
    // FIXME coherency between this value and the one in component parameters controller
    String name;

    /**
     * @param owner
     */
    public PANameControllerImpl(Component owner) {
        super(owner);
    }

    @Override
    protected void setControllerItfType() {
        try {
            setItfType(PAGCMTypeFactoryImpl.instance().createFcItfType(Constants.NAME_CONTROLLER,
                    NameController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                    TypeFactory.SINGLE));
        } catch (InstantiationException e) {
            throw new ProActiveRuntimeException("cannot create controller " + this.getClass().getName(), e);
        }
    }

    /*
     * @see org.objectweb.fractal.api.control.NameController#getFcName()
     */
    public String getFcName() {
        return name;
    }

    /*
     * @see org.objectweb.fractal.api.control.NameController#setFcName(java.lang.String)
     */
    public void setFcName(String name) {
        this.name = name;
    }

    public void duplicateController(Object c) {
        if (c instanceof String) {
            name = (String) c;

        } else {
            throw new ProActiveRuntimeException("PANameController: Impossible to duplicate the controller " +
                this + " from the controller" + c);
        }

    }

    public ControllerState getState() {
        return new ControllerState(name);
    }
}
