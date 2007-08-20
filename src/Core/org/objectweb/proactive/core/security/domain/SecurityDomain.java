/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
package org.objectweb.proactive.core.security.domain;

import java.util.ArrayList;

import org.objectweb.proactive.core.security.SecurityContext;
import org.objectweb.proactive.core.security.SecurityEntity;
import org.objectweb.proactive.core.security.exceptions.SecurityNotAvailableException;
import org.objectweb.proactive.core.security.securityentity.Entity;


/**
 * @author Arnaud Contes
 *
 * A domain is used to enforce a security policy to a set of Runtimes
 *
 */
public interface SecurityDomain extends SecurityEntity {

    /**
     * @param securityContext
     * @return returns the policy matching the corresponding securityContext
     *
     */
    public SecurityContext getPolicy(SecurityContext securityContext);

    /**
     * @return returns the certificate of the entity corresponding to this domain
     * @throws SecurityNotAvailableException
     */
    public byte[] getCertificateEncoded() throws SecurityNotAvailableException;

    /**
     * @return returns the set of wrapping entities
     * @throws SecurityNotAvailableException
     */
    public ArrayList<Entity> getEntities() throws SecurityNotAvailableException;

    /**
     * @return Returns the name of the domain.
     */
    public String getName();
}
