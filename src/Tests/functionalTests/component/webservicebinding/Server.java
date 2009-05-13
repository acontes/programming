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
package functionalTests.component.webservicebinding;

public class Server implements Services, Service {
    public Server() {
    }

    public void doNothing() {
        System.out.println("doNothing called!");
    }

    public int incrementInt(int i) {
        return i + Services.INCREMENT_VALUE;
    }

    public double[] decrementArrayDouble() {//double[] array) {
        double[] array = new double[5];
        for (int j = 0; j < array.length; j++) {
            array[j] = j + (0.1 * j);
        }
        for (int i = 0; i < array.length; i++) {
            array[i] -= Services.DECREMENT_VALUE;
        }
        return array;
    }

    public String hello(String name) {
        return Services.HELLO_STRING + name;
    }

    public String[] splitString(String string) {
        return string.split(Services.SPLIT_REGEX);
    }

    public AnObject modifyObject(AnObject object) {
        object.setId(object.getId() + "Modified");
        object.setIntField(incrementInt(object.getIntField()));
        double[] array = object.getArrayField();
        for (int i = 0; i < array.length; i++) {
            array[i] -= Services.DECREMENT_VALUE;
        }
        object.setArrayField(array);//decrementArrayDouble(object.getArrayField());
        AnObject object2 = new AnObject();
        object2.setId("Id" + ((int) (Math.random() * 100)));
        object.setObjectField(object2);
        return object;
    }

    public AnObject[] modifyArrayObject(AnObject[] arrayObject) {
        for (int i = 0; i < arrayObject.length; i++) {
            arrayObject[i] = modifyObject(arrayObject[i]);
        }
        return arrayObject;
    }

    public String modifyString(String string) {
        return string + " modified at: " + System.currentTimeMillis();
    }
}
