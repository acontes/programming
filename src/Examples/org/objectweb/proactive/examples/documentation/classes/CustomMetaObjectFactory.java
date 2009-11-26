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
//@snippet-start class_CustomMetaObjectFactory
package org.objectweb.proactive.examples.documentation.classes;

import java.io.Serializable;
import org.objectweb.proactive.core.body.MetaObjectFactory;
import org.objectweb.proactive.core.body.ProActiveMetaObjectFactory;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFactory;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.mop.MethodCall;


/**
 * @author ProActive Team
 *
 * Customized Meta-Object Factory
 */
public class CustomMetaObjectFactory extends ProActiveMetaObjectFactory {

    private static final MetaObjectFactory instance = new CustomMetaObjectFactory();

    //return a new factory instance
    public static MetaObjectFactory newInstance() {
        return instance;
    }

    private CustomMetaObjectFactory() {
        super();
    }

    protected RequestFactory newRequestFactorySingleton() {
        System.out.println("Creating the  custom metaobject factory...");
        return new CustomRequestFactory();
    }

    protected class CustomRequestFactory extends RequestFactoryImpl implements Serializable {

        public Request newRequest(MethodCall methodCall, UniversalBody sourceBody, boolean isOneWay,
                long sequenceID) {
            System.out.println("Received a new request...");
            return new CustomRequest(methodCall, sourceBody, isOneWay, sequenceID);
        }

        protected class CustomRequest extends RequestImpl {
            public CustomRequest(MethodCall methodCall, UniversalBody sourceBody, boolean isOneWay,
                    long sequenceID) {
                super(methodCall, sourceBody, isOneWay, sequenceID);
                System.out.println("I am a custom request handler");
            }
        }
    }
}
//@snippet-end class_CustomMetaObjectFactory
