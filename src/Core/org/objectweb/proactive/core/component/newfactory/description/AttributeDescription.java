package org.objectweb.proactive.core.component.newfactory.description;

import java.util.HashMap;
import java.util.Map;

import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.newfactory.ADLException;

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
		if (signature == null) throw new NullPointerException();

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

}
