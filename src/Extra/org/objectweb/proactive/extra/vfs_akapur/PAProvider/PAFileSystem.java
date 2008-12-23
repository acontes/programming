package org.objectweb.proactive.extra.vfs_akapur.PAProvider;



/**
 * the PAFileSystem class.
 * Will initialize the PAFileSystem configurations
 */


import java.util.Collection;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;
import org.apache.commons.vfs.provider.GenericFileName;


public class PAFileSystem
extends AbstractFileSystem
{
	
    static PAClient client;
	
	protected PAFileSystem(final GenericFileName rootName,final PAClient client,final FileSystemOptions filesystemoptions)
	{
		super(rootName,null,filesystemoptions);
		
		PAFileSystem.client = client;
	}

	
	protected void addCapabilities(final Collection caps) {
		
		caps.addAll(PAFileProvider.capabilities);
		
	}
	
	
	@Override
	protected FileObject createFile(FileName name) throws FileSystemException {
		
		System.out.println("Inside createFile() in PAFileSystem");
		System.out.println("Name in createFile() : " + name);
		return new PAFileObject(name,this, getRootName());
	}

	/**
	 * A PAFileProviderEngine reference is acting as the client 
	 *
	 * @return : reference to a PAFileProviderEngine
	 */
	
	protected PAClient getClient()
	{
		return PAFileSystem.client;
	}
	
	
}
