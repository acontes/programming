package org.objectweb.proactive.examples.components.reconfiguration.helloworld;



import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;

import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.reconfiguration.ProActiveReconfigurationController;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;



public class FcTestProActiveModel {
static ProActiveDescriptor deploymentDescriptor;
	
	public static void main(final String[] args) throws Exception {
		
		 Component boot = org.objectweb.fractal.api.Fractal.getBootstrapComponent();
         TypeFactory tf = Fractal.getTypeFactory(boot);
         Component rComp = null;

         // type of root component
         ComponentType rType = tf.createFcType(new InterfaceType[] { tf.createFcItfType("r",
                 Runnable.class.getName(),false, false, false) });

         // type of client component
         ComponentType cType = tf.createFcType(new InterfaceType[] {
                 tf.createFcItfType("r", Runnable.class.getName(), false, false, false),
                 tf.createFcItfType("s", Service.class.getName(), true, false, false) });

         // type of server component
         ComponentType sType = tf.createFcType(new InterfaceType[] {
                 tf.createFcItfType("s", Service.class.getName(), false, false, false),
                 tf.createFcItfType("attribute-controller", ServiceAttributes.class.getName(), false,
                         false, false) });

         GenericFactory cf = Fractal.getGenericFactory(boot);
         String configFile =  System.getProperty("user.dir") +
         		"/src/Examples/org/objectweb/proactive/examples/components/reconfiguration/helloworld/config.xml";
         rComp = cf.newFcInstance(rType, 
        		 	new ControllerDescription("root",
        		 				Constants.COMPOSITE, 
        		 				configFile), 
	 				null);
             // create client component
//         Component cComp = cf.newFcInstance(cType, new ControllerDescription("client",
//                 Constants.PRIMITIVE), new ContentDescription(ClientImpl.class.getName()));
//         
         Component cComp = cf.newFcInstance(cType, new ControllerDescription("client",
                 Constants.PRIMITIVE,configFile), 
 		         new ContentDescription(ClientImpl.class.getName())); // other properties could be added (activity for example)

             // create server component
//         Component sComp = cf.newFcInstance(sType, new ControllerDescription("server",
//                 Constants.PRIMITIVE), new ContentDescription(ServerImpl.class.getName()));
         Component sComp = cf.newFcInstance(sType, new ControllerDescription("server",
        		 Constants.PRIMITIVE,configFile), 
        		 new ContentDescription(ServerImpl.class.getName()));
         
         ((ServiceAttributes) Fractal.getAttributeController(sComp)).setHeader("--------> ");
         ((ServiceAttributes) Fractal.getAttributeController(sComp)).setCount(1);

       

             // component assembly
         Fractal.getContentController(rComp).addFcSubComponent(cComp);
         Fractal.getContentController(rComp).addFcSubComponent(sComp);
         Fractal.getBindingController(rComp).bindFc("r", cComp.getFcInterface("r"));
         Fractal.getBindingController(cComp).bindFc("s", sComp.getFcInterface("s"));

         /* Creating sub component having client and server primitive component*/
      // type of root component
         ComponentType srType = tf.createFcType(new InterfaceType[] { tf.createFcItfType("r",
                 Runnable.class.getName(),false, false, false) });

         // type of client component
         ComponentType scType = tf.createFcType(new InterfaceType[] {
                 tf.createFcItfType("r", Runnable.class.getName(), false, false, false),
                 tf.createFcItfType("s", Service.class.getName(), true, false, false) });

         // type of server component
         ComponentType ssType = tf.createFcType(new InterfaceType[] {
                 tf.createFcItfType("s", Service.class.getName(), false, false, false),
                 tf.createFcItfType("attribute-controller", ServiceAttributes.class.getName(), false,
                         false, false) });
         
         Component srComp = cf.newFcInstance(srType, 
     		 	new ControllerDescription("subRoot",
     		 				Constants.COMPOSITE, 
     		 				configFile), 
	 				null);
         Component scComp = cf.newFcInstance(scType, new ControllerDescription("client",
                 Constants.PRIMITIVE,configFile), 
 		         new ContentDescription(ClientImpl.class.getName())); // other properties could be added (activity for example)

             // create server component
//         Component sComp = cf.newFcInstance(sType, new ControllerDescription("server",
//                 Constants.PRIMITIVE), new ContentDescription(ServerImpl.class.getName()));
         Component ssComp = cf.newFcInstance(ssType, new ControllerDescription("server",
        		 Constants.PRIMITIVE,configFile), 
        		 new ContentDescription(ServerImpl.class.getName()));
         
         ((ServiceAttributes) Fractal.getAttributeController(ssComp)).setHeader("--------> ");
         ((ServiceAttributes) Fractal.getAttributeController(ssComp)).setCount(1);

       

             // component assembly
         Fractal.getContentController(srComp).addFcSubComponent(scComp);
         Fractal.getContentController(srComp).addFcSubComponent(ssComp);
         Fractal.getBindingController(srComp).bindFc("r", scComp.getFcInterface("r"));
         Fractal.getBindingController(scComp).bindFc("s", ssComp.getFcInterface("s"));

         Fractal.getContentController(rComp).addFcSubComponent(srComp);
         
         // start root component
         //Fractal.getLifeCycleController(rComp).startFc();

         // call main method
         //((Runnable) rComp.getFcInterface("r")).run();
         
         ProActiveReconfigurationController rc = (ProActiveReconfigurationController)
         rComp.getFcInterface(ProActiveReconfigurationController.RECONFIGURATION_CONTROLLER_NAME);
         
         
         ProActiveReconfigurationController rc2 = (ProActiveReconfigurationController)
         cComp.getFcInterface(ProActiveReconfigurationController.RECONFIGURATION_CONTROLLER_NAME);
 
         ProActiveReconfigurationController rc3 = (ProActiveReconfigurationController)
         sComp.getFcInterface(ProActiveReconfigurationController.RECONFIGURATION_CONTROLLER_NAME);
         
         
         ProActiveReconfigurationController rc4 = (ProActiveReconfigurationController)
         srComp.getFcInterface(ProActiveReconfigurationController.RECONFIGURATION_CONTROLLER_NAME);
         
         
         ProActiveReconfigurationController rc41 = (ProActiveReconfigurationController)
         scComp.getFcInterface(ProActiveReconfigurationController.RECONFIGURATION_CONTROLLER_NAME);
 
         ProActiveReconfigurationController rc42 = (ProActiveReconfigurationController)
         ssComp.getFcInterface(ProActiveReconfigurationController.RECONFIGURATION_CONTROLLER_NAME);
         
                
         rc.setInterpreter("org.objectweb.proactive.core.component.reconfiguration.FScriptInterpreterForProActive");
         
         rc2.setInterpreter("org.objectweb.proactive.core.component.reconfiguration.FScriptInterpreterForProActive");
         
         rc3.setInterpreter("org.objectweb.proactive.core.component.reconfiguration.FScriptInterpreterForProActive");

         rc4.setInterpreter("org.objectweb.proactive.core.component.reconfiguration.FScriptInterpreterForProActive");

         rc41.setInterpreter("org.objectweb.proactive.core.component.reconfiguration.FScriptInterpreterForProActive");

         rc42.setInterpreter("org.objectweb.proactive.core.component.reconfiguration.FScriptInterpreterForProActive");
       
         
         String scriptFile;
         scriptFile = System.getProperty("user.dir")+
  					  "/src/Examples/org/objectweb/proactive/examples/components/reconfiguration/helloworld/" +
  					  "/reconfigureRoot.fscript";
         rc.loadScript(scriptFile);
         rc4.loadScript(scriptFile);
         
         //Fractal.getLifeCycleController(rComp).stopFc();
         //rc.createVariable("owner", rComp);
//         rc.createVariable("client", cComp);
//         rc.createVariable("client", cComp);
         System.out.println("executing reconfigure($root)...");
         rc.executeScript("reconfigure($root);");  
     }
		
}
