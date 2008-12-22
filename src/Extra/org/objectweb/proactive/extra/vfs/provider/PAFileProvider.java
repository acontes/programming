package org.objectweb.proactive.extra.vfs.provider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs.provider.FileProvider;
import org.apache.commons.vfs.provider.GenericFileName;
import org.objectweb.proactive.core.node.Node;

public class PAFileProvider extends AbstractOriginatingFileProvider implements FileProvider{
	
	private static Node n;
	
	public PAFileProvider() {
		super();
		setFileNameParser(PAFileNameParser.getInstance());
	}
	
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
		       Capability.LIST_CHILDREN,
		   }));
	
	public Collection getCapabilities() {
		return capabilities;
	}

	protected FileSystem doCreateFileSystem(final FileName name,final FileSystemOptions fileSystemOptions) {
		
		GenericFileName  rootName = (GenericFileName)name;
		String scheme = rootName.getScheme();
		String host = rootName.getHostName();
		int port = rootName.getPort();
		String url = scheme + "://" + host +":" + port + "/" + "VFS_"+n.getNodeInformation().getName();
//		String url = scheme + "://" + host +":" + port + "/" + "VFSSERVER";
		
		System.out.println("**** Requesting to VFS SERVER at "+url + " ****");
		PAClient client = new PAClient();
		client.createConnection(url);
		if (client.isConnectionOKClient())
			System.out.println("**** Client connected with "+url + " ****");
		return new PAFileSystem(rootName, client , fileSystemOptions);
	}

/*	public FileObject findFile(FileObject baseFile, String uri, FileSystemOptions fileSystemOptions) throws FileSystemException {
		// TODO Auto-generated method stub
		FileName f = new PAFileObject(f, null, f).getName();
		return new PAFileObject(null, new PAFileSystem(baseFile, getClient(), fileSystemOptions), uri);
	}*/
	
	public void setNode (Node n) {
		this.n = n;
	}
	
}
