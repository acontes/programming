/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.objectweb.proactive.extensions.webservices.cxf.deployer;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.cxf.servicedeployer.ServiceDeployerItf;


public final class PADeployer {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    public PADeployer() {
    }

    public static void deploy(Object o, String url, String urn) throws Exception {
        String correctUrl = "";

        if (!url.endsWith("/")) {
            correctUrl = url + "/";
        } else {
            correctUrl = url;
        }

        if (!correctUrl.startsWith("http://")) {
            correctUrl = "http://" + correctUrl;
        }

        byte[] marshalledObject = HttpMarshaller.marshallObject(o);

        ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
        factory.setServiceClass(ServiceDeployerItf.class);
        factory.setAddress(correctUrl + WSConstants.SERVICES_PATH + "ServiceDeployer");
        ServiceDeployerItf client = (ServiceDeployerItf) factory.create();
        client.deploy(marshalledObject, urn);

    }

}
