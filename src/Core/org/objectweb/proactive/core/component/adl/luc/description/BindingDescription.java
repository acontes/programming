package org.objectweb.proactive.core.component.adl.luc.description;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

import com.sun.org.apache.xml.internal.utils.IntVector;

import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

public class BindingDescription extends Description
{
	private String client, server;

	public String getClient()
	{
		return client;
	}

	public void setClient(String client)
	{
		this.client = client;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}
	
	

	
	
	public static BindingDescription createBindingDescription(XMLNode n)
	{
		Assertions.ensure(n.getName().equals("binding"), "binding description tag must be named 'binding''");

		BindingDescription bd = new BindingDescription();
		bd.setClient(n.getAttributes().get("client"));
		bd.setServer(n.getAttributes().get("server"));
		return bd;
	}

	@Override
	public XMLNode toXMLNode()
	{
		XMLNode n = new XMLNode();
		n.setName("binding");
		n.getAttributes().put("client", getClient());
		n.getAttributes().put("server", getServer());
		return n;
	}

	@Override
	public void check() throws ADLException
	{
		// make sure that bound interface is defined
		ensureExist(getClient());
		ensureExist(getServer());
		
	}

	public ComponentDescription getParentComponentDescription()
	{
		return (ComponentDescription) getParentDescription();
	}
	
	
	private void ensureExist(String interfaceName)
	{
		// if the interface refers to a collection interface
		if (interfaceName.matches(".+[0-9]+"))
		{
		//	interfaceName = interfaceName.sub
		}

		//Assertions.ensure(getParentComponentDescription().findInterfaceDescription(interfaceName) != null);
	}
}
