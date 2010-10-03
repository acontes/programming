package org.objectweb.proactive.core.component.adl.luc.description;

import lucci.Clazz;
import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

public class InterfaceDescription extends Description
{
	public static enum Role { CLIENT, SERVER };
	public static enum Contingency { MANDATORY, OPTIONAL };
	public static enum Cardinality { SINGLETON, COLLECTION, gathercast, multicast };

	private String name;
	private Role role;
	private Class<?> signature;
	private Contingency contingency = Contingency.MANDATORY;
	private Cardinality cardinality = Cardinality.SINGLETON;

	public InterfaceDescription(String name, Role role, Class<?> signature)
	{
		setName(name);
		setRole(role);
		setSignature(signature);
	}
	
	public Contingency getContingency()
	{
		return contingency;
	}
	public void setContingency(Contingency contingency)
	{
		if (contingency == null)
			throw new NullPointerException();


		this.contingency = contingency;
	}
	public Cardinality getCardinality()
	{
		return cardinality;
	}
	public void setCardinality(Cardinality cardinality)
	{
		if (cardinality == null)
			throw new NullPointerException();


		this.cardinality = cardinality;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		if (name == null)
			throw new NullPointerException();

		this.name = name;
	}
	public Role getRole()
	{
		return role;
	}
	public void setRole(Role role)
	{
		if (role == null)
			throw new NullPointerException();

		this.role = role;
	}
	public Class<?> getSignature()
	{
		return signature;
	}
	public void setSignature(Class<?> signature)
	{
		if (signature == null)
			throw new NullPointerException();
		
		this.signature = signature;
	}
	
	
	public  static InterfaceDescription createInterfaceDescription(XMLNode n)
	{
		Assertions.ensure(n.getName().equals("interface"), "interface description tag must be named 'interface''");
		String name  = n.getAttributes().get("name");
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

	@Override
	public XMLNode toXMLNode()
	{
		XMLNode n = new XMLNode();
		n.setName("interface");
		n.getAttributes().put("name", getName());
		n.getAttributes().put("role", getRole().name());
		n.getAttributes().put("signature", getSignature().getName());
		n.getAttributes().put("contingency", getContingency().name());
		n.getAttributes().put("cardinality", getCardinality().name());
		return n;
	}
	@Override
	public void check() throws ADLException
	{
		
	}
	
	public ComponentDescription getParentComponentDescription()
	{
		return (ComponentDescription) getParentDescription();
	}
}
