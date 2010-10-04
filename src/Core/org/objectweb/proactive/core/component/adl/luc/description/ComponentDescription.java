package org.objectweb.proactive.core.component.adl.luc.description;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lucci.collections.Lists;
import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

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

	public ComponentDescription(String name)
	{
		setName(name);
	}

	public List<ComponentDescription> getSuperDescriptions()
	{
		return superDescriptions;
	}

	public List<AttributeDescription> getDeclaredAttributesDescriptions()
	{
		return attributesDescriptions;
	}

	public List<AttributeDescription> getAllAttributeDescriptions()
	{
		if (getSuperDescriptions().isEmpty())
		{
			return getDeclaredAttributesDescriptions();
		}
		else
		{
			List<AttributeDescription> l = new ArrayList<AttributeDescription>();

			for (ComponentDescription cd : getSuperDescriptions())
			{
				l.addAll(cd.getAllAttributeDescriptions());
			}

			return Collections.unmodifiableList(Lists.concatene(l, getDeclaredAttributesDescriptions()));
		}
	}

	public List<ComponentDescription> getAllSubcomponentDescriptions()
	{
		if (getSuperDescriptions().isEmpty())
		{
			return getDeclaredSubcomponentDescriptions();
		}
		else
		{
			List<ComponentDescription> l = new ArrayList<ComponentDescription>();

			for (ComponentDescription cd : getSuperDescriptions())
			{
				l.addAll(cd.getAllSubcomponentDescriptions());
			}

			return Lists.concatene(l, getDeclaredSubcomponentDescriptions());
		}
	}
	
	public List<BindingDescription> getDeclaredBindingDescriptions()
	{
		return bindingDescriptions;
	}

	public List<BindingDescription> getAllBindingDescriptions()
	{
		if (getSuperDescriptions().isEmpty())
		{
			return getDeclaredBindingDescriptions();
		}
		else
		{
			List<BindingDescription> l = new ArrayList<BindingDescription>();

			for (ComponentDescription cd : getSuperDescriptions())
			{
				l.addAll(cd.getAllBindingDescriptions());
			}

			return Collections.unmodifiableList(Lists.concatene(l, getDeclaredBindingDescriptions()));
		}
	}

	public List<InterfaceDescription> getDeclaredInterfaceDescriptions()
	{
		return interfaceDescriptions;
	}

	public List<InterfaceDescription> getAllInterfaceDescriptions()
	{
		if (getSuperDescriptions().isEmpty())
		{
			return getDeclaredInterfaceDescriptions();
		}
		else
		{
			List<InterfaceDescription> l = new ArrayList<InterfaceDescription>();

			for (ComponentDescription cd : getSuperDescriptions())
			{
				l.addAll(cd.getAllInterfaceDescriptions());
			}

			return Collections.unmodifiableList(Lists.concatene(l, getDeclaredInterfaceDescriptions()));
		}
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
		if (this.content != null)
		{
			return this.content;
		}
		else
		{
			for (ComponentDescription sd : getSuperDescriptions())
			{
				Class<?> content = sd.getContent();
				
				if (content != null)
				{
					return content;
				}
			}

			return null;
		}
	}

	public void setContent(Class<?> content)
	{
		this.content = content;
	}

	public MembraneDescription getMembraneDescription()
	{
		if (this.membraneDescription != null)
		{
			return this.membraneDescription;
		}
		else
		{
			for (ComponentDescription sd : getSuperDescriptions())
			{
				MembraneDescription md = sd.getMembraneDescription();
				
				if (md != null)
				{
					return md;
				}
			}

			return null;
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



	@Override
	public XMLNode toXMLNode()
	{
		XMLNode n = new XMLNode();
		n.setName("component");
		n.getAttributes().put("name", getName());

		for (Description id : getAllInterfaceDescriptions())
		{
			n.getChildren().add(id.toXMLNode());
		}

		for (Description id : getAllBindingDescriptions())
		{
			n.getChildren().add(id.toXMLNode());
		}

		for (Description id : getAllSubcomponentDescriptions())
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
		Assertions.ensure(!getAllInterfaceDescriptions().isEmpty(), "no interface defined");

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

		for (ComponentDescription sd : getSuperDescriptions())
		{
			sd.check();
		}
	}

	public ComponentDescription findSubcomponentDescriptionByName(String name)
	{
		for (ComponentDescription d : getAllSubcomponentDescriptions())
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
			for (InterfaceDescription id : getAllInterfaceDescriptions())
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

}
