/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive;


/**
 * <P>
 * InitActive is related to the initialization of the activity of an
 * active object. The initialization of the activity is done only once.
 * In case of a migration, an active object restarts its activity
 * automatically without reinitializing.
 * </P><P>
 * An object implementing this interface can be invoked to perform the
 * initialization work before the activity is started. The object being
 * reified as an active object can implement this interface or an external
 * class can also be used.
 * </P>
 * <P>
 * It is generally the role of the body of the active object to perform the
 * call on the object implementing this interface.
 * </P>
 *
 * @author  ProActive Team
 * @version 1.0,  2002/06
 * @since   ProActive 0.9.3
 */
public interface InitActive extends Active {

    /**
     * Initializes the activity of the active object.
     * @param body the body of the active object being initialized
     */
    public void initActivity(Body body);
}
