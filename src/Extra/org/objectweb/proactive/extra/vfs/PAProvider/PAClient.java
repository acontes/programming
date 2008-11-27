package org.objectweb.proactive.extra.vfs.PAProvider;

public class PAClient {
	
	
	static PAFileProviderEngine engine;
	
	public PAClient()
	{
		
	}
	
	public void listChildren()
	{
		
	}
	
	public static void setEngine(PAFileProviderEngine eg)
	{
		engine = eg;
		
	}
	
	public PAFileProviderEngine getEngine()
	{
	  return this.engine;	
	}

}
