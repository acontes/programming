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
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.activeobject.webservices;

import java.util.LinkedList;

import org.objectweb.proactive.extensions.annotation.ActiveObject;


/**
 * A simple example to expose an active object as a web service.
 *
 * @author The ProActive Team
 */
@ActiveObject
public class HelloWorld extends HelloWorldSuperClass implements java.io.Serializable {

    LinkedList<String> textsToSay = new LinkedList<String>();
    Couple[] couples;

    public HelloWorld() {
    }

    public void putHelloWorld() {
        this.textsToSay.add("Hello world!");
    }

    public void putTextToSay(String textToSay) {
        this.textsToSay.add(textToSay);
    }

    public String sayText() {
        if (this.textsToSay.isEmpty()) {
            return "The list is empty";
        } else {
            return this.textsToSay.poll();
        }
    }

    public Boolean contains(String textToCheck) {
        return new Boolean(textsToSay.contains(textToCheck));
    }

    public void setCouples(Couple[] couples) {
        this.couples = couples;
    }

    public Couple[] getCouples() {
        return couples;
    }
}
