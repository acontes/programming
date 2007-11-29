package org.objectweb.proactive.compi.control;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.node.NodeException;

/**
 * User: emathias
 * Date: Apr 22, 2007
 * Time: 4:00:16 AM
 */
public class ProActiveMPINodeComp extends ProActiveMPINodeImpl implements ProActiveMPINode, BindingController {

    ProActiveMPICluster clusterItf;

    public ProActiveMPINodeComp() {
    }

    public ProActiveMPINodeComp(String libName, Integer jobNum) throws IllegalAccessException, ActiveObjectCreationException, InstantiationException, ClassNotFoundException, NodeException {
        super(libName, jobNum);
    }

    public String[] listFc() {
        return new String[]{"node2cluster"};
    }

    public Object lookupFc(String itfName) throws NoSuchInterfaceException {
        if (itfName.equals("node2cluster")) {
            return clusterItf;
        }
        return null;
    }

    public void bindFc(String itfName, Object itf) throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException {
        if (itfName.equals("node2cluster")) {
            this.clusterItf = (ProActiveMPICluster) itf;
            this.setManager((ProActiveMPICluster) itf);
        }
    }

    public void unbindFc(String itfName) throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException {
        if (itfName.equals("node2cluster")) {
            clusterItf = null;
        }
    }

}
