package org.objectweb.proactive.examples.documentation.webservices;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.examples.documentation.classes.B;
import org.objectweb.proactive.examples.documentation.components.A;
import org.objectweb.proactive.examples.documentation.components.AImpl;
import org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.axis2.WSConstants;


public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            //@snippet-start webservices_AO_1
            B b = (B) PAActiveObject.newActive(B.class.getName(), new Object[] {});
            //@snippet-end webservices_AO_1

            //@snippet-start webservices_Component_1
            Component boot = org.objectweb.fractal.api.Fractal.getBootstrapComponent();

            TypeFactory tf = Fractal.getTypeFactory(boot);
            GenericFactory cf = Fractal.getGenericFactory(boot);

            // type of server component
            ComponentType sType = tf.createFcType(new InterfaceType[] { tf.createFcItfType("hello-world",
                    A.class.getName(), false, false, false) });
            // create server component
            Component a = cf.newFcInstance(sType, new ControllerDescription("server", Constants.PRIMITIVE),
                    new ContentDescription(AImpl.class.getName()));
            //start the component
            Fractal.getLifeCycleController(a).startFc();
            //@snippet-end webservices_Component_1

            //@snippet-start webservices_AO_2
            HelloWorld hw = (HelloWorld) PAActiveObject
                    .newActive(HelloWorld.class.getName(), new Object[] {});

            WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, hw,
                    "http://localhost:8080/", "MyHelloWorldService", new String[] { "helloWorld" });
            //@snippet-end webservices_AO_2
        } catch (ActiveObjectCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchInterfaceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalLifeCycleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
