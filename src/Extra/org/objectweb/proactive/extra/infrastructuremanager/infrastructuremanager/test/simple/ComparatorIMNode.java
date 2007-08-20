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
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
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
package org.objectweb.proactive.extra.infrastructuremanager.test.simple;

import java.util.Comparator;

import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;

/**
 * <p>This comparator allow to sort IMNode by :
 * </p>
 * <ul>
 * <li>ProActive Descriptor</li>
 * <li>Virtual Node</li>
 * <li>Host machine</li>
 * <li>Virtual Machine</li>
 * </ul>
 * 
 * @author ProActive Team
 * @version 1.0, Jun 25, 2007
 * @since ProActive 3.2
 */
public class ComparatorIMNode implements Comparator {
    public int compare(Object arg0, Object arg1) {
        //System.out.println("compare");
        IMNode imnode1 = (IMNode) arg0;
        IMNode imnode2 = (IMNode) arg1;

        //System.out.println("compare : " + imnode1.getNodeName() + " with " + imnode2.getNodeName());
        if (imnode1.getPADName().equals(imnode2.getPADName())) {
            if (imnode1.getVNodeName().equals(imnode2.getVNodeName())) {
                if (imnode1.getHostName().equals(imnode2.getHostName())) {
                    if (imnode1.getDescriptorVMName()
                                   .equals(imnode2.getDescriptorVMName())) {
                        return imnode1.getNodeName()
                                      .compareTo(imnode2.getNodeName());
                    } else {
                        return imnode1.getDescriptorVMName()
                                      .compareTo(imnode2.getDescriptorVMName());
                    }
                } else {
                    return imnode1.getHostName().compareTo(imnode2.getHostName());
                }
            } else {
                return imnode1.getVNodeName().compareTo(imnode2.getVNodeName());
            }
        } else {
            return imnode1.getPADName().compareTo(imnode2.getPADName());
        }
    }
}
