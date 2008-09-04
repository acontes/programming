package Langch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

import myhelloworld.Client;

import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.component.identity.ProActiveComponentImpl;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;

import componentmonitorTests.component.monitor.controller.AbstractComponentMonitorController;
import componentmonitorTests.component.monitor.controller.itf.ComponentMonitorController;


public class LanchApplication {

    private static final long serialVersionUID = 1L;

    public LanchApplication() {

    }

    public void action() throws Exception {

        /**
         * set the system's properties it is important here, after this, there
         * is no need to right something in the Arguments
         */

        Properties programProperties = System.getProperties();
        programProperties.setProperty("java.security.manager", new String());
        programProperties.setProperty("java.security.policy", "lib/proactive.java.policy");
        programProperties.setProperty("proactive.communication.protocol", "rmissh");
        programProperties.setProperty("fractal.provider", "org.objectweb.proactive.core.component.Fractive");
        programProperties.setProperty("log4j.configuration", "file:lib/proactive-log4j");
        System.setProperties(programProperties);

        // to get the org.objectweb.fractal.adl.Factory
        Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();

        // initial the proactive deployment discripter
        ProActiveDescriptor deploymentDescriptor = ProActive
                .getProactiveDescriptor("myhelloworld/deployment/mydeployment-descriptor.xml");
        //		ProActiveDescriptor deploymentDescriptor = new ProActiveDescriptorImpl("myhelloworld/deployment/mydeployment-descriptor.xml");

        deploymentDescriptor.activateMappings();

        HashMap<String, Object> context = new HashMap<String, Object>(1);
        context.put("deployment-descriptor", deploymentDescriptor);

        // this is the place where use the fractal ADL, in path
        // myhelloworld/adl/myapplication.fractal

        System.out.println("Before Component create");

        Component testcase = (Component) f.newComponent("myhelloworld.adl.myapplication", context);

        System.out.println("after Component create");

        //start the thread to show the component monitoring info...
        ComponenMonitorFresher Monitorfresher = new ComponenMonitorFresher(testcase, 20000);
        Thread MonitorThread = new Thread(Monitorfresher);
        MonitorThread.start();

        //Monitor controller
        //		ComponentMonitorController CMc = (ComponentMonitorController)testcase.getFcInterface("monitor-controller");
        //		String[] metrics = CMc.listMetrics();
        //		for(String m : metrics)
        //		{
        //			String[] param = new String[10];
        //			Object[] pa = (Object[])param;
        //			
        //			System.out.println("Metric ==>"+m+":"+(String)CMc.execMonitor(m, new Object[1]).getObject());
        //		}
        //		
        //		ContentController CC = (ContentController)testcase.getFcInterface(Constants.CONTENT_CONTROLLER);
        ////		Component[] subComponents = CC.getFcSubComponents();
        //		Component[] subComponents = CMc.getSubComponents();
        //		for(Component SB: subComponents)
        //		{
        //			System.out.println("==============SubComponentMetricBegin==========");
        //			CMc = (ComponentMonitorController)SB.getFcInterface("monitor-controller");
        //			metrics = CMc.listMetrics();
        //			for(String m : metrics)
        //			{
        //				String[] param = new String[10];
        //				Object[] pa = (Object[])param;
        //				
        //				System.out.println("Metric ==>"+m+":"+(String)CMc.execMonitor(m, new Object[1]).getObject());
        //			}
        //			System.out.println("==============SubComponentMetricEnd==========");
        //		}

        //��testcase��ȡ��һ��controller
        //		ContentController cc = (ContentController)testcase.getFcInterface("content-controller");
        //		//Ҫȡ��һ��component��subcomponent,��������content-controller��ȡ������ֻ��composite component����content-controller.
        //		Component[] subComps = cc.getFcSubComponents();
        //		for(Component subC : subComps)
        //		{
        ////			Interface[] Is = (Interface[])subC.getFcInterfaces();
        ////			for(Interface I : Is)
        ////			{
        ////				System.out.println("==>"+I.getFcItfName());
        ////			}
        //			
        //			NameController nc = (NameController)subC.getFcInterface("name-controller");
        //			System.out.println(nc.getFcName());
        //			System.out.println(nc.toString());
        //		}

        //		//ContentController
        //		cc.getFcInternalInterfaces();
        //		cc.getFcSubComponents();
        //		cc.toString();
        //		//BindingController
        //		ProActiveBindingController bc = (ProActiveBindingController)testcase.getFcInterface("binding-controller");
        //		bc.listFc();
        //		//ComponentParametersController
        //		ComponentParametersController pc = (ComponentParametersController)testcase.getFcInterface("parameters-controller");
        //		pc.getComponentParameters();
        //		//ProActiveLifeCycleController
        //		ProActiveLifeCycleController lcc = (ProActiveLifeCycleController)testcase.getFcInterface("lifecycle-controller");
        //		lcc.getFcState();
        //		//ProActiveSuperController
        //		ProActiveSuperController sc = (ProActiveSuperController)testcase.getFcInterface("super-controller");
        //		sc.getFcSuperComponents();
        //		//NameController
        //		NameController nc = (NameController)testcase.getFcInterface("name-controller");
        //		nc.getFcName();
        //		//MulticastController
        //		MulticastController mc = (MulticastController)testcase.getFcInterface("multicast-controller");
        //		//MigrationController
        //		MigrationController mgc = (MigrationController)testcase.getFcInterface("migration-controller");
        //		//PriorityController
        //		PriorityController prc = (PriorityController)testcase.getFcInterface("priority-controller");
        ////		prc.getPriority(interfaceName, methodName, parametersTypes);

        //		BindingController bc = (BindingController)testcase.getFcInterface("binding-controller");
        //		String[] bcFcs = bc.listFc();
        //		for(String Fc: bcFcs)
        //		{
        //			System.out.println("binding-controller ==>"+Fc);
        //		}

        System.out.println();
        System.out.println();
        System.out.println("\t\t **************************************** ");
        System.out.println("\t\t *---------- GridCOMP Example ----------* ");
        System.out.println("\t\t *            my hello world            * ");
        System.out.println("\t\t *--------------------------------------* ");
        System.out.println("\t\t **************************************** ");
        System.out.println();
        System.out.println();
        System.out.println("\t\t <- Values stored in file PiOutput.log ->");

        System.out.println("Press 's' to start this example");
        System.out.println("Press '*' to terminate this example");
        System.out.println("Press 'sn' to set the name");
        System.out.println("Press 'sl' to set the location");
        System.out.println("Press 'gn' to get the name");
        System.out.println("Press 'gl' to get the location");

        System.out.flush();

        boolean terminated = false;

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        do {
            System.out.print("Console >>> ");
            String input = console.readLine();
            System.out.println();

            if (input == null)
                terminated = true;
            else {

                if (input.equals("*")) {
                    System.out.println(">>> Terminating the Application... <<<");
                    Fractal.getLifeCycleController(testcase).stopFc();
                    terminated = true;
                } else if (input.equals("s")) {
                    System.out.println(">>> Starting the Application... <<<");
                    try {
                        Fractal.getLifeCycleController(testcase).startFc();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (input.equals("sn")) {
                    System.out.print("input the name >>> ");
                    input = console.readLine();
                    System.out.println();
                    // add some control of testcase here
                    ((Client) testcase.getFcInterface("Outer-interface")).setName(input);
                    System.out.println(">>> Setting the name to be " + input + "... <<<");
                } else if (input.equals("sl")) {
                    System.out.print("input the location >>> ");
                    input = console.readLine();
                    System.out.println();
                    // add some control of testcase here
                    ((Client) testcase.getFcInterface("Outer-interface")).setLocation(input);
                    System.out.println(">>> Setting the location to be " + input + "... <<<");
                } else if (input.equals("gn")) {
                    //					Object[] t = testcase.getFcInterfaces();
                    //					for(int i = 0; i < t.length; i++)
                    //						System.out.println("====> " + t[i]);
                    System.out.println(">>> get the location is " +
                        ((Client) testcase.getFcInterface("Outer-interface")).getName());
                } else if (input.equals("gl")) {
                    System.out.println(">>> get the location is " +
                        ((Client) testcase.getFcInterface("Outer-interface")).getLocation());
                }
            }

        } while (!terminated);

        System.out.println("Application Terminated");
        System.exit(0);
    }

    private class ComponenMonitorFresher implements Runnable {
        private int interval;
        private Component root;

        public ComponenMonitorFresher(Component root, int interval) {
            this.root = root;
            this.interval = interval;
        }

        private void ShowComponentInfo(Component c, int hierachicalLevel) {
            String Space = "";
            for (int i = 0; i < hierachicalLevel; i++)
                Space += "    ";
            try {
                ComponentMonitorController CMc = (ComponentMonitorController) c
                        .getFcInterface("monitor-controller");
                System.out.println(Space +
                    "--Component : " +
                    (String) CMc
                            .execMonitor(AbstractComponentMonitorController.COMPONENT_NAME, new Object[1])
                            .getObject());
                String[] metrics = CMc.listMetrics();
                for (String m : metrics) {
                    String[] param = new String[10];
                    Object[] pa = (Object[]) param;

                    System.out.println(Space + "     |--" + "Metric ==>" + m + " : " +
                        (String) CMc.execMonitor(m, new Object[1]).getObject());
                }
                Component[] subComponents = CMc.getSubComponents();
                if (subComponents != null) {
                    for (Component subc : subComponents) {
                        ShowComponentInfo(subc, hierachicalLevel + 1);
                    }
                }
            } catch (NoSuchInterfaceException e) {

            }
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(this.interval);
                    System.out.println();
                    System.out
                            .println("==============Begin to Monitor the component and its childern============");
                    ShowComponentInfo(this.root, 0);
                    System.out
                            .println("=========================Components Monitoring Ended=====================");
                    System.out.println();

                } catch (Exception e) {

                }
            }
        }

    }

    public static void main(String[] args) {

        LanchApplication test = new LanchApplication();
        try {
            test.action();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
