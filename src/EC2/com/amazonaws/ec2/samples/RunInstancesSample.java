/******************************************************************************* 
 *  Copyright 2008 Amazon Technologies, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  
 *  You may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 *  This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 *  CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 * ***************************************************************************** 
 *    __  _    _  ___ 
 *   (  )( \/\/ )/ __)
 *   /__\ \    / \__ \
 *  (_)(_) \/\/  (___/
 * 
 *  Amazon EC2 Java Library
 *  API Version: 2008-05-05
 *  Generated: Thu Aug 28 20:50:13 PDT 2008 
 * 
 */

package com.amazonaws.ec2.samples;

import java.util.List;
import java.util.ArrayList;
import com.amazonaws.ec2.*;
import com.amazonaws.ec2.model.*;
import com.amazonaws.ec2.mock.AmazonEC2Mock;


/**
 *
 * Run Instances  Samples
 *
 *
 */
public class RunInstancesSample {

    /**
     * Just add few required parameters, and try the service
     * Run Instances functionality
     *
     * @param args unused
     */
    public static void main(String... args) {

        /************************************************************************
         * Access Key ID and Secret Acess Key ID, obtained from:
         * http://aws.amazon.com
         ***********************************************************************/
        String accessKeyId = "1K6JVRSE482QZZ8N7302";
        String secretAccessKey = "JRJxPd2RcWjPCaBbtfjsY71vf8VryHl0ikLunhoj";

        /************************************************************************
         * Instantiate Http Client Implementation of Amazon EC2 
         ***********************************************************************/
        AmazonEC2 service = new AmazonEC2Client(accessKeyId, secretAccessKey);

        /************************************************************************
         * Uncomment to try advanced configuration options. Available options are:
         *
         *  - Signature Version
         *  - Proxy Host and Proxy Port
         *  - Service URL
         *  - User Agent String to be sent to Amazon EC2   service
         *
         ***********************************************************************/
        // AmazonEC2Config config = new AmazonEC2Config();
        // config.setSignatureVersion("0");
        // AmazonEC2 service = new AmazonEC2Client(accessKeyId, secretAccessKey, config);
        /************************************************************************
         * Uncomment to try out Mock Service that simulates Amazon EC2 
         * responses without calling Amazon EC2  service.
         *
         * Responses are loaded from local XML files. You can tweak XML files to
         * experiment with various outputs during development
         *
         * XML files available under com/amazonaws/ec2/mock tree
         *
         ***********************************************************************/
        // AmazonEC2 service = new AmazonEC2Mock();
        /************************************************************************
         * Setup request parameters and uncomment invoke to try out 
         * sample for Run Instances 
         ***********************************************************************/
        RunInstancesRequest request = new RunInstancesRequest();

        request.setImageId("ami-3bd63252");
        request.setMinCount(1);
        request.setMaxCount(1);

        // @TODO: set request parameters here

        invokeRunInstances(service, request);

    }

    /**
     * Run Instances  request sample
     * The RunInstances operation launches a specified number of instances.
     * If Amazon EC2 cannot launch the minimum number AMIs you request, no instances
     * launch. If there is insufficient capacity to launch the maximum number of AMIs
     * you request, Amazon EC2 launches as many as possible to satisfy the requested
     * maximum values.
     * Every instance is launched in a security group. If you do not specify a
     * security group at launch, the instances start in your default security group.
     * For more information on creating security groups, see CreateSecurityGroup.
     * An optional instance type can be specified. For information about instance
     * types, see Instance Types.
     * You can provide an optional key pair ID for each image in the launch request
     * (for more information, see CreateKeyPair). All instances that are created from
     * images that use this key pair will have access to the associated public key at
     * boot. You can use this key to provide secure access to an instance of an image
     * on a per-instance basis. Amazon EC2 public images use this feature to provide
     * secure access without passwords.
     * Important:
     * Launching public images without a key pair ID will leave them inaccessible.
     * The public key material is made available to the instance at boot time by
     * placing it in the openssh_id.pub file on a logical device that is exposed to
     * the instance as /dev/sda2 (the ephemeral store). The format of this file is
     * suitable for use as an entry within ~/.ssh/authorized_keys (the OpenSSH
     * format). This can be done at boot (e.g., as part of rc.local) allowing for
     * secure access without passwords.
     * Optional user data can be provided in the launch request. All instances that
     * collectively comprise the launch request have access to this data For more
     * information, see Instance Metadata.
     * Note:
     * If any of the AMIs have a product code attached for which the user has not
     * subscribed, the RunInstances call will fail.
     * Important:
     * We strongly recommend using the 2.6.18 Xen stock kernel with the c1.medium and
     * c1.xlarge instances. Although the default Amazon EC2 kernels will work, the new
     * kernels provide greater stability and performance for these instance types. For
     * more information about kernels, see Kernels, RAM Disks, and Block Device
     * Mappings.
     *   
     * @param service instance of AmazonEC2 service
     * @param request Action to invoke
     */
    public static void invokeRunInstances(AmazonEC2 service, RunInstancesRequest request) {
        try {

            RunInstancesResponse response = service.runInstances(request);

            System.out.println("RunInstances Action Response");
            System.out
                    .println("=============================================================================");
            System.out.println();

            System.out.print("    RunInstancesResponse");
            System.out.println();
            if (response.isSetResponseMetadata()) {
                System.out.print("        ResponseMetadata");
                System.out.println();
                ResponseMetadata responseMetadata = response.getResponseMetadata();
                if (responseMetadata.isSetRequestId()) {
                    System.out.print("            RequestId");
                    System.out.println();
                    System.out.print("                " + responseMetadata.getRequestId());
                    System.out.println();
                }
            }
            if (response.isSetRunInstancesResult()) {
                System.out.print("        RunInstancesResult");
                System.out.println();
                RunInstancesResult runInstancesResult = response.getRunInstancesResult();
                if (runInstancesResult.isSetReservation()) {
                    System.out.print("            Reservation");
                    System.out.println();
                    Reservation reservation = runInstancesResult.getReservation();
                    if (reservation.isSetReservationId()) {
                        System.out.print("                ReservationId");
                        System.out.println();
                        System.out.print("                    " + reservation.getReservationId());
                        System.out.println();
                    }
                    if (reservation.isSetOwnerId()) {
                        System.out.print("                OwnerId");
                        System.out.println();
                        System.out.print("                    " + reservation.getOwnerId());
                        System.out.println();
                    }
                    java.util.List<String> groupNameList = reservation.getGroupName();
                    for (String groupName : groupNameList) {
                        System.out.print("                GroupName");
                        System.out.println();
                        System.out.print("                    " + groupName);
                    }
                    java.util.List<RunningInstance> runningInstanceList = reservation.getRunningInstance();
                    for (RunningInstance runningInstance : runningInstanceList) {
                        System.out.print("                RunningInstance");
                        System.out.println();
                        if (runningInstance.isSetInstanceId()) {
                            System.out.print("                    InstanceId");
                            System.out.println();
                            System.out.print("                        " + runningInstance.getInstanceId());
                            System.out.println();
                        }
                        if (runningInstance.isSetImageId()) {
                            System.out.print("                    ImageId");
                            System.out.println();
                            System.out.print("                        " + runningInstance.getImageId());
                            System.out.println();
                        }
                        if (runningInstance.isSetInstanceState()) {
                            System.out.print("                    InstanceState");
                            System.out.println();
                            InstanceState instanceState = runningInstance.getInstanceState();
                            if (instanceState.isSetCode()) {
                                System.out.print("                        Code");
                                System.out.println();
                                System.out.print("                            " + instanceState.getCode());
                                System.out.println();
                            }
                            if (instanceState.isSetName()) {
                                System.out.print("                        Name");
                                System.out.println();
                                System.out.print("                            " + instanceState.getName());
                                System.out.println();
                            }
                        }
                        if (runningInstance.isSetPrivateDnsName()) {
                            System.out.print("                    PrivateDnsName");
                            System.out.println();
                            System.out
                                    .print("                        " + runningInstance.getPrivateDnsName());
                            System.out.println();
                        }
                        if (runningInstance.isSetPublicDnsName()) {
                            System.out.print("                    PublicDnsName");
                            System.out.println();
                            System.out.print("                        " + runningInstance.getPublicDnsName());
                            System.out.println();
                        }
                        if (runningInstance.isSetStateTransitionReason()) {
                            System.out.print("                    StateTransitionReason");
                            System.out.println();
                            System.out.print("                        " +
                                runningInstance.getStateTransitionReason());
                            System.out.println();
                        }
                        if (runningInstance.isSetKeyName()) {
                            System.out.print("                    KeyName");
                            System.out.println();
                            System.out.print("                        " + runningInstance.getKeyName());
                            System.out.println();
                        }
                        if (runningInstance.isSetAmiLaunchIndex()) {
                            System.out.print("                    AmiLaunchIndex");
                            System.out.println();
                            System.out
                                    .print("                        " + runningInstance.getAmiLaunchIndex());
                            System.out.println();
                        }
                        java.util.List<String> productCodeList = runningInstance.getProductCode();
                        for (String productCode : productCodeList) {
                            System.out.print("                    ProductCode");
                            System.out.println();
                            System.out.print("                        " + productCode);
                        }
                        if (runningInstance.isSetInstanceType()) {
                            System.out.print("                    InstanceType");
                            System.out.println();
                            System.out.print("                        " + runningInstance.getInstanceType());
                            System.out.println();
                        }
                        if (runningInstance.isSetLaunchTime()) {
                            System.out.print("                    LaunchTime");
                            System.out.println();
                            System.out.print("                        " + runningInstance.getLaunchTime());
                            System.out.println();
                        }
                        if (runningInstance.isSetPlacement()) {
                            System.out.print("                    Placement");
                            System.out.println();
                            Placement placement = runningInstance.getPlacement();
                            if (placement.isSetAvailabilityZone()) {
                                System.out.print("                        AvailabilityZone");
                                System.out.println();
                                System.out.print("                            " +
                                    placement.getAvailabilityZone());
                                System.out.println();
                            }
                        }
                        if (runningInstance.isSetKernelId()) {
                            System.out.print("                    KernelId");
                            System.out.println();
                            System.out.print("                        " + runningInstance.getKernelId());
                            System.out.println();
                        }
                        if (runningInstance.isSetRamdiskId()) {
                            System.out.print("                    RamdiskId");
                            System.out.println();
                            System.out.print("                        " + runningInstance.getRamdiskId());
                            System.out.println();
                        }
                    }
                }
            }
            System.out.println();

        } catch (AmazonEC2Exception ex) {

            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
        }
    }

}
