package org.objectweb.proactive.extra.vfs.provider;

import java.util.Collection;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;

public class PAFileSystem extends AbstractFileSystem implements FileSystem {
	
	static PAClient client;
	
	protected PAFileSystem(FileName rootName, PAClient client, FileSystemOptions fileSystemOptions) {
		 super(rootName, null, fileSystemOptions);
	     PAFileSystem.client = client;
	}
	
	@Override
	protected void addCapabilities(final Collection caps) {
		caps.addAll(PAFileProvider.capabilities);
	}

	@Override
	protected FileObject createFile(FileName name) throws Exception {
		return new PAFileObject(name, this, getRootName());
	}
	
	public static PAClient getClient() {
		return client;
	}
}
