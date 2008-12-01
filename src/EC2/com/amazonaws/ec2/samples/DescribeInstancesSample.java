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
 * Describe Instances  Samples
 *
 *
 */
public class DescribeInstancesSample {

    /**
     * Just add few required parameters, and try the service
     * Describe Instances functionality
     *
     * @param args unused
     */
    public static void main(String... args) {

        /************************************************************************
         * Access Key ID and Secret Acess Key ID, obtained from:
         * http://aws.amazon.com
         ***********************************************************************/
        String accessKeyId = "<Your Access Key ID>";
        String secretAccessKey = "<Your Secret Access Key>";

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
         * sample for Describe Instances 
         ***********************************************************************/
        DescribeInstancesRequest request = new DescribeInstancesRequest();

        // @TODO: set request parameters here

        // invokeDescribeInstances(service, request);

    }

    /**
     * Describe Instances  request sample
     * The DescribeInstances operation returns information about instances that you
     * own.
     * If you specify one or more instance IDs, Amazon EC2 returns information for
     * those instances. If you do not specify instance IDs, Amazon EC2 returns
     * information for all relevant instances. If you specify an invalid instance ID,
     * a fault is returned. If you specify an instance that you do not own, it will
     * not be included in the returned results.
     * Recently terminated instances might appear in the returned results. This
     * interval is usually less than one hour.
     *   
     * @param service instance of AmazonEC2 service
     * @param request Action to invoke
     */
    public static void invokeDescribeInstances(AmazonEC2 service, DescribeInstancesRequest request) {
        try {

            DescribeInstancesResponse response = service.describeInstances(request);

            System.out.println("DescribeInstances Action Response");
            System.out
                    .println("=============================================================================");
            System.out.println();

            System.out.print("    DescribeInstancesResponse");
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
            if (response.isSetDescribeInstancesResult()) {
                System.out.print("        DescribeInstancesResult");
                System.out.println();
                DescribeInstancesResult describeInstancesResult = response.getDescribeInstancesResult();
                java.util.List<Reservation> reservationList = describeInstancesResult.getReservation();
                for (Reservation reservation : reservationList) {
                    System.out.print("            Reservation");
                    System.out.println();
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
