/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
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
//@snippet-start class_UnSerializableAgent2
package org.objectweb.proactive.examples.documentation.classes;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Random;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAMobileAgent;


/**
 * @author The ProActive Team
 *
 */
public class UnSerializableAgent2 implements Serializable {

    private transient BigInteger toBeSaved; //will be null after serialization

    /**
     * Empty, no-arg constructor required by ProActive
     */
    public UnSerializableAgent2() {
    }

    public UnSerializableAgent2(int length) {
        toBeSaved = new BigInteger(length, new Random());
    }

    public void displayArray() {
        System.out.println(toBeSaved.toString());
    }

    /**
     * Migrates the active object
     *
     * @param url - URL of the node where you want to
     *              migrate your active object to
     */
    public void moveTo(String url) {
        try {
            PAMobileAgent.migrateTo(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the name of the host where the active
     * object is.
     *
     * @return The node url and the hostname where the active object is
     */
    public String whereAreYou() {
        try {
            return PAActiveObject.getActiveObjectNodeUrl(PAActiveObject.getStubOnThis()) + " on host " +
                InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Localhost lookup failed";
        }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        //do the default serialization
        out.defaultWriteObject();
        //do the custom serialization
        out.writeObject(toBeSaved);
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        //do the default deserialization
        in.defaultReadObject();
        //do the custom deserialization
        toBeSaved = (BigInteger) in.readObject();
    }
}
//@snippet-end class_UnSerializableAgent2