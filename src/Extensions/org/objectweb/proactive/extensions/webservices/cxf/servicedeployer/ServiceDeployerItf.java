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

package org.objectweb.proactive.extensions.webservices.cxf.servicedeployer;

/**
 * Interface of the service which will be deployed on the server at the
 * same time as the proactive web application. This service is used to deploy and undeploy
 * Active Object and components on the server side.
 *
 * @author The ProActive Team
 */
public interface ServiceDeployerItf {

    /**
     * Expose the marshalled active object as a web service
     *
     * @param marshalledObject marshalled object
     * @param serviceName Name of the service
     * @param marshalledSerializedMethods byte array representing the methods (of type Method)
     *        to be exposed
     */
    public void deploy(byte[] marshalledObject, String serviceName, byte[] marshalledSerializedMethods);

    /**
     * Undeploy the service whose name is serviceName
     *
     * @param serviceName name of the service
     */
    public void undeploy(String serviceName);
}
