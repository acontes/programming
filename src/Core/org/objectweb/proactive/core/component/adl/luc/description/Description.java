package org.objectweb.proactive.core.component.adl.luc.description;

import lucci.text.xml.XMLNode;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

public abstract class Description
{
	private Description parentDescription;
	
	
	public Description getParentDescription()
	{
		return parentDescription;
	}


	public void setParentDescription(Description parentDescription)
	{
		this.parentDescription = parentDescription;
	}


	@Override
	public String toString()
	{
		return toXMLNode().toString();
	}
	

	public abstract XMLNode toXMLNode();


	/**
	 * Because the instantiation of the component may involve networking, remote resource, etc... It
	 * is advisable to check things first! Without distribution, we would normally checks things
	 * on-the-fly, during instantiation.
	 * @throws ADLException
	 */
	public abstract void check() throws ADLException;
}
