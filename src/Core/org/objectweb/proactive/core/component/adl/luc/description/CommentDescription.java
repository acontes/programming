package org.objectweb.proactive.core.component.adl.luc.description;

import lucci.Clazz;
import lucci.text.xml.XMLNode;
import lucci.util.assertion.Assertions;

import org.objectweb.proactive.core.component.adl.luc.ADLException;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription.Cardinality;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription.Contingency;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription.Role;

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

	public  static CommentDescription createCommentDescription(XMLNode n)
	{
		Assertions.ensure(n.getName().equals("comment"), "comment description tag must be named 'comment''");
		CommentDescription cd = new CommentDescription();
		String language  = n.getAttributes().get("language");
		
		if (language != null)
		{
			cd.setLanguage(language);
		}
		String text  = n.getAttributes().get("text");
		
		if (text != null)
		{
			cd.setText(text);
		}
	
		return cd;
	}

	
}
