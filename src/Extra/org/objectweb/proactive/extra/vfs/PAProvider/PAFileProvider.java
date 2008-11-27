package org.objectweb.proactive.extra.vfs.PAProvider;


import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.UserAuthenticationData;
import org.apache.commons.vfs.provider.AbstractFileProvider;
import org.apache.commons.vfs.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs.provider.FileProvider;
import org.apache.commons.vfs.provider.GenericFileName;
import org.apache.commons.vfs.provider.UriParser;
import org.apache.commons.vfs.util.UserAuthenticatorUtils;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.extra.vfs.MountedNodes;
import org.objectweb.proactive.extra.vfs.ProActiveVFS;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

//This class provider the entry point into the PAProvider. 



public class PAFileProvider 
extends AbstractOriginatingFileProvider
{
	Node node;
	MountedNodes mt = new MountedNodes();

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
	
	
	@Override
	protected FileSystem doCreateFileSystem(final FileName Name,
			final FileSystemOptions fileSystemOptions) throws FileSystemException {
		// TODO Auto-generated method stub
		
		//create the file system
		final GenericFileName rootName = (GenericFileName)Name;
		
	    PAFileProviderEngine engine = null;
	    
		try {
			engine = PAClientFactory.createConnection(rootName.getScheme(),rootName.getHostName(),
					                                                       rootName.getPort(),rootName.getPath());
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new PAFileSystem(rootName,engine,fileSystemOptions);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection getCapabilities()
    {
        return capabilities;
    }
	
	public FileSystemConfigBuilder getConfigBuilder()
	{
		return PAFileSystemConfigBuilder.getInstance();
	}
	


	
	
	


	
	
	
}
