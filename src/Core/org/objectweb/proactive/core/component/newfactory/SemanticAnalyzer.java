package org.objectweb.proactive.core.component.newfactory;

import java.util.List;
import java.util.Map;

import lucci.Clazz;
import lucci.text.TextUtilities;
import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.newfactory.description.AttributeDescription;
import org.objectweb.proactive.core.component.newfactory.description.BindingDescription;
import org.objectweb.proactive.core.component.newfactory.description.CommentDescription;
import org.objectweb.proactive.core.component.newfactory.description.ComponentDescription;
import org.objectweb.proactive.core.component.newfactory.description.InterfaceDescription;
import org.objectweb.proactive.core.component.newfactory.description.MembraneDescription;
import org.objectweb.proactive.core.component.newfactory.description.InterfaceDescription.Cardinality;
import org.objectweb.proactive.core.component.newfactory.description.InterfaceDescription.Contingency;
import org.objectweb.proactive.core.component.newfactory.description.InterfaceDescription.Role;

public class SemanticAnalyzer
{

	public AttributeDescription createAttributeDescription(XMLNode n) throws ADLException
	{
		Assertions.ensure(n.getName().equals("attributes"), "attributes description tag must be named 'attributes''");
		AttributeDescription attrDesc = new AttributeDescription(Clazz.findClassOrFail(n.getAttributes().get("signature")));

		for (XMLNode c : n.getChildren())
		{
			if (c.getName().equals("attribute"))
			{
				for (String name : c.getAttributes().keySet())
				{
					attrDesc.getName_value().put(name, c.getAttributes().get(name));
				}
			}
			else
			{
				throw new ADLException("child description " + c.getName() + " not allowed for attributes description");
			}
		}

		return attrDesc;
	}

	public ComponentDescription createComponentDescription(XMLNode node) throws ADLException
	{
		Assertions.ensure(node.getName().matches("component|definition"), "component description tag must be named 'comopnent' or 'definition''");
		ComponentDescription description = new ComponentDescription(node.getAttributes().get("name"));



		{
			List<XMLNode> contentNodes = XMLNode.findChildrenWhoseNameMatch(node, "content");

			if (!contentNodes.isEmpty())
			{
				String contentClassname = contentNodes.get(0).getAttributes().get("class");
				Class<?> contentClass = Clazz.findClass(contentClassname);

				if (contentClass == null) throw new ADLException("cannot find content class " + contentClassname);

				description.setContent(contentClass);
			}
		}

		{
			List<XMLNode> membraneNodes = XMLNode.findChildrenWhoseNameMatch(node, "controller");

			if (membraneNodes.size() == 1)
			{
				description.setMembraneDescription(createMembraneDescription(membraneNodes.get(0)));
			}
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "interface"))
		{
			InterfaceDescription id = createInterfaceDescription(n);
			id.setParentDescription(description);
			description.getInterfaceDescriptions().add(id);
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "component"))
		{
			ComponentDescription cd = createComponentDescription(n);
			cd.setParentDescription(description);
			description.getSubcomponentDescriptions().add(cd);
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "binding"))
		{
			BindingDescription bd = createBindingDescription(n);
			bd.setParentDescription(description);
			description.getBindingDescriptions().add(bd);
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "attributes"))
		{
			AttributeDescription ad = createAttributeDescription(n);
			ad.setParentDescription(description);
			description.getAttributesDescriptions().add(ad);
		}

		for (XMLNode n : XMLNode.findChildrenWhoseNameMatch(node, "comments"))
		{
			CommentDescription ad = createCommentDescription(n);
			ad.setParentDescription(description);
			description.getCommentDescriptions().add(ad);
		}

		return description;
	}

	public BindingDescription createBindingDescription(XMLNode n)
	{
		Assertions.ensure(n.getName().equals("binding"), "binding description tag must be named 'binding''");
		return new BindingDescription(n.getAttributes().get("client"), n.getAttributes().get("server"));
	}

	public CommentDescription createCommentDescription(XMLNode n)
	{
		Assertions.ensure(n.getName().equals("comment"), "comment description tag must be named 'comment''");
		CommentDescription cd = new CommentDescription();
		String language = n.getAttributes().get("language");

		if (language != null)
		{
			cd.setLanguage(language);
		}
		String text = n.getAttributes().get("text");

		if (text != null)
		{
			cd.setText(text);
		}

		return cd;
	}

	public InterfaceDescription createInterfaceDescription(XMLNode n)
	{
		Assertions.ensure(n.getName().equals("interface"), "interface description tag must be named 'interface''");
		String name = n.getAttributes().get("name");
		Role role = n.getAttributes().get("role").equals("client") ? Role.CLIENT : Role.SERVER;
		Class<?> signature = Clazz.findClassOrFail(n.getAttributes().get("signature"));
		InterfaceDescription id = new InterfaceDescription(name, role, signature);

		if (n.getAttributes().get("contigency") != null)
		{
			id.setContingency(n.getAttributes().get("contigency").equals("mandatory") ? Contingency.MANDATORY : Contingency.OPTIONAL);
		}

		if (n.getAttributes().get("cardinality") != null)
		{
			id.setCardinality(n.getAttributes().get("cardinality").equals("singleton") ? Cardinality.SINGLETON : Cardinality.COLLECTION);
		}

		return id;
	}

	public MembraneDescription createMembraneDescription(XMLNode n)
	{
		Assertions.ensure(n.getName().equals("controller"), "membrane description tag must be named 'controller'");
		MembraneDescription id = new MembraneDescription();
		return id;
	}
	
	public void resolveVariablesInNode(XMLNode node, Map<String, String> argumentValues) throws ADLException
	{
		// for every attribute of the node
		for (String attrName : node.getAttributes().keySet())
		{
			// get the raw value for the attribute
			String attrValue = node.getAttributes().get(attrName);

			// replace the raw value by the evaluated one
			node.getAttributes().put(attrName, TextUtilities.replaceVariableValues(attrValue, argumentValues));
		}

		for (XMLNode c : node.getChildren())
		{
			resolveVariablesInNode(c, argumentValues);
		}
	}


}
