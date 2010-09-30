package org.objectweb.proactive.core.component.adl.luc.description;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

import lucci.text.xml.XMLNode;

public class MembraneDescription extends Description
{
	private final List<ComponentDescription> componentDescriptions = new ArrayList<ComponentDescription>();
	private String desc;
	

	public String getDesc()
	{
		return desc;
	}


	public void setDesc(String desc)
	{
		this.desc = desc;
	}


	public List<ComponentDescription> getComponentDescriptions()
	{
		return componentDescriptions;
	}
	

	@Override
	public XMLNode toXMLNode()
	{
		XMLNode n = new XMLNode();
		n.setName("controller");
		return n;
	}


	@Override
	public void check() throws ADLException
	{
		for(ComponentDescription cd : getComponentDescriptions())
		{
			cd.check();
		}
		
	}
}
