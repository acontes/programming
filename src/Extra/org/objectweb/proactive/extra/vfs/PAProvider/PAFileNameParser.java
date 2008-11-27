package org.objectweb.proactive.extra.vfs.PAProvider;

import org.apache.commons.vfs.provider.FileNameParser;
import org.apache.commons.vfs.provider.HostFileNameParser;



/*
 * Implementation for PAFileProvider. Setting default port according to the Protocol.
 * Right now it is only for RMI
 */

public class PAFileNameParser extends HostFileNameParser

{  
	
	static String protocol;
	static int port;
	
	public static void setProtocol(String pr)
	{
	   protocol = pr;	
	      
	}
	
	public static void setDefaultPort()
	{
		if(protocol.equalsIgnoreCase("rmi"))
				{
			         port = 1099;
				}
		
	}
	
	public final static PAFileNameParser INSTACE = new PAFileNameParser(); 
	
	
	PAFileNameParser()
	{	
	
		super(port);
						
	}
	
	public static FileNameParser getInstance()
	{
		return INSTACE;
	}

}
