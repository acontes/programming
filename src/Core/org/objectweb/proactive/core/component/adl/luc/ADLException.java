package org.objectweb.proactive.core.component.adl.luc;

public class ADLException extends Exception
{
	public ADLException()
	{
		this("no message");
	}

	public ADLException(String msg)
	{
		super(msg);
	}
}
