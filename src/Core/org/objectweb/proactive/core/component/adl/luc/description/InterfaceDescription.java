package org.objectweb.proactive.core.component.adl.luc.description;

import lucci.Clazz;
import lucci.text.xml.XMLNode;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

public class InterfaceDescription extends Description
{
	public static enum Role { CLIENT, SERVER };
	public static enum Contingency { MANDATORY, OPTIONAL };
	public static enum Cardinality { SINGLETON, COLLECTION, gathercast, multicast };

	private String name;
	private Role role;
	private Class<?> signature;
	private Contingency contingency;
	public Contingency getContingency()
	{
		return contingency;
	}
	public void setContingency(Contingency contingency)
	{
		this.contingency = contingency;
	}
	public Cardinality getCardinality()
	{
		return cardinality;
	}
	public void setCardinality(Cardinality cardinality)
	{
		this.cardinality = cardinality;
	}
	private Cardinality cardinality;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public Role getRole()
	{
		return role;
	}
	public void setRole(Role role)
	{
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
		InterfaceDescription id = new InterfaceDescription();
		id.setName(n.getAttributes().get("name"));
		id.setRole(n.getAttributes().get("role").equals("client") ? InterfaceDescription.Role.CLIENT : InterfaceDescription.Role.SERVER);
		id.setSignature(Clazz.forName(n.getAttributes().get("signature")));
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
