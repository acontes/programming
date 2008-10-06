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
import java.util.concurrent.Future;

/**
 *
 * Confirm Product Instance  Samples
 *
 *
 */
public class ConfirmProductInstanceAsyncSample {

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
         * Last argument (35) in following constructor is number of threads client should
         * spawn for processing.
         *
         ***********************************************************************/
         AmazonEC2 service = new AmazonEC2Client(accessKeyId, secretAccessKey, 35);

        /************************************************************************
         * Setup requests parameters and invoke parallel processing. Of course
         * in real world application, there will be much more than a couple of
         * requests to process.
         ***********************************************************************/
         ConfirmProductInstanceRequest requestOne = new ConfirmProductInstanceRequest();
         // @TODO: set request parameters here

         ConfirmProductInstanceRequest requestTwo = new ConfirmProductInstanceRequest();
         // @TODO: set second request parameters here

         List<ConfirmProductInstanceRequest> requests = new ArrayList<ConfirmProductInstanceRequest>();
         requests.add(requestOne);
         requests.add(requestTwo);

         // invokeConfirmProductInstance(service, requests);

    }


                                        
    /**
     * Confirm Product Instance request sample
     * The ConfirmProductInstance operation returns true if the specified product code
     * is attached to the specified instance. The operation returns false if the
     * product code is not attached to the instance.
     * The ConfirmProductInstance operation can only be executed by the owner of the
     * AMI. This feature is useful when an AMI owner is providing support and wants to
     * verify whether a user's instance is eligible.
     *   
     * @param service instance of AmazonEC2 service
     * @param requests list of requests to process
     */
    public static void invokeConfirmProductInstance(AmazonEC2 service, List<ConfirmProductInstanceRequest> requests) {
        List<Future<ConfirmProductInstanceResponse>> responses = new ArrayList<Future<ConfirmProductInstanceResponse>>();
        for (ConfirmProductInstanceRequest request : requests) {
            responses.add(service.confirmProductInstanceAsync(request));
        }
        for (Future<ConfirmProductInstanceResponse> future : responses) {
            while (!future.isDone()) {
                Thread.yield();
            }
            try {
                ConfirmProductInstanceResponse response = future.get();
                // Original request corresponding to this response, if needed:
                ConfirmProductInstanceRequest originalRequest = requests.get(responses.indexOf(future));
                System.out.println("Response request id: " + response.getResponseMetadata().getRequestId());
            } catch (Exception e) {
                if (e.getCause() instanceof AmazonEC2Exception) {
                    AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e.getCause());
                    System.out.println("Caught Exception: " + exception.getMessage());
                    System.out.println("Response Status Code: " + exception.getStatusCode());
                    System.out.println("Error Code: " + exception.getErrorCode());
                    System.out.println("Error Type: " + exception.getErrorType());
                    System.out.println("Request ID: " + exception.getRequestId());
                    System.out.print("XML: " + exception.getXML());
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
                                                                                                                        
}
