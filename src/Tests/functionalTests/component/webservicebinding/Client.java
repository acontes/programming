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

import junit.framework.Assert;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


public class Client implements BindingController, Runner {
    public static final String SERVICES_NAME = "Services";
    public static final String SERVICEMULTICASTREAL_NAME = "ServiceMulticastReal";
    public static final String SERVICEMULTICASTFALSE_NAME = "ServiceMulticastFalse";
    public static final String SERVICEERROR_NAME = "ServiceError";

    private Services services;
    private ServiceMulticast serviceMulticast;
    private ServiceError serviceError;

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if (SERVICES_NAME.equals(clientItfName)) {
            services = (Services) serverItf;
        } else if (SERVICEMULTICASTREAL_NAME.equals(clientItfName) ||
            SERVICEMULTICASTFALSE_NAME.equals(clientItfName)) {
            serviceMulticast = (ServiceMulticast) serverItf;
        } else if (SERVICEERROR_NAME.equals(clientItfName)) {
            serviceError = (ServiceError) serverItf;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public String[] listFc() {
        return new String[] { SERVICES_NAME, SERVICEMULTICASTREAL_NAME, SERVICEMULTICASTFALSE_NAME,
                SERVICEERROR_NAME };
    }

    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if (SERVICES_NAME.equals(clientItfName)) {
            return services;
        } else if (SERVICEMULTICASTREAL_NAME.equals(clientItfName) ||
            SERVICEMULTICASTFALSE_NAME.equals(clientItfName)) {
            return serviceMulticast;
        } else if (SERVICEERROR_NAME.equals(clientItfName)) {
            return serviceError;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public void unbindFc(String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if (SERVICES_NAME.equals(clientItfName)) {
            services = null;
        } else if (SERVICEMULTICASTREAL_NAME.equals(clientItfName) ||
            SERVICEMULTICASTFALSE_NAME.equals(clientItfName)) {
            serviceMulticast = null;
        } else if (SERVICEERROR_NAME.equals(clientItfName)) {
            serviceError = null;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public BooleanWrapper execute() {
        try {
            if (services != null) {
                services.doNothing();

                int i = 1;
                i = services.incrementInt(i);
                Assert.assertTrue("Integer not (correctly) modified: expected " +
                    (1 + Services.INCREMENT_VALUE) + ", actual " + i, (i == (1 + Services.INCREMENT_VALUE)));

                double[] array = new double[5];
                for (int j = 0; j < array.length; j++) {
                    array[j] = j + (0.1 * j);
                }
                array = services.decrementArrayDouble();//array);
                for (int j = 0; j < array.length; j++) {
                    Assert.assertTrue("Array of double not (correctly) modified: expected array[" + j +
                        "] = " + (j + (0.1 * j) - Services.DECREMENT_VALUE) + ", actual array[" + j + "] = " +
                        array[j], (array[j] == (j + (0.1 * j) - Services.DECREMENT_VALUE)));
                }

                String helloString = "Client";
                helloString = services.hello(helloString);
                Assert.assertTrue("String not (correctly) modified", helloString
                        .equals(Services.HELLO_STRING + "Client"));

                String[] expectedHelloStringSplit = helloString.split(Services.SPLIT_REGEX);
                String[] helloStringSplit = services.splitString(helloString);
                Assert.assertTrue("String not correctly split",
                        helloStringSplit.length == expectedHelloStringSplit.length);
                for (int j = 0; j < helloStringSplit.length; j++) {
                    Assert.assertTrue("String not correctly split", helloStringSplit[j]
                            .equals(expectedHelloStringSplit[j]));
                }

                AnObject defaultObject = new AnObject();
                AnObject object = services.modifyObject(defaultObject);
                Assert.assertTrue("Object not (correctly) modified", !object.equals(defaultObject));

                AnObject[] arrayObject = new AnObject[5];
                for (int j = 0; j < arrayObject.length; j++) {
                    arrayObject[j] = new AnObject();
                }
                arrayObject = services.modifyArrayObject(arrayObject);
                for (int j = 0; j < arrayObject.length; j++) {
                    Assert.assertTrue("Array of objects not (correctly) modified", !arrayObject[j]
                            .equals(defaultObject));
                }
            }

            if (serviceMulticast != null) {
                String string = "A message";
                String[] strings = serviceMulticast.modifyString(string).toArray(new String[] {});
                String precString = null;
                Assert
                        .assertTrue(
                                "Call on multicast interface failed: incorrect number of returned strings, expected " +
                                    TestWebServiceBinding.NUMBER_SERVERS + ", received " + strings.length,
                                strings.length == TestWebServiceBinding.NUMBER_SERVERS);
                for (int j = 0; j < strings.length; j++) {
                    Assert.assertTrue("Call on multicast interface failed: string " + j +
                        " has not been modified", !strings[j].equals(string));
                    Assert.assertTrue("Call on multicast interface failed: string " + j +
                        " is equal to string " + (j + 1), !strings[j].equals(precString));
                    precString = strings[j];
                }
            }

            if (serviceError != null) {
                String result = serviceError.methodError();
                if (result == null) {
                    throw new Exception();
                }
            }

            return new BooleanWrapper(true);
        } catch (Exception e) {
            e.printStackTrace();
            return new BooleanWrapper(false);
        }
    }
}
