package functionalTests.structuredp2p.can;

import java.io.Serializable;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


public class AwareObject implements RunActive, Serializable {

    public Peer remotePeer;

    public AwareObject() {

    }

    public void callMe() {
        System.out.println("Oh oh oh, Iam running !");
    }

    public void runActivity(Body body) {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.callMe();
        }
    }

    public Body getBody() {
        return PAActiveObject.getBodyOnThis();
    }

    public void setPeer(Peer peer) {
        this.remotePeer = peer;
    }
}