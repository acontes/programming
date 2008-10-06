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
 * Describe Image Attribute  Samples
 *
 *
 */
public class DescribeImageAttributeSample {

    /**
     * Just add few required parameters, and try the service
     * Describe Image Attribute functionality
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
         * sample for Describe Image Attribute 
         ***********************************************************************/
         DescribeImageAttributeRequest request = new DescribeImageAttributeRequest();
        
         // @TODO: set request parameters here

         // invokeDescribeImageAttribute(service, request);

    }


                                                                                        
    /**
     * Describe Image Attribute  request sample
     * The DescribeImageAttribute operation returns information about an attribute of
     * an AMI. Only one attribute can be specified per call.
     *   
     * @param service instance of AmazonEC2 service
     * @param request Action to invoke
     */
    public static void invokeDescribeImageAttribute(AmazonEC2 service, DescribeImageAttributeRequest request) {
        try {
            
            DescribeImageAttributeResponse response = service.describeImageAttribute(request);

            
            System.out.println ("DescribeImageAttribute Action Response");
            System.out.println ("=============================================================================");
            System.out.println ();

            System.out.print("    DescribeImageAttributeResponse");
            System.out.println();
            if (response.isSetResponseMetadata()) {
                System.out.print("        ResponseMetadata");
                System.out.println();
                ResponseMetadata  responseMetadata = response.getResponseMetadata();
                if (responseMetadata.isSetRequestId()) {
                    System.out.print("            RequestId");
                    System.out.println();
                    System.out.print("                " + responseMetadata.getRequestId());
                    System.out.println();
                }
            } 
            if (response.isSetDescribeImageAttributeResult()) {
                System.out.print("        DescribeImageAttributeResult");
                System.out.println();
                DescribeImageAttributeResult  describeImageAttributeResult = response.getDescribeImageAttributeResult();
                java.util.List<ImageAttribute> imageAttributeList = describeImageAttributeResult.getImageAttribute();
                for (ImageAttribute imageAttribute : imageAttributeList) {
                    System.out.print("            ImageAttribute");
                    System.out.println();
                    if (imageAttribute.isSetImageId()) {
                        System.out.print("                ImageId");
                        System.out.println();
                        System.out.print("                    " + imageAttribute.getImageId());
                        System.out.println();
                    }
                    if (imageAttribute.isSetLaunchPermission()) {
                        System.out.print("                LaunchPermission");
                        System.out.println();
                        LaunchPermission  launchPermission = imageAttribute.getLaunchPermission();
                        if (launchPermission.isSetUserId()) {
                            System.out.print("                    UserId");
                            System.out.println();
                            System.out.print("                        " + launchPermission.getUserId());
                            System.out.println();
                        }
                        if (launchPermission.isSetGroupName()) {
                            System.out.print("                    GroupName");
                            System.out.println();
                            System.out.print("                        " + launchPermission.getGroupName());
                            System.out.println();
                        }
                    } 
                    java.util.List<String> productCodeList  =  imageAttribute.getProductCode();
                    for (String productCode : productCodeList) { 
                        System.out.print("                ProductCode");
                            System.out.println();
                        System.out.print("                    " + productCode);
                    }	
                    if (imageAttribute.isSetKernelId()) {
                        System.out.print("                KernelId");
                        System.out.println();
                        System.out.print("                    " + imageAttribute.getKernelId());
                        System.out.println();
                    }
                    if (imageAttribute.isSetRamdiskId()) {
                        System.out.print("                RamdiskId");
                        System.out.println();
                        System.out.print("                    " + imageAttribute.getRamdiskId());
                        System.out.println();
                    }
                    if (imageAttribute.isSetBlockDeviceMapping()) {
                        System.out.print("                BlockDeviceMapping");
                        System.out.println();
                        BlockDeviceMapping  blockDeviceMapping = imageAttribute.getBlockDeviceMapping();
                        if (blockDeviceMapping.isSetVirtualName()) {
                            System.out.print("                    VirtualName");
                            System.out.println();
                            System.out.print("                        " + blockDeviceMapping.getVirtualName());
                            System.out.println();
                        }
                        if (blockDeviceMapping.isSetDeviceName()) {
                            System.out.print("                    DeviceName");
                            System.out.println();
                            System.out.print("                        " + blockDeviceMapping.getDeviceName());
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
