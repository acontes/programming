package org.objectweb.proactive.core.component.adl.luc.description;

import java.util.HashMap;
import java.util.Map;

import lucci.Clazz;
import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

public class AttributeDescription extends Description
{
	private Class<?> signature;
	private final Map<String, Object> name_value = new HashMap<String, Object>();
	
	public AttributeDescription(Class<?> signature)
	{
		setSignature(signature);
	}
	
	
	public Map<String, Object> getName_value()
	{
		return name_value;
	}

	@Override
	public void check() throws ADLException
	{
		Assertions.ensure(getSignature() == null, "signature is null");
	}

	@Override
	public XMLNode toXMLNode()
	{
		XMLNode n = new XMLNode();
		n.setName("attribute");
		n.getAttributes().put("signature", getSignature().getName());
		
		for (String k : name_value.keySet())
		{
			XMLNode child = new XMLNode();
			child.getAttributes().put(k, name_value.get(k).toString());
			n.getChildren().add(child);
		}

		return n;
	}

	public void setSignature(Class<?> signature)
	{
		if (signature == null)
			throw new NullPointerException();

		this.signature = signature;
	}

	public Class<?> getSignature()
	{
		return signature;
	}
	
	public ComponentDescription getParentComponentDescription()
	{
		return (ComponentDescription) getParentDescription();
	}
	
	
	public  static AttributeDescription createAttributeDescription(XMLNode n) throws ADLException
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


}
