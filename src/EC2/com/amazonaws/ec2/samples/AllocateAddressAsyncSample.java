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
 * Allocate Address  Samples
 *
 *
 */
public class AllocateAddressAsyncSample {

    /**
     * Just add few required parameters, and try the service
     * Allocate Address functionality
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
        AllocateAddressRequest requestOne = new AllocateAddressRequest();
        // @TODO: set request parameters here

        AllocateAddressRequest requestTwo = new AllocateAddressRequest();
        // @TODO: set second request parameters here

        List<AllocateAddressRequest> requests = new ArrayList<AllocateAddressRequest>();
        requests.add(requestOne);
        requests.add(requestTwo);

        // invokeAllocateAddress(service, requests);

    }

    /**
     * Allocate Address request sample
     * The AllocateAddress operation acquires an elastic IP address for use with your
     * account.
     *   
     * @param service instance of AmazonEC2 service
     * @param requests list of requests to process
     */
    public static void invokeAllocateAddress(AmazonEC2 service, List<AllocateAddressRequest> requests) {
        List<Future<AllocateAddressResponse>> responses = new ArrayList<Future<AllocateAddressResponse>>();
        for (AllocateAddressRequest request : requests) {
            responses.add(service.allocateAddressAsync(request));
        }
        for (Future<AllocateAddressResponse> future : responses) {
            while (!future.isDone()) {
                Thread.yield();
            }
            try {
                AllocateAddressResponse response = future.get();
                // Original request corresponding to this response, if needed:
                AllocateAddressRequest originalRequest = requests.get(responses.indexOf(future));
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
