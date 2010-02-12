/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.nonfunctional.membranecontroller.bindnfc.components.internalserver;

import java.util.HashMap;
import java.util.Map;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Type;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.controller.PAMembraneController;
import org.objectweb.proactive.core.component.factory.ProActiveGenericFactory;
import org.objectweb.proactive.core.component.representative.ProActiveComponentRepresentative;
import org.objectweb.proactive.core.component.type.ProActiveGCMTypeFactory;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;

import functionalTests.ComponentTest;
import functionalTests.component.creation.ComponentA;
import functionalTests.component.creation.ComponentInfo;
import functionalTests.component.nonfunctional.creation.DummyControllerItf;


/**
 * @author The ProActive Team
 *
 *Testing non-functional bindings
 */
public class Test extends ComponentTest {
    Component componentA;
    String name;
    String nodeUrl;

    public Test() {
        super(
                "Binds the non-functional internal server interface of composite component to a component inside the membrane",
                "Binds the non-functional internal server interface of composite component to a component inside the membrane");
    }

    @org.junit.Test
    public void action() throws Exception {
        //Thread.sleep(2000);
        Component boot = Fractal.getBootstrapComponent(); /*Getting the Fractal-Proactive bootstrap component*/
        TypeFactory type_factory = Fractal.getTypeFactory(boot); /*Getting the Fractal-ProActive type factory*/
        ProActiveGenericFactory cf = Fractive.getGenericFactory(boot); /*Getting the Fractal-ProActive generic factory*/

        Type fType = type_factory.createFcType(new InterfaceType[] { type_factory.createFcItfType(
                "componentInfo", ComponentInfo.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                TypeFactory.SINGLE), });

        Type nfType = type_factory
                .createFcType(new InterfaceType[] {
                        type_factory
                                .createFcItfType(
                                        Constants.BINDING_CONTROLLER,
                                        /*BINDING CONTROLLER*/org.objectweb.proactive.core.component.controller.PABindingController.class
                                                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                                        TypeFactory.SINGLE),
                        type_factory
                                .createFcItfType(
                                        Constants.CONTENT_CONTROLLER,
                                        /*CONTENT CONTROLLER*/org.objectweb.proactive.core.component.controller.PAContentController.class
                                                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                                        TypeFactory.SINGLE),
                        type_factory
                                .createFcItfType(
                                        Constants.LIFECYCLE_CONTROLLER,
                                        /*LIFECYCLE CONTROLLER*/org.objectweb.proactive.core.component.controller.PAGCMLifeCycleController.class
                                                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                                        TypeFactory.SINGLE),
                        type_factory
                                .createFcItfType(
                                        Constants.SUPER_CONTROLLER,
                                        /*SUPER CONTROLLER*/org.objectweb.proactive.core.component.controller.PASuperController.class
                                                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                                        TypeFactory.SINGLE),
                        type_factory.createFcItfType(Constants.NAME_CONTROLLER,
                        /*NAME CONTROLLER*/org.objectweb.fractal.api.control.NameController.class.getName(),
                                TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE),

                        type_factory.createFcItfType(Constants.MEMBRANE_CONTROLLER,
                        /*MEMBRANE CONTROLLER*/PAMembraneController.class.getName(), TypeFactory.SERVER,
                                TypeFactory.MANDATORY, TypeFactory.SINGLE),
                        type_factory
                                .createFcItfType(
                                        "dummy-controller",
                                        /*DUMMY CONTROLLER*/functionalTests.component.nonfunctional.creation.DummyControllerItf.class
                                                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                                        TypeFactory.SINGLE),
                        ((ProActiveGCMTypeFactory) type_factory)
                                .createGCMItfType(
                                        "dummy-internal-client-controller",
                                        /*DUMMY CONTROLLER*/functionalTests.component.nonfunctional.creation.DummyControllerItf.class
                                                .getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY,
                                        GCMTypeFactory.SINGLETON_CARDINALITY,
                                        ProActiveGCMTypeFactory.INTERNAL),
                        ((ProActiveGCMTypeFactory) type_factory)
                                .createGCMItfType(
                                        "dummy-internal-server-controller",
                                        /*DUMMY CONTROLLER*/functionalTests.component.nonfunctional.creation.DummyControllerItf.class
                                                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                                        GCMTypeFactory.SINGLETON_CARDINALITY,
                                        ProActiveGCMTypeFactory.INTERNAL) });

        /************************************NF type for componentB****************/
        Type nfTypeB = type_factory
                .createFcType(new InterfaceType[] {
                        type_factory
                                .createFcItfType(
                                        Constants.SUPER_CONTROLLER,
                                        /*SUPER CONTROLLER*/org.objectweb.proactive.core.component.controller.PASuperController.class
                                                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                                        TypeFactory.SINGLE),
                        type_factory.createFcItfType(Constants.NAME_CONTROLLER,
                        /*NAME CONTROLLER*/org.objectweb.fractal.api.control.NameController.class.getName(),
                                TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE),

                        type_factory.createFcItfType(Constants.MEMBRANE_CONTROLLER,
                        /*MEMBRANE CONTROLLER*/PAMembraneController.class.getName(), TypeFactory.SERVER,
                                TypeFactory.MANDATORY, TypeFactory.SINGLE),
                        type_factory
                                .createFcItfType(
                                        "dummy-controller",
                                        /*DUMMY CONTROLLER*/functionalTests.component.nonfunctional.creation.DummyControllerItf.class
                                                .getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                                        TypeFactory.SINGLE),
                        type_factory
                                .createFcItfType(
                                        "dummy-client-controller",
                                        /*DUMMY CONTROLLER*/functionalTests.component.nonfunctional.creation.DummyControllerItf.class
                                                .getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY,
                                        TypeFactory.SINGLE), });
        /************************************NF type for componentB****************/

        componentA = cf.newFcInstance(fType, nfType, (ContentDescription) null,//The component is composite
                new ControllerDescription("componentA", Constants.COMPOSITE, !Constants.SYNCHRONOUS,
                    Constants.WITHOUT_CONFIG_FILE), (Node) null);

        //Filling the membrane with object  controllers
        PAMembraneController memController = Fractive.getMembraneController(componentA);

        memController.setControllerObject(Constants.BINDING_CONTROLLER,
                org.objectweb.proactive.core.component.controller.PABindingControllerImpl.class.getName());
        memController.setControllerObject(Constants.CONTENT_CONTROLLER,
                org.objectweb.proactive.core.component.controller.PAContentControllerImpl.class.getName());
        memController.setControllerObject(Constants.SUPER_CONTROLLER,
                org.objectweb.proactive.core.component.controller.PASuperControllerImpl.class.getName());
        memController.setControllerObject(Constants.NAME_CONTROLLER,
                org.objectweb.proactive.core.component.controller.PANameController.class.getName());

        Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getNFFactory();
        Map context = new HashMap();
        Component dummyMaster = (Component) f.newComponent(
                "functionalTests.component.nonfunctional.adl.dummyMaster", context);
        Fractal.getNameController(dummyMaster).setFcName("dummyMaster");
        //Including the dummyMaster component inside the membrane of componentA
        memController.addNFSubComponent(dummyMaster);
        memController.bindNFc("dummy-controller", "dummyMaster.dummy-master");
        memController.bindNFc("dummyMaster.dummy-client", "dummy-internal-client-controller");

        Component componentB = cf.newFcInstance(fType, nfTypeB, new ContentDescription(ComponentA.class
                .getName(), new Object[] { "tata" }), new ControllerDescription("componentB",
            Constants.PRIMITIVE, !Constants.SYNCHRONOUS, Constants.WITHOUT_CONFIG_FILE), (Node) null);

        PAMembraneController memControllerB = Fractive.getMembraneController(componentB);
        memControllerB.setControllerObject(Constants.SUPER_CONTROLLER,
                org.objectweb.proactive.core.component.controller.PASuperControllerImpl.class.getName());

        Component dummyMaster2 = (Component) f.newComponent(
                "functionalTests.component.nonfunctional.adl.dummyMaster", context);
        Fractal.getNameController(dummyMaster2).setFcName("dummyMaster");

        Component dummyController = (Component) f.newComponent(
                "functionalTests.component.nonfunctional.adl.dummyPrimitive", context);
        Fractal.getNameController(dummyController).setFcName("dummyPrimitive");

        //Including the dummyMaster component inside the membrane of componentB
        memControllerB.addNFSubComponent(dummyMaster2);
        memControllerB.bindNFc("dummy-controller", "dummyMaster.dummy-master");
        memControllerB.bindNFc("dummyMaster.dummy-client", "dummy-client-controller");
        memController.addNFSubComponent(dummyController);
        memController.bindNFc("dummy-internal-server-controller", "dummyPrimitive.dummy-membrane");

        Fractal.getContentController(componentA).addFcSubComponent(componentB);

        memController.bindNFc("dummy-internal-client-controller", "componentB.dummy-controller");
        memController.bindNFc("componentB.dummy-client-controller", "dummy-internal-server-controller");

        memController.startMembrane();//Starting the two membranes
        memControllerB.startMembrane();
        Object itf = memController.lookupNFc("dummyMaster.dummy-client");
        System.out.println("Lookup on the dumyMaster " + itf);
        itf = memController.lookupNFc("dummy-internal-client-controller");
        System.out.println("Lookup on the internal Nf client: " + itf);
        itf = memController.lookupNFc("componentB.dummy-client-controller");
        System.out.println("Lookup on the external Nf client of componentB: " + itf);
        DummyControllerItf dummyControl = (DummyControllerItf) componentA.getFcInterface("dummy-controller");
        //System.out.println("Dummy void method : " + dummyControl.dummyMethodWithResult()); ATTENTION!! This method generates a deadlock!!
        IntWrapper res = dummyControl.result(new IntWrapper(4));//This works because the call is ASYNCHRONOUS
        System.out.println(" Message with return value : " + res.intValue());

    }

    /**
     * @see testsuite.test.AbstractTest#initTest()
     */
    public void initTest() throws Exception {
    }

    /**
     * @see testsuite.test.AbstractTest#endTest()
     */
    public void endTest() throws Exception {
        Fractal.getLifeCycleController(componentA).stopFc();
    }

    public boolean postConditions() throws Exception {
        return (componentA instanceof ProActiveComponentRepresentative);
    }
}
