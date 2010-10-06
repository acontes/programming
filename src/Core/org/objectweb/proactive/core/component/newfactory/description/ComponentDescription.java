package org.objectweb.proactive.core.component.newfactory.description;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import lucci.collections.Lists;
import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.newfactory.ADLException;

public class ComponentDescription extends Description
{
	private String name;
	private final List<ComponentDescription> superDescriptions = new ArrayList<ComponentDescription>();
	
	// the content may be null
	private Class<?> content;

	// may be null: component are not obliged to have componentized membrane
	private MembraneDescription membraneDescription;

	private final List<ComponentDescription> subcomponentDescriptions = new ArrayList<ComponentDescription>();
	private final List<InterfaceDescription> interfaceDescriptions = new ArrayList<InterfaceDescription>();
	private final List<BindingDescription> bindingDescriptions = new ArrayList<BindingDescription>();
	private final List<AttributeDescription> attributesDescriptions = new ArrayList<AttributeDescription>();
	private final Map<String, String> arg_defaultValue = new HashMap<String, String>();

	public ComponentDescription(String name)
	{
		setName(name);
	}

	public Map<String, String> getArguments()
	{
		return arg_defaultValue;
	}

	public List<ComponentDescription> getSuperDescriptions()
	{
		return superDescriptions;
	}

	public List<AttributeDescription> getAttributesDescriptions()
	{
		return attributesDescriptions;
	}


	public List<BindingDescription> getBindingDescriptions()
	{
		return bindingDescriptions;
	}

	public List<InterfaceDescription> getInterfaceDescriptions()
	{
		return interfaceDescriptions;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (name == null) throw new NullPointerException();

		this.name = name;
	}

	public Class<?> getContent()
	{
		return content;
	}

	public List<ComponentDescription> flattenInheritanceTree()
	{
		List<ComponentDescription> l = new ArrayList<ComponentDescription>();
		l.add(this);

		// l.size() must be re-evaluated at every cycle
		for (int i = 0; i < l.size(); ++i)
		{
			l.addAll(l.get(i).getSuperDescriptions());
		}

		return l;
	}
	
	public void setContent(Class<?> content)
	{
		this.content = content;
	}

	public MembraneDescription getMembraneDescription()
	{
		return this.membraneDescription;
	}

	public void setMembraneDescription(MembraneDescription membraneDescription)
	{
		this.membraneDescription = membraneDescription;
	}

	public List<ComponentDescription> getSubcomponentDescriptions()
	{
		return subcomponentDescriptions;
	}

	@Override
	public XMLNode toXMLNode()
	{
		XMLNode n = new XMLNode();
		n.setName("component");
		n.getAttributes().put("name", getName());

		if (!getArguments().isEmpty())
		{
			String argumentsValue = "";

			for (String argName : getArguments().keySet())
			{
				if (!argumentsValue.isEmpty())
				{
					argumentsValue += ", ";
				}

				String defaultValue = getArguments().get(argName);
				argumentsValue += argName + (defaultValue == null ? "" : "=" + defaultValue);
			}

			n.getAttributes().put("arguments", argumentsValue);
		}
		

		for (Description id : getInterfaceDescriptions())
		{
			n.getChildren().add(id.toXMLNode());
		}

		for (Description id : getBindingDescriptions())
		{
			n.getChildren().add(id.toXMLNode());
		}

		for (Description id : getCommentDescriptions())
		{
			n.getChildren().add(id.toXMLNode());
		}

		for (Description id : getSubcomponentDescriptions())
		{
			n.getChildren().add(id.toXMLNode());
		}

		if (getMembraneDescription() != null)
		{
			n.getChildren().add(getMembraneDescription().toXMLNode());
		}

		if (getContent() != null)
		{
			XMLNode contentNode = new XMLNode();
			contentNode.setName("content");
			contentNode.getAttributes().put("class", getContent().getClass().getName());
			n.getChildren().add(contentNode);
		}

		return n;
	}

	@Override
	public void check() throws ADLException
	{
		Assertions.ensure(!getName().trim().isEmpty(), "name can't be empty");
		Assertions.ensure(!getName().equals("this"), "name can't be 'this'");
		Assertions.ensure(!getInterfaceDescriptions().isEmpty(), "no interface defined");

		for (Description d : getInterfaceDescriptions())
		{
			d.check();
		}

		for (Description d : getSubcomponentDescriptions())
		{
			d.check();
		}

		for (Description d : getBindingDescriptions())
		{
			d.check();
		}

		for (Description d : getAttributesDescriptions())
		{
			d.check();
		}

		if (this.membraneDescription != null)
		{
			this.membraneDescription.check();
		}

		for (ComponentDescription sd : getSuperDescriptions())
		{
			sd.check();
		}
	}

	public ComponentDescription findSubcomponentDescriptionByName(String name)
	{
		for (ComponentDescription d : getSubcomponentDescriptions())
		{
			if (d.getName().equals(name))
			{
				return d;
			}
		}

		return null;
	}

	public InterfaceDescription findInterfaceDescription(String name)
	{
		int pos = name.indexOf('.');

		// no dot found so the interface belongs to the current component
		if (pos < 0)
		{
			for (InterfaceDescription id : getInterfaceDescriptions())
			{
				if (id.getName().equals(name))
				{
					return id;
				}
			}

			return null;
		}
		else
		{
			String childName = name.substring(0, pos);
			String interfaceName = name.substring(pos + 1);

			if (childName.equals("this"))
			{
				return findInterfaceDescription(interfaceName);
			}
			else
			{
				return findSubcomponentDescriptionByName(childName).findInterfaceDescription(interfaceName);
			}
		}
	}
	
	public ComponentDescription resolveInheritance()
	{
		Map<String, InterfaceDescription> interfaces = new HashMap<String, InterfaceDescription>();
		Collection<BindingDescription> bindings = new HashSet<BindingDescription>();
		Map<String, ComponentDescription> children = new HashMap<String, ComponentDescription>();
		Map<Class<?>, AttributeDescription> attributes = new HashMap<Class<?>, AttributeDescription>();
		Class<?> content = null;
		MembraneDescription membraneDescription = null;
		Map<String, String> arguments = null;
		
		
		for (ComponentDescription cd : Lists.reverse(flattenInheritanceTree()))
		{
			for (InterfaceDescription id : cd.getInterfaceDescriptions())
			{
				interfaces.put(id.getName(), id);
			}

			for (ComponentDescription childD : cd.getSubcomponentDescriptions())
			{
				children.put(childD.getName(), childD);
			}

			for (AttributeDescription attrs : cd.getAttributesDescriptions())
			{
				attributes.put(attrs.getSignature(), attrs);
			}

			bindings.addAll(cd.getBindingDescriptions());
			
			if (cd.getContent() != null) content = cd.getContent();
			if (cd.getArguments() != null) arguments = cd.getArguments();
			if (cd.getMembraneDescription() != null) membraneDescription = cd.getMembraneDescription();
		}
		
		ComponentDescription r = new ComponentDescription(getName());
		r.getInterfaceDescriptions().addAll(interfaces.values());
		r.getSubcomponentDescriptions().addAll(children.values());
		r.getBindingDescriptions().addAll(bindings);
		r.getAttributesDescriptions().addAll(attributes.values());
		if (membraneDescription != null) r.setMembraneDescription(membraneDescription);
		if (content != null) r.setContent(content);
		if (arguments != null) r.getArguments().putAll(arguments);
		return r;
	}
	

}
