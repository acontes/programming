package org.objectweb.proactive.core.component.adl.luc;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import lucci.io.FileUtilities;
import lucci.io.Utilities;
import lucci.text.xml.XMLNode;
import lucci.text.xml.XMLUtilities;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.adl.luc.description.BindingDescription;
import org.objectweb.proactive.core.component.adl.luc.description.ComponentDescription;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription.Contingency;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription.Role;
import org.xml.sax.SAXException;

public class SimplestGCMFactory
{
	public SimplestGCMFactory()
	{
		if (System.getProperty("gcm.provider") == null)
		{
			System.setProperty("gcm.provider", Fractive.class.getName());
		}
	}



	public Component createComponent(File adlFile, File argumentFile) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, ADLException, ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		String adlDescription = new String(FileUtilities.getFileContent(adlFile));
		XMLNode node = XMLUtilities.xml2node(adlDescription, false);
		Arguments.apply(Arguments.parseArguments(argumentFile), node);
		ComponentDescription componentDescription = ComponentDescription.createComponentDescription(node);
		componentDescription.setFile(adlFile);
		return createComponent(componentDescription);
	}

	public Component createComponent(ComponentDescription componentDescription) throws ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		componentDescription.check();
		ComponentType type = createType(componentDescription);
		Component component = instantiateComponent(componentDescription, type);
		GCM.getNameController(component).setFcName(componentDescription.getName());
		Map<String, Component> name_comp = setSubComponents(componentDescription, component);
		name_comp.put("this", component);
		bindAllInterfaces(componentDescription, name_comp);
		return component;
	}

	private void bindAllInterfaces(ComponentDescription componentDescription, Map<String, Component> comp_child) throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException
	{
		for (BindingDescription bd : componentDescription.getAllBindingDescriptions())
		{
			InterfaceDescription clientInterfaceDescription = componentDescription.findInterfaceDescription(bd.getClient());
			InterfaceDescription serverInterfaceDescription = componentDescription.findInterfaceDescription(bd.getServer());
			Component clientComp = comp_child.get(clientInterfaceDescription.getParentComponentDescription().getName());
			Component serverComp = comp_child.get(serverInterfaceDescription.getParentComponentDescription().getName());
			Object serverIterface = serverComp.getFcInterface(serverInterfaceDescription.getName());
			GCM.getBindingController(clientComp).bindFc(clientInterfaceDescription.getName(), serverIterface);
		}
	}

	private Map<String, Component> setSubComponents(ComponentDescription componentDescription, Component component) throws ADLException, IllegalContentException, IllegalLifeCycleException, NoSuchInterfaceException, InstantiationException, IllegalBindingException
	{
		Map<String, Component> comp_child = new HashMap<String, Component>();

		for (ComponentDescription subD : componentDescription.getAllSubcomponentDescriptions())
		{
			Component child = createComponent(subD);
			GCM.getContentController(component).addFcSubComponent(child);
			comp_child.put(GCM.getNameController(child).getFcName(), child);
		}

		return comp_child;
	}

	private Component instantiateComponent(ComponentDescription componentDescription, ComponentType type) throws InstantiationException, NoSuchInterfaceException
	{
		GenericFactory gf = GCM.getGenericFactory(Utils.getBootstrapComponent());

		if (componentDescription.getMembraneDescription() == null)
		{
			// if the component is primitive
			String s = componentDescription.getAllSubcomponentDescriptions().isEmpty() ? Constants.PRIMITIVE : Constants.COMPOSITE;
			return gf.newFcInstance(type, s, componentDescription.getContent() == null ? null : componentDescription.getContent().getName());
		}
		else
		{
			String desc = componentDescription.getMembraneDescription().getDesc();

			if (desc.equals(Constants.PRIMITIVE) || desc.equals(Constants.COMPOSITE))
			{
				return gf.newFcInstance(type, desc, componentDescription.getContent().getName());
			}
			else
			{
				// this refers to an external controller config
				ControllerDescription cd = new ControllerDescription("parametricPrimitive", componentDescription.getAllSubcomponentDescriptions().isEmpty() ? Constants.PRIMITIVE : Constants.COMPOSITE, desc, false);
				return gf.newFcInstance(type, cd, componentDescription.getContent().getName());
			}
		}
	}

	private ComponentType createType(ComponentDescription componentDescription) throws InstantiationException, NoSuchInterfaceException
	{

		GCMTypeFactory typeFactory = GCM.getGCMTypeFactory(Utils.getBootstrapComponent());
		List<InterfaceType> interfaceTypes = new ArrayList<InterfaceType>();

		for (InterfaceDescription id : componentDescription.getAllInterfaceDescriptions())
		{
			System.out.println(id);
			interfaceTypes.add(typeFactory.createGCMItfType(id.getName(), id.getSignature().getName(), id.getRole() == Role.CLIENT, id.getContingency() == Contingency.OPTIONAL, id.getCardinality().name()));
		}

		return typeFactory.createFcType(interfaceTypes.toArray(new InterfaceType[0]));
	}


}
