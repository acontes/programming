package org.objectweb.proactive.core.component.adl.luc.demo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.proactive.core.component.adl.FactoryFactory;
import org.objectweb.proactive.core.component.adl.luc.SimplestGCMFactory;
import org.xml.sax.SAXException;

public class Main
{
	
	public static void main(String... args) throws IOException, ParserConfigurationException, SAXException, ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException, org.objectweb.proactive.core.component.adl.luc.ADLException
	{
		File adlFile = new File("org/objectweb/proactive/core/component/adl/luc/demo/ExampleComponent.fractal");
		Component component = new SimplestGCMFactory().createComponent(adlFile, null	);
		GCM.getLifeCycleController(component).startFc();
		((ExampleComponent) component.getFcInterface("r")).printOk();
	}
	public static void main2(String... args) throws ADLException, NoSuchInterfaceException, InterruptedException, IllegalLifeCycleException
	{
		System.setProperty("fractal.provider", "org.objectweb.proactive.core.component.Fractive");
		Factory f = FactoryFactory.getFactory();
		Map<String, String> context = new HashMap<String, String>();		
		Object o = f.newComponent("org.objectweb.proactive.core.component.adl.luc.ExampleComponent", context);
		
		Component c = (Component) o;
		GCM.getLifeCycleController(c).startFc();

		ExampleComponent ec = (ExampleComponent) c.getFcInterface("r");
		System.err.println(ec.printOk());
		Thread.sleep(10000);
	}
}