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
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package functionalTests.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProActiveGroup;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class PrimitiveComponentE implements I1, BindingController {
    protected final static Logger logger = ProActiveLogger.getLogger("functionalTestss.components");
    public final static String I2_ITF_NAME = "i2";

    // typed collective interface
    Map<String, I2> i2 = new HashMap<String, I2>(); // collection interface

    //    I2 i2 = (I2) Fractive.createMulticastClientInterface(I2_ITF_NAME,
    //            I2.class.getName());

    // ref on the Group
    //    Group i2Group = ProActiveGroup.getGroup(i2);

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if (clientItfName.startsWith(I2_ITF_NAME) && !clientItfName.equals(I2_ITF_NAME)) {
            // conformance to the Fractal API
            i2.put(clientItfName, (I2) serverItf);
        } else {
            throw new IllegalBindingException("Binding impossible : wrong client interface name (" +
                serverItf + ")");
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.fractal.api.control.BindingController#listFc()
     */
    public String[] listFc() {
        Set itf_names = i2.keySet();
        return (String[]) itf_names.toArray(new String[itf_names.size()]);
    }

    /* (non-Javadoc)
     * @see org.objectweb.fractal.api.control.BindingController#lookupFc(java.lang.String)
     */
    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if (clientItfName.startsWith(I2_ITF_NAME) && !clientItfName.equals(I2_ITF_NAME)) {
            return i2.get(clientItfName);
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.fractal.api.control.BindingController#unbindFc(java.lang.String)
     */
    public void unbindFc(String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if (clientItfName.startsWith(I2_ITF_NAME)) {
            i2.remove(clientItfName);
            if (logger.isDebugEnabled()) {
                logger.debug(clientItfName + " interface unbound");
            }
        } else {
            throw new NoSuchInterfaceException("client interface not found");
        }
    }

    public Message processInputMessage(Message message) {
        Set<Map.Entry<String, I2>> entries = i2.entrySet();
        Message result = new Message();
        for (Map.Entry<String, I2> entry : entries) {
            result.append(entry.getValue().processOutputMessage(message));
        }
        result.append(MESSAGE);
        return result;
    }
}
