package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group;

import org.objectweb.proactive.extensions.gcmdeployment.PathElement;

import com.amazonaws.ec2.AmazonEC2;
import com.amazonaws.ec2.AmazonEC2Exception;
import com.amazonaws.ec2.mock.AmazonEC2Mock;
import com.amazonaws.ec2.model.RunInstancesRequest;
import com.amazonaws.ec2.model.RunInstancesResponse;


public class GroupEC2 extends AbstractJavaGroup {

    public static final String EC2_NAMESPACE = "http://ec2.amazonaws.com/doc/2008-05-05";
    private PathElement privateKey;
    private PathElement certification;
    private String imageName;
    private String accessKeyId;
    private String secretAccessKey;

    //    @Override
    //    public List<String> internalBuildCommands() {
    //        List<String> res = new ArrayList<String>();
    //        
    //        StringBuffer command = new StringBuffer();
    //        
    //        command.append(getCommandPath());
    //        command.append(" ec2-run-instances ");
    //        command.append(imageName);
    //        // TODO - or use soap API ?
    //        res.add(command.toString());
    //        return res;
    //    }

    static protected class EC2InstanceRunner implements Runnable {
        AmazonEC2 service;
        private RunInstancesRequest request;
        private RunInstancesResponse response;

        public EC2InstanceRunner(String accessKeyId, String secretAccessKey, RunInstancesRequest request) {
            //            service = new AmazonEC2Client(accessKeyId, secretAccessKey);
            service = new AmazonEC2Mock();
            this.request = request;
            this.response = null;
        }

        public void run() {

            try {
                response = service.runInstances(request);
            } catch (AmazonEC2Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public RunInstancesResponse getResponse() {
            return response;
        }
    }

    @Override
    public Runnable buildJavaJob() {

//        String accessKeyId = "1K6JVRSE482QZZ8N7302";
//        String secretAccessKey = "QUItsO2PiZoFATBv6wdFFeyst3LIO+0l4XqtfaS5";

        RunInstancesRequest request = new RunInstancesRequest();

        StringBuffer userData = new StringBuffer();

        request.setUserData(userData.toString());

        EC2InstanceRunner instanceRunner = new EC2InstanceRunner(accessKeyId, secretAccessKey, request);
        
        return new Thread(instanceRunner);
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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
