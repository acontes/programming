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
 * Create Volume  Samples
 *
 *
 */
public class CreateVolumeSample {

    /**
     * Just add few required parameters, and try the service
     * Create Volume functionality
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
         * sample for Create Volume 
         ***********************************************************************/
        CreateVolumeRequest request = new CreateVolumeRequest();

        // @TODO: set request parameters here

        // invokeCreateVolume(service, request);

    }

    /**
     * Create Volume  request sample
     * Initializes an empty volume of a given size
     *   
     * @param service instance of AmazonEC2 service
     * @param request Action to invoke
     */
    public static void invokeCreateVolume(AmazonEC2 service, CreateVolumeRequest request) {
        try {

            CreateVolumeResponse response = service.createVolume(request);

            System.out.println("CreateVolume Action Response");
            System.out
                    .println("=============================================================================");
            System.out.println();

            System.out.print("    CreateVolumeResponse");
            System.out.println();
            if (response.isSetCreateVolumeResult()) {
                System.out.print("        CreateVolumeResult");
                System.out.println();
                CreateVolumeResult createVolumeResult = response.getCreateVolumeResult();
                if (createVolumeResult.isSetVolume()) {
                    System.out.print("            Volume");
                    System.out.println();
                    Volume volume = createVolumeResult.getVolume();
                    if (volume.isSetVolumeId()) {
                        System.out.print("                VolumeId");
                        System.out.println();
                        System.out.print("                    " + volume.getVolumeId());
                        System.out.println();
                    }
                    if (volume.isSetSize()) {
                        System.out.print("                Size");
                        System.out.println();
                        System.out.print("                    " + volume.getSize());
                        System.out.println();
                    }
                    if (volume.isSetSnapshotId()) {
                        System.out.print("                SnapshotId");
                        System.out.println();
                        System.out.print("                    " + volume.getSnapshotId());
                        System.out.println();
                    }
                    if (volume.isSetZone()) {
                        System.out.print("                Zone");
                        System.out.println();
                        System.out.print("                    " + volume.getZone());
                        System.out.println();
                    }
                    if (volume.isSetStatus()) {
                        System.out.print("                Status");
                        System.out.println();
                        System.out.print("                    " + volume.getStatus());
                        System.out.println();
                    }
                    if (volume.isSetCreateTime()) {
                        System.out.print("                CreateTime");
                        System.out.println();
                        System.out.print("                    " + volume.getCreateTime());
                        System.out.println();
                    }
                    java.util.List<Attachment> attachmentList = volume.getAttachment();
                    for (Attachment attachment : attachmentList) {
                        System.out.print("                Attachment");
                        System.out.println();
                        if (attachment.isSetVolumeId()) {
                            System.out.print("                    VolumeId");
                            System.out.println();
                            System.out.print("                        " + attachment.getVolumeId());
                            System.out.println();
                        }
                        if (attachment.isSetInstanceId()) {
                            System.out.print("                    InstanceId");
                            System.out.println();
                            System.out.print("                        " + attachment.getInstanceId());
                            System.out.println();
                        }
                        if (attachment.isSetDevice()) {
                            System.out.print("                    Device");
                            System.out.println();
                            System.out.print("                        " + attachment.getDevice());
                            System.out.println();
                        }
                        if (attachment.isSetStatus()) {
                            System.out.print("                    Status");
                            System.out.println();
                            System.out.print("                        " + attachment.getStatus());
                            System.out.println();
                        }
                        if (attachment.isSetAttachTime()) {
                            System.out.print("                    AttachTime");
                            System.out.println();
                            System.out.print("                        " + attachment.getAttachTime());
                            System.out.println();
                        }
                    }
                }
            }
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
