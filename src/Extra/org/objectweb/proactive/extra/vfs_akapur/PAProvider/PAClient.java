package org.objectweb.proactive.extra.vfs_akapur.PAProvider;


import java.io.IOException;


import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;


/**
 * This is a client for the ProActive communication
 * It will connect to the PAFileProviderEngine 
 * @author akapur
 *
 */

public class PAClient {
	
	
	private static PAFileProviderEngine engine = null;
	private PAFileEntryParser __entryParser;
	private PAFileEntryParserFactory __parserFactory;
	private PAFile[] children;
	
	public PAClient()
	{
		
	}
	
    public void createConnection(String Scheme,String hostname,int port,String path,Node node) throws ActiveObjectCreationException, IOException, NodeException
	
	{	             
			
		String url = Scheme + "://" + hostname + ":" + port + "/" + "VFS_"+node.getNodeInformation().getName();
			
		System.out.println("Looking up Active Object with URL:" + url);
		engine = (PAFileProviderEngine)PAActiveObject.lookupActive(PAFileProviderEngine.class.getName(), url);
		
		System.out.println("Connection created with Engine at : " + PAActiveObject.getActiveObjectNode(engine).getNodeInformation().getURL());
											
	}
    
public void createConnection(String url) throws ActiveObjectCreationException, IOException, NodeException
	
	{	             
			
		
			
		System.out.println("Looking up Active Object with URL:" + url);
		engine = (PAFileProviderEngine)PAActiveObject.lookupActive(PAFileProviderEngine.class.getName(), url);
		
		System.out.println("Connection created with Engine at : " + PAActiveObject.getActiveObjectNode(engine));
											
	}

public PAFile[] listFiles(String pathName)
{
	System.out.println("In Client....going to call the method on the Engine");  	
	System.out.println("Node of Engine Reference: " + PAActiveObject.getActiveObjectNodeUrl(engine));
	/*if(pathName.endsWith("."))
	{
		pathName = pathName.substring(pathName.length()-1);
	}*/
		
	
	try {
	return engine.getFiles(pathName+"VFS_"+PAActiveObject.getActiveObjectNode(engine).getNodeInformation().getName());
	} catch (NodeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
	
}
       
   
    
    /**************************************************************/
	
	public PAFileProviderEngine getEngine()
	{
	  return this.engine;	
	}
	
	
	
	
	/*public PAFile[] listFiles(String pathname)
    throws IOException
    {
        String key = null;
        PAListParseEngine engine =
            //initiateListParsing(key, pathname);
        return engine.getFiles();

    }*/
	
	   /*public PAListParseEngine initiateListParsing(
	            String parserKey, String pathname)
	    throws IOException
	    {
	        // We cache the value to avoid creation of a new object every
	        // time a file listing is generated.
	        if(__entryParser == null) {
	            if (null != parserKey) {
	                // if a parser key was supplied in the parameters, 
	                // use that to create the parser
	                __entryParser = 
	                    __parserFactory.createFileEntryParser(parserKey);
	                
	            } else {
	                // if no parserKey was supplied, check for a configuration
	                // in the params, and if non-null, use that.
	                if (null != __configuration) {
	                    __entryParser = 
	                        __parserFactory.createFileEntryParser(__configuration);
	                } else {
	                    // if a parserKey hasn't been supplied, and a configuration
	                    // hasn't been supplied, then autodetect by calling
	                    // the SYST command and use that to choose the parser.
	                    __entryParser = 
	                        __parserFactory.createFileEntryParser(getSystemName());
	                }
	            }
	        }

	        return initiateListParsing(__entryParser, pathname);

	    }*/



}
