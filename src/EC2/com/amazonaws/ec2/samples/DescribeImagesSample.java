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
 * Describe Images  Samples
 *
 *
 */
public class DescribeImagesSample {

    /**
     * Just add few required parameters, and try the service
     * Describe Images functionality
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
         * sample for Describe Images 
         ***********************************************************************/
        DescribeImagesRequest request = new DescribeImagesRequest();

        // @TODO: set request parameters here

        // invokeDescribeImages(service, request);

    }

    /**
     * Describe Images  request sample
     * The DescribeImages operation returns information about AMIs, AKIs, and ARIs
     * available to the user. Information returned includes image type, product codes,
     * architecture, and kernel and RAM disk IDs. Images available to the user include
     * public images available for any user to launch, private images owned by the
     * user making the request, and private images owned by other users for which the
     * user has explicit launch permissions.
     * Launch permissions fall into three categories:
     * Public:
     * The owner of the AMI granted launch permissions for the AMI to the all group.
     * All users have launch permissions for these AMIs.
     * Explicit:
     * The owner of the AMI granted launch permissions to a specific user.
     * Implicit:
     * A user has implicit launch permissions for all AMIs he or she owns.
     * The list of AMIs returned can be modified by specifying AMI IDs, AMI owners, or
     * users with launch permissions. If no options are specified, Amazon EC2 returns
     * all AMIs for which the user has launch permissions.
     * If you specify one or more AMI IDs, only AMIs that have the specified IDs are
     * returned. If you specify an invalid AMI ID, a fault is returned. If you specify
     * an AMI ID for which you do not have access, it will not be included in the
     * returned results.
     * If you specify one or more AMI owners, only AMIs from the specified owners and
     * for which you have access are returned. The results can include the account IDs
     * of the specified owners, amazon for AMIs owned by Amazon or self for AMIs that
     * you own.
     * If you specify a list of executable users, only users that have launch
     * permissions for the AMIs are returned. You can specify account IDs (if you own
     * the AMI(s)), self for AMIs for which you own or have explicit permissions, or
     * all for public AMIs.
     * Note:
     * Deregistered images are included in the returned results for an unspecified
     * interval after deregistration.
     *   
     * @param service instance of AmazonEC2 service
     * @param request Action to invoke
     */
    public static void invokeDescribeImages(AmazonEC2 service, DescribeImagesRequest request) {
        try {

            DescribeImagesResponse response = service.describeImages(request);

            System.out.println("DescribeImages Action Response");
            System.out
                    .println("=============================================================================");
            System.out.println();

            System.out.print("    DescribeImagesResponse");
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
            if (response.isSetDescribeImagesResult()) {
                System.out.print("        DescribeImagesResult");
                System.out.println();
                DescribeImagesResult describeImagesResult = response.getDescribeImagesResult();
                java.util.List<Image> imageList = describeImagesResult.getImage();
                for (Image image : imageList) {
                    System.out.print("            Image");
                    System.out.println();
                    if (image.isSetImageId()) {
                        System.out.print("                ImageId");
                        System.out.println();
                        System.out.print("                    " + image.getImageId());
                        System.out.println();
                    }
                    if (image.isSetImageLocation()) {
                        System.out.print("                ImageLocation");
                        System.out.println();
                        System.out.print("                    " + image.getImageLocation());
                        System.out.println();
                    }
                    if (image.isSetImageState()) {
                        System.out.print("                ImageState");
                        System.out.println();
                        System.out.print("                    " + image.getImageState());
                        System.out.println();
                    }
                    if (image.isSetOwnerId()) {
                        System.out.print("                OwnerId");
                        System.out.println();
                        System.out.print("                    " + image.getOwnerId());
                        System.out.println();
                    }
                    if (image.isSetVisibility()) {
                        System.out.print("                Visibility");
                        System.out.println();
                        System.out.print("                    " + image.getVisibility());
                        System.out.println();
                    }
                    java.util.List<String> productCodeList = image.getProductCode();
                    for (String productCode : productCodeList) {
                        System.out.print("                ProductCode");
                        System.out.println();
                        System.out.print("                    " + productCode);
                    }
                    if (image.isSetArchitecture()) {
                        System.out.print("                Architecture");
                        System.out.println();
                        System.out.print("                    " + image.getArchitecture());
                        System.out.println();
                    }
                    if (image.isSetImageType()) {
                        System.out.print("                ImageType");
                        System.out.println();
                        System.out.print("                    " + image.getImageType());
                        System.out.println();
                    }
                    if (image.isSetKernelId()) {
                        System.out.print("                KernelId");
                        System.out.println();
                        System.out.print("                    " + image.getKernelId());
                        System.out.println();
                    }
                    if (image.isSetRamdiskId()) {
                        System.out.print("                RamdiskId");
                        System.out.println();
                        System.out.print("                    " + image.getRamdiskId());
                        System.out.println();
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
