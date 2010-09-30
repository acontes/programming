package org.objectweb.proactive.core.component.adl.luc.description;

import java.util.ArrayList;
import java.util.List;

import lucci.Clazz;
import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

public class ComponentDescription extends Description
{
	private String name;

	// the content may be null
	private Class<?> content;

	// may be null: component are not obliged to have componentized membrane
	private MembraneDescription membraneDescription;

	private final List<ComponentDescription> subcomponentDescriptions = new ArrayList<ComponentDescription>();
	private final List<InterfaceDescription> interfaceDescriptions = new ArrayList<InterfaceDescription>();
	private final List<BindingDescription> bindingDescriptions = new ArrayList<BindingDescription>();

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
		this.name = name;
	}

	public Class<?> getContent()
	{
		return content;
	}

	public void setContent(Class<?> content)
	{
		this.content = content;
	}

	public MembraneDescription getMembraneDescription()
	{
		return membraneDescription;
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

		for (Description id : getInterfaceDescriptions())
		{
			n.getChildren().add(id.toXMLNode());
		}

		for (Description id : getBindingDescriptions())
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

		for (Description id : getInterfaceDescriptions())
		{
			id.check();
		}

		for (Description cd : getSubcomponentDescriptions())
		{
			cd.check();
		}

		for (Description bd : getBindingDescriptions())
		{
			bd.check();
		}

		getMembraneDescription().check();
	}

	public static ComponentDescription createComponentDescription(XMLNode node) throws ADLException
	{
		ComponentDescription description = new ComponentDescription();

		description.setName(node.getAttributes().remove("name"));

		{
			List<XMLNode> contentNodes = XMLNode.findChildrenWhoseNameMatch(node, "content");

			if (!contentNodes.isEmpty())
			{
				String contentClassname = contentNodes.get(0).getAttributes().remove("class");
				Class<?> contentClass = Clazz.forName(contentClassname);

				if (contentClass == null) throw new ADLException("cannot find content class " + contentClassname);

				description.setContent(contentClass);
			}
		}

		{
			List<XMLNode> membraneNodes = XMLNode.findChildrenWhoseNameMatch(node, "controller");

			if (!membraneNodes.isEmpty())
			{
				description.setMembraneDescription(new MembraneDescription());
			}
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "interface"))
		{
			description.getInterfaceDescriptions().add(InterfaceDescription.createInterfaceDescription(n));
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "component"))
		{
			description.getSubcomponentDescriptions().add(createComponentDescription(n));
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "binding"))
		{
			description.getBindingDescriptions().add(BindingDescription.createBindingDescription(n));
		}

		return description;
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
			return findSubcomponentDescriptionByName(childName).findInterfaceDescription(interfaceName);
		}
	}

	
}
