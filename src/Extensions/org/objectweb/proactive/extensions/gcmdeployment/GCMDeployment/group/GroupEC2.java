/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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

package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group;

import java.util.List;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;

import com.amazonaws.ec2.AmazonEC2;
import com.amazonaws.ec2.AmazonEC2Client;
import com.amazonaws.ec2.AmazonEC2Exception;
import com.amazonaws.ec2.mock.AmazonEC2Mock;
import com.amazonaws.ec2.model.Reservation;
import com.amazonaws.ec2.model.RunInstancesRequest;
import com.amazonaws.ec2.model.RunInstancesResponse;
import com.amazonaws.ec2.model.RunInstancesResult;
import com.amazonaws.ec2.model.RunningInstance;


public class GroupEC2 extends AbstractJavaGroup {

    private String imageId;
    private String accessKeyId;
    private String secretAccessKey;

    static protected class EC2InstanceRunner implements Runnable {
        AmazonEC2 service;
        private RunInstancesRequest request;
        private RunInstancesResponse response;

        public EC2InstanceRunner(String accessKeyId, String secretAccessKey, RunInstancesRequest request) {
            service = new AmazonEC2Client(accessKeyId, secretAccessKey);
//            service = new AmazonEC2Mock();
            this.request = request;
            this.response = null;
        }

        public void run() {

            try {
                response = service.runInstances(request);

                if (response.isSetRunInstancesResult()) {
                    RunInstancesResult runInstancesResult = response.getRunInstancesResult();
                    if (runInstancesResult.isSetReservation()) {
                        Reservation reservation = runInstancesResult.getReservation();
                        List<RunningInstance> runningInstanceList = reservation.getRunningInstance();
                        for (RunningInstance runningInstance : runningInstanceList) {
                            if (runningInstance.isSetInstanceId()) {
                                System.out.print("                    InstanceId");
                                System.out.println();
                                System.out
                                        .print("                        " + runningInstance.getInstanceId());
                                System.out.println();
                            }
                        }
                    }
                }

            } catch (AmazonEC2Exception e) {
                throw new ProActiveRuntimeException("EC2 RunInstances request failed", e);
            }
        }

        public RunInstancesResponse getResponse() {
            return response;
        }
    }

    @Override
    public Runnable buildJavaJob(GCMApplicationInternal gcma) {

        RunInstancesRequest request = new RunInstancesRequest();

        request.setImageId(imageId);
        int hostCapacity = getHostInfo().getHostCapacity();
        
        // request for the number of instances equal to host capacity
        //
        request.setMinCount(hostCapacity);
        request.setMaxCount(hostCapacity);

        try {
            StringBuffer userData = new StringBuffer();
            String parentURL = RuntimeFactory.getDefaultRuntime().getURL();

            // set root node access data in the instance's user data, one item per line,
            // according to the following sequence :
            //
            // 1 parentURL
            // 2 hostCapacity
            // 3 vmCapacity
            // 4 deploymentId
            // 5 topologyId
            // 
            userData.append(parentURL);
            userData.append('\n');
            userData.append(hostCapacity);
            userData.append('\n');
            userData.append(getHostInfo().getVmCapacity());
            userData.append('\n');
            userData.append(gcma.getDeploymentId());
            userData.append('\n');
            userData.append(getHostInfo().getToplogyId());
            userData.append('\n');

            byte[] charArray = userData.toString().getBytes();
            byte[] encodedData = org.apache.commons.codec.binary.Base64.encodeBase64(charArray, false);

            request.setUserData(new String(encodedData));

            System.err.println("EC2 request user data : " + userData.toString());

            EC2InstanceRunner instanceRunner = new EC2InstanceRunner(accessKeyId, secretAccessKey, request);

            return instanceRunner;

        } catch (ProActiveException e) {
            throw new ProActiveRuntimeException("Couldn't get default ProActive runtime", e);
        }
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    @Override
    public void check() throws IllegalStateException {
        // TODO Auto-generated method stub

    }

}
