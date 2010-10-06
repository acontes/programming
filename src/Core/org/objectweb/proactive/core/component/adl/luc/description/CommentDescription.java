package org.objectweb.proactive.core.component.adl.luc.description;

import lucci.text.xml.XMLNode;

import org.objectweb.proactive.core.component.adl.luc.ADLException;

public class CommentDescription extends Description
{
	private String language = "en", text = "";

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		if (language == null)
			throw new NullPointerException();
		
		this.language = language;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		if (text == null)
			throw new NullPointerException();
		
		this.text = text;
	}

	@Override
	public void check() throws ADLException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public XMLNode toXMLNode()
	{
		XMLNode n = new XMLNode();
		n.setName("comment");
		n.getAttributes().put("language", getLanguage());
		n.getAttributes().put("text", getText());
		return n;
	}

	
	
}
