package org.objectweb.proactive.core.component.adl.luc.description;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lucci.Clazz;
import lucci.collections.Lists;
import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

public class ComponentDescription extends Description
{
	private File file;
	private String name;

	// the content may be null
	private Class<?> content;

	// may be null: component are not obliged to have componentized membrane
	private MembraneDescription membraneDescription;

	private final List<ComponentDescription> subcomponentDescriptions = new ArrayList<ComponentDescription>();
	private final List<InterfaceDescription> interfaceDescriptions = new ArrayList<InterfaceDescription>();
	private final List<BindingDescription> bindingDescriptions = new ArrayList<BindingDescription>();
	private final List<AttributeDescription> attributesDescriptions = new ArrayList<AttributeDescription>();
	private final List<String> arguments = new ArrayList<String>();
	
	private ComponentDescription superDefinition;

	public ComponentDescription getSuperDefinition()
	{
		return superDefinition;
	}

	public List<String> getArguments()
	{
		return arguments;
	}

	public List<AttributeDescription> getDeclaredAttributesDescriptions()
	{
		return attributesDescriptions;
	}
	
	public List<AttributeDescription> geAttributeDescriptions()
	{
		return Collections.unmodifiableList(Lists.concatene(superDefinition.geAttributeDescriptions(), getDeclaredAttributesDescriptions()));
	}


	public List<BindingDescription> getDeclaredBindingDescriptions()
	{
		return bindingDescriptions;
	}
	
	public List<BindingDescription> getBindingDescriptions()
	{
		return Collections.unmodifiableList(Lists.concatene(superDefinition.getBindingDescriptions(), getDeclaredBindingDescriptions()));
	}


	public List<InterfaceDescription> getDeclaredInterfaceDescriptions()
	{
		return interfaceDescriptions;
	}

	public List<InterfaceDescription> getInterfaceDescriptions()
	{
		return Collections.unmodifiableList(Lists.concatene(superDefinition.getInterfaceDescriptions(), getDeclaredInterfaceDescriptions()));
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
		if (this.content == null)
		{
			return getSuperDefinition().getContent();
		}
		else
		{
			return content;
		}
	}

	public void setContent(Class<?> content)
	{
		this.content = content;
	}

	public MembraneDescription getMembraneDescription()
	{
		if (this.membraneDescription == null)
		{
			return getSuperDefinition().getMembraneDescription();
		}
		else
		{
			return membraneDescription;
		}
	}

	public void setMembraneDescription(MembraneDescription membraneDescription)
	{
		this.membraneDescription = membraneDescription;
	}

	public List<ComponentDescription> getDeclaredSubcomponentDescriptions()
	{
		return subcomponentDescriptions;
	}
	
	public List<ComponentDescription> getSubcomponentDescriptions()
	{
		return Lists.concatene(superDefinition.getSubcomponentDescriptions(), getDeclaredSubcomponentDescriptions());
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

		for (Description d : getDeclaredInterfaceDescriptions())
		{
			d.check();
		}

		for (Description d : getDeclaredSubcomponentDescriptions())
		{
			d.check();
		}

		for (Description d : getDeclaredBindingDescriptions())
		{
			d.check();
		}

		for (Description d : getDeclaredAttributesDescriptions())
		{
			d.check();
		}

		
		if (this.membraneDescription != null)
		{
			this.membraneDescription.check();
		}
	}

	public static ComponentDescription createComponentDescription(XMLNode node) throws ADLException
	{
		Assertions.ensure(node.getName().matches("component|definition"), "component description tag must be named 'comopnent' or 'definition''");
		ComponentDescription description = new ComponentDescription();

		// this as to be done first
		description.getArguments().addAll(Arrays.asList(node.getAttributes().get("arguments").split(" *, *")));
		
		description.setName(node.getAttributes().remove("name"));

		{
			List<XMLNode> contentNodes = XMLNode.findChildrenWhoseNameMatch(node, "content");

			if (!contentNodes.isEmpty())
			{
				String contentClassname = contentNodes.get(0).getAttributes().remove("class");
				Class<?> contentClass = Clazz.findClass(contentClassname);

				if (contentClass == null) throw new ADLException("cannot find content class " + contentClassname);

				description.setContent(contentClass);
			}
		}

		{
			List<XMLNode> membraneNodes = XMLNode.findChildrenWhoseNameMatch(node, "controller");

			if (membraneNodes.size() == 1)
			{
				description.setMembraneDescription(MembraneDescription.createMembraneDescription(membraneNodes.get(0)));
			}
			else if ((membraneNodes.size() > 1))
			{
				throw new ADLException("only one membrane description is allowed");
			}
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "interface"))
		{
			description.getDeclaredInterfaceDescriptions().add(InterfaceDescription.createInterfaceDescription(n));
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "component"))
		{
			description.getDeclaredSubcomponentDescriptions().add(createComponentDescription(n));
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "binding"))
		{
			description.getDeclaredBindingDescriptions().add(BindingDescription.createBindingDescription(n));
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "attributes"))
		{
			description.getDeclaredAttributesDescriptions().add(AttributeDescription.createAttributeDescription(n));
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

	public void setFile(File file)
	{
		this.file = file;
	}

	public File getFile()
	{
		return file;
	}

	
}
