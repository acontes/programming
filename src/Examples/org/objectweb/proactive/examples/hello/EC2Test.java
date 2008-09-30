package org.objectweb.proactive.examples.hello;

import com.amazonaws.ec2.AmazonEC2Client;
import com.amazonaws.ec2.AmazonEC2Exception;
import com.amazonaws.ec2.model.RunInstancesRequest;

public class EC2Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        System.out.println("Trying to instanciate a RunInstancesRequest");
        
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        
        runInstancesRequest.setMinCount(1);
        runInstancesRequest.setMaxCount(1);

        System.out.println(runInstancesRequest);
        
        AmazonEC2Client service = new AmazonEC2Client("dummy", "dummy");
        
        try {
            service.runInstances(runInstancesRequest);
        } catch (AmazonEC2Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
