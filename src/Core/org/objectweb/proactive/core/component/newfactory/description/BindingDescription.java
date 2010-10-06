package org.objectweb.proactive.core.component.newfactory.description;

import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.newfactory.ADLException;

public class BindingDescription extends Description
{
	private String client, server;

	public BindingDescription(String client, String server)
	{
		setClient(client);
		setServer(server);
	}
	
	public String getClient()
	{
		return client;
	}

	public void setClient(String client)
	{
		if (client == null)
			throw new NullPointerException();

		this.client = client;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		if (server == null)
			throw new NullPointerException();

		this.server = server;
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
