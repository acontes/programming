package temp;



import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;

import org.objectweb.proactive.core.body.ActiveBody;
import org.objectweb.proactive.core.body.proxy.UniversalBodyProxy;
import org.objectweb.proactive.core.body.request.BlockingRequestQueue;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;

import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.reconfiguration.tagrequest.ProActiveAOPLikeController;
import org.objectweb.proactive.core.component.representative.ProActiveComponentRepresentative;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import temp.ClientImpl;
import temp.ServerImpl;
import temp.Service;
import temp.ServiceAttributes;


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
         
         ComponentType srType = tf.createFcType(new InterfaceType[] {
                 tf.createFcItfType("r", Runnable.class.getName(), true, false, false),
                 tf.createFcItfType("s", Service.class.getName(), false, false, false) });

         GenericFactory cf = Fractal.getGenericFactory(boot);
         String configFile =  System.getProperty("user.dir") + "/src/Tests/temp/config.xml";
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
         
//         Component sComp = cf.newFcInstance(sType, new ControllerDescription("server",
//        		 Constants.PRIMITIVE,configFile), 
//        		 new ContentDescription(ServerImpl.class.getName()));
//         
         Component srComp = cf.newFcInstance(srType, new ControllerDescription("connectionSR",
        		 Constants.PRIMITIVE,configFile), 
        		 new ContentDescription(ConnectionImpl.class.getName()));
         Component sreComp = cf.newFcInstance(srType, new ControllerDescription("connectionSRExtra",
        		 Constants.PRIMITIVE,configFile), 
        		 new ContentDescription(ConnectionImpl.class.getName()));
         
         Component sre1Comp = cf.newFcInstance(srType, new ControllerDescription("connectionSRExtra1",
        		 Constants.PRIMITIVE,configFile), 
        		 new ContentDescription(ConnectionImpl.class.getName()));
         Component sre2Comp = cf.newFcInstance(srType, new ControllerDescription("connectionSRExtra2",
        		 Constants.PRIMITIVE,configFile), 
        		 new ContentDescription(ConnectionImpl.class.getName()));
//         
         
         Component rsComp = cf.newFcInstance(cType, new ControllerDescription("connectionRS",
        		 Constants.PRIMITIVE,configFile), 
        		 new ContentDescription(ClientImpl.class.getName()));
         
         ((ServiceAttributes) Fractal.getAttributeController(sComp)).setHeader("--------> ");
         ((ServiceAttributes) Fractal.getAttributeController(sComp)).setCount(1);

 

             // component assembly
         Fractal.getContentController(rComp).addFcSubComponent(cComp);
         Fractal.getContentController(rComp).addFcSubComponent(srComp);
         Fractal.getContentController(rComp).addFcSubComponent(rsComp);
         Fractal.getContentController(rComp).addFcSubComponent(sComp);
         Fractal.getContentController(rComp).addFcSubComponent(sreComp);
         Fractal.getContentController(rComp).addFcSubComponent(sre1Comp);
         Fractal.getContentController(rComp).addFcSubComponent(sre2Comp);
         
         Fractal.getBindingController(rComp).bindFc("r", cComp.getFcInterface("r"));
         Fractal.getBindingController(cComp).bindFc("s", srComp.getFcInterface("s"));
         Fractal.getBindingController(srComp).bindFc("r", rsComp.getFcInterface("r"));
         Fractal.getBindingController(rsComp).bindFc("s", sComp.getFcInterface("s"));
         Fractal.getBindingController(sreComp).bindFc("r", rsComp.getFcInterface("r"));
         Fractal.getBindingController(sre1Comp).bindFc("r", rsComp.getFcInterface("r"));
         Fractal.getBindingController(sre2Comp).bindFc("r", rsComp.getFcInterface("r"));

         String scriptFile;
         scriptFile = System.getProperty("user.dir")+
  					  "/fscript/src/test/java/org/objectweb/fractal/fscript/model/test/proactive" +
  					  "/reconfigureRoot.fscript";

         ProActiveAOPLikeController aopController = (ProActiveAOPLikeController) rComp
         				.getFcInterface(ProActiveAOPLikeController.AOP_LIKE_CONTROLLER_NAME);
         //aopController.setServingTagMethod();
         
         
         Fractal.getLifeCycleController(rComp).startFc();
         //Fractal.getLifeCycleController(srComp).stopFc();
         //Fractal.getLifeCycleController(rsComp).stopFc();
         //(new DelegateFunctionalCode(rComp)).run();
         ((Runnable) rComp.getFcInterface("r")).run();
         Fractal.getLifeCycleController(rComp).stopFc();
   
     
//		rq = ((ActiveBody)((UniversalBodyProxy)((ProActiveComponentRepresentative) rComp)
//        		 .getProxy())
//        		 .getBody())
//        		 .getRequestQueue();
//		System.out.println(rq.toString());
//         ((Runnable) rComp.getFcInterface("r")).run();
//         ((Runnable) rComp.getFcInterface("r")).run();
//         ((Runnable) rComp.getFcInterface("r")).run();
         ((Runnable) rComp.getFcInterface("r")).run();
         ((Runnable) rComp.getFcInterface("r")).run();
         ((Runnable) rComp.getFcInterface("r")).run();
         Fractal.getLifeCycleController(rComp).stopFc();
//         Fractal.getLifeCycleController(rComp).startFc();

         //((Runnable) rComp.getFcInterface("r")).run();
   
       
         //new DelegateFunctionalCode(srComp,rComp).run();
         //new DelegateFunctionalCode(srComp).run();
         //rc.executeScript("reconfigure($root);");
         

         // call main method
         //((Runnable) rComp.getFcInterface("r")).run();
         //Fractal.getLifeCycleController(srComp).stopFc();
         BlockingRequestQueue rq;
         rq = ((ActiveBody)((UniversalBodyProxy)((ProActiveComponentRepresentative) rComp)
        		 .getProxy())
        		 .getBody())
        		 .getRequestQueue();
         System.out.println("requests queue = "+rq.toString());
     }
		
}
