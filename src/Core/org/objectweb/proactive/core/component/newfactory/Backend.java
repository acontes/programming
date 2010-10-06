package org.objectweb.proactive.core.component.newfactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.newfactory.description.BindingDescription;
import org.objectweb.proactive.core.component.newfactory.description.ComponentDescription;
import org.objectweb.proactive.core.component.newfactory.description.InterfaceDescription;
import org.objectweb.proactive.core.component.newfactory.description.InterfaceDescription.Contingency;
import org.objectweb.proactive.core.component.newfactory.description.InterfaceDescription.Role;

public class Backend
{

	public Component createComponent(ComponentDescription componentDescription, Object deploymentFile) throws ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		// verifies that everything in the description and all sub-descriptions is okay
		componentDescription.check();

		// flattens the inheritance graph into one big component description
		System.out.println(componentDescription);
		componentDescription = componentDescription.resolveInheritance();
		System.out.println(componentDescription);

		// creates the Fractal "type" for the component, necessary to instantiate
		// the component
		ComponentType type = createType(componentDescription);
		Component component = instantiateComponent(componentDescription, type);
		GCM.getNameController(component).setFcName(componentDescription.getName());
		Map<String, Component> name_comp = setSubComponents(componentDescription, component, deploymentFile);
		name_comp.put(componentDescription.getName(), component);
		bindAllInterfaces(componentDescription, name_comp);
		return component;
	}
	
	private void bindAllInterfaces(ComponentDescription componentDescription, Map<String, Component> comp_child) throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException
	{
		for (BindingDescription bd : componentDescription.getBindingDescriptions())
		{
			InterfaceDescription clientInterfaceDescription = componentDescription.findInterfaceDescription(bd.getClient());
			Component clientComp = comp_child.get(clientInterfaceDescription.getParentComponentDescription().getName());

			InterfaceDescription serverInterfaceDescription = componentDescription.findInterfaceDescription(bd.getServer());
			Component serverComp = comp_child.get(serverInterfaceDescription.getParentComponentDescription().getName());
			Object serverIterface = serverComp.getFcInterface(serverInterfaceDescription.getName());

			GCM.getBindingController(clientComp).bindFc(clientInterfaceDescription.getName(), serverIterface);
		}
	}

	private Map<String, Component> setSubComponents(ComponentDescription componentDescription, Component component, Object deploymentFile) throws ADLException, IllegalContentException, IllegalLifeCycleException, NoSuchInterfaceException, InstantiationException, IllegalBindingException
	{
		Map<String, Component> comp_child = new HashMap<String, Component>();

		for (ComponentDescription subD : componentDescription.getSubcomponentDescriptions())
		{
			Component child = createComponent(subD, deploymentFile);
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
			String s = componentDescription.getSubcomponentDescriptions().isEmpty() ? Constants.PRIMITIVE : Constants.COMPOSITE;
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
				ControllerDescription cd = new ControllerDescription("parametricPrimitive", componentDescription.getSubcomponentDescriptions().isEmpty() ? Constants.PRIMITIVE : Constants.COMPOSITE, desc, false);
				return gf.newFcInstance(type, cd, componentDescription.getContent().getName());
			}
		}
	}

	private ComponentType createType(ComponentDescription componentDescription) throws InstantiationException, NoSuchInterfaceException
	{
		GCMTypeFactory typeFactory = GCM.getGCMTypeFactory(Utils.getBootstrapComponent());
		List<InterfaceType> interfaceTypes = new ArrayList<InterfaceType>();

		for (InterfaceDescription id : componentDescription.getInterfaceDescriptions())
		{
			interfaceTypes.add(typeFactory.createGCMItfType(id.getName(), id.getSignature().getName(), id.getRole() == Role.CLIENT, id.getContingency() == Contingency.OPTIONAL, id.getCardinality().name()));
		}

		return typeFactory.createFcType(interfaceTypes.toArray(new InterfaceType[0]));
	}


}
