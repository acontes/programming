package org.objectweb.proactive.extra.vfs.PAProvider;

import java.util.Collection;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;
import org.apache.commons.vfs.provider.GenericFileName;

public class PAFileSystem
extends AbstractFileSystem
implements FileSystem{
	
    private final PAFileProviderEngine engineRef;
	
	protected PAFileSystem(final GenericFileName rootName,final PAFileProviderEngine engine,final FileSystemOptions filesystemoptions)
	{
		super(rootName,null,filesystemoptions);
		
		this.engineRef = engine;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addCapabilities(final Collection caps) {
		// TODO Auto-generated method stub
		
		caps.addAll(PAFileProvider.capabilities);
		
	}
	
	
	@Override
	protected FileObject createFile(final FileName name) throws FileSystemException {
		// TODO Auto-generated method stub
		return new PAFileObject(name,this);
	}

	/**
	 * A PAFileProviderEngine reference is acting as the client 
	 *
	 * @return : reference to a PAFileProviderEngine
	 */
	
	public PAFileProviderEngine getClient()
	{
		return this.engineRef;
	}
	
	
}
