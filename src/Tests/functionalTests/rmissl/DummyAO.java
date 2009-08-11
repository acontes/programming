package functionalTests.rmissl;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;


public class DummyAO implements Serializable, InitActive {

    public DummyAO() {

    }

    public String sayHello(String arg) {
        return arg;
    }

    public void initActivity(Body body) {
        try {

            RemoteObjectExposer<UniversalBody> roe = ((AbstractBody) body).getRemoteObjectExposer();
            System.out.println("DummyAO.initActivity() Unregister all protocols");
            roe.unregisterAll();

            try {
                System.out.println("DummyAO.initActivity() create SSL remote Object");
                roe.createRemoteObject(new URI("rmissl://" +
                    PAActiveObject.getNode().getVMInformation().getHostName() + ":" +
                    PAProperties.PA_RMI_PORT.getValue() + "/" + DummyAO.class.getName()));
                System.out.println("DummyAO.initActivity() created SSL remote Object");
            } catch (URISyntaxException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                roe.unregisterAll();
            } catch (ProActiveException e) {
                // see PROACTIVE-416
                e.printStackTrace();
            }
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
