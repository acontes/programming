package nonregressiontest.component.collectiveitf.multicast.classbased;

import java.util.ArrayList;
import java.util.List;

import nonregressiontest.component.collectiveitf.multicast.MulticastTestItf;
import nonregressiontest.component.collectiveitf.multicast.Test;
import nonregressiontest.component.collectiveitf.multicast.Tester;
import nonregressiontest.component.collectiveitf.multicast.WrappedInteger;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.controller.MulticastBindingController;

import testsuite.test.Assertions;


public class TesterImpl implements Tester, MulticastBindingController {
    
    MulticastTestItf clientItf;

    OneToOneMulticast oneToOneMulticastClientItf = null;
    BroadcastMulticast broadcastMulticastClientItf = null;


    public void testConnectedServerMulticastItf() throws Exception {

    }

    public void testOwnClientMulticastItf() throws Exception {

        List<WrappedInteger> listParameter = new ArrayList<WrappedInteger>();
        for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
            listParameter.add(i, new WrappedInteger(i));
        }
        List<WrappedInteger> result;
        
        result = broadcastMulticastClientItf.dispatch(listParameter);
        Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
        for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
            Assertions.assertTrue(result.contains(new WrappedInteger(i)));
        }
        
        result = oneToOneMulticastClientItf.dispatch(listParameter);
        Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
        for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
            Assertions.assertTrue(result.get(i).equals(new WrappedInteger(i)));
        } 



    }

    public Object getMulticastFcItfRef(String itfName)
            throws NoSuchInterfaceException {

        if ("oneToOneMulticastClientItf".equals(itfName)) {
            return oneToOneMulticastClientItf;
        }
        if ("broadcastMulticastClientItf".equals(itfName)) {
            return broadcastMulticastClientItf;
        }
        throw new NoSuchInterfaceException(itfName);
    
    }

    public void setMulticastFcItfRef(String itfName, Object itfRef) {

        if ("oneToOneMulticastClientItf".equals (itfName) && (itfRef instanceof OneToOneMulticast)) {
            oneToOneMulticastClientItf = (OneToOneMulticast)itfRef;
        } else  
        if ("broadcastMulticastClientItf".equals (itfName) && (itfRef instanceof BroadcastMulticast)) {
            broadcastMulticastClientItf = (BroadcastMulticast)itfRef;
        } else {
        throw new ProActiveRuntimeException("cannot find multicast interface " + itfName);
        }

    }

}
