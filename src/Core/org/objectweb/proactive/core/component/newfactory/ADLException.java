package org.objectweb.proactive.core.component.newfactory;

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
