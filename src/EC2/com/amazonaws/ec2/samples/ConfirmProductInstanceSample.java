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
 * Confirm Product Instance  Samples
 *
 *
 */
public class ConfirmProductInstanceSample {

    /**
     * Just add few required parameters, and try the service
     * Confirm Product Instance functionality
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
         * sample for Confirm Product Instance 
         ***********************************************************************/
        ConfirmProductInstanceRequest request = new ConfirmProductInstanceRequest();

        // @TODO: set request parameters here

        // invokeConfirmProductInstance(service, request);

    }

    /**
     * Confirm Product Instance  request sample
     * The ConfirmProductInstance operation returns true if the specified product code
     * is attached to the specified instance. The operation returns false if the
     * product code is not attached to the instance.
     * The ConfirmProductInstance operation can only be executed by the owner of the
     * AMI. This feature is useful when an AMI owner is providing support and wants to
     * verify whether a user's instance is eligible.
     *   
     * @param service instance of AmazonEC2 service
     * @param request Action to invoke
     */
    public static void invokeConfirmProductInstance(AmazonEC2 service, ConfirmProductInstanceRequest request) {
        try {

            ConfirmProductInstanceResponse response = service.confirmProductInstance(request);

            System.out.println("ConfirmProductInstance Action Response");
            System.out
                    .println("=============================================================================");
            System.out.println();

            System.out.print("    ConfirmProductInstanceResponse");
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
            if (response.isSetConfirmProductInstanceResult()) {
                System.out.print("        ConfirmProductInstanceResult");
                System.out.println();
                ConfirmProductInstanceResult confirmProductInstanceResult = response
                        .getConfirmProductInstanceResult();
                if (confirmProductInstanceResult.isSetOwnerId()) {
                    System.out.print("            OwnerId");
                    System.out.println();
                    System.out.print("                " + confirmProductInstanceResult.getOwnerId());
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
