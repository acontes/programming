package org.objectweb.proactive.extra.vfs_akapur.PAProvider;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs.provider.FileProvider;
import org.apache.commons.vfs.provider.GenericFileName;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.vfs_akapur.MountedNodes;

//This class provider the entry point into the PAProvider. 



public class PAFileProvider 
extends AbstractOriginatingFileProvider
implements FileProvider

{
	private static Node currentNode;
	MountedNodes mt = new MountedNodes();
	String url;

	final static Collection capabilities = Collections.unmodifiableCollection(Arrays.asList(new Capability[]
	  {
			
			Capability.CREATE,
	        Capability.DELETE,
	        Capability.RENAME,
	        Capability.GET_TYPE,
	        Capability.LIST_CHILDREN,
	        Capability.READ_CONTENT,
	        Capability.GET_LAST_MODIFIED,
	        Capability.URI,
	        Capability.WRITE_CONTENT,
	        Capability.APPEND_CONTENT,
	        Capability.RANDOM_ACCESS_READ,
			
			
			
	  }));

	public PAFileProvider()
	{
		super();
		setFileNameParser(PAFileNameParser.getInstance());
		
	}
	      
	public PAFileProvider(String url)
	{	
		super();
		setFileNameParser(PAFileNameParser.getInstance());
		this.url = url;
	}
	
	
	public static void setCurrentNode(Node node)
	{
		PAFileProvider.currentNode = node;
	}
	
	@Override
	protected FileSystem doCreateFileSystem(final FileName Name,
			final FileSystemOptions fileSystemOptions) throws FileSystemException {
		
		System.out.println("Inside doCreateFileSystem");
		
		//create the file system
		final GenericFileName rootName = (GenericFileName)Name;
		System.out.println(rootName);
		
	    PAClient client = new PAClient();
	    
		try {
			
			//client.createConnection(url);
			client.createConnection(rootName.getScheme(),rootName.getHostName(),
                    rootName.getPort(),rootName.getPath(),PAFileProvider.currentNode);
			
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new PAFileSystem(rootName,client,fileSystemOptions);
	}
	
	
	public Collection getCapabilities()
    {
        return capabilities;
    }
	
	public FileSystemConfigBuilder getConfigBuilder()
	{
		return PAFileSystemConfigBuilder.getInstance();
	}


	



	}
	


	
	
	


	
	
	

