package org.objectweb.proactive.extra.vfs.PAProvider;


import java.io.InputStream;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.vfs.ProActiveVFS;

public class PAFileObject 
extends AbstractFileObject
implements FileObject
{
	
	private final PAFileSystem filesystem; 
	
	private FileObject root;
	
	private Node node;

	protected PAFileObject(final FileName name,final PAFileSystem fs) 
	{
		super(name, fs);
		this.filesystem = fs;
		
	}

	
	/**
	 * I think we should attach the root of the File System at the node being mounted
	 * Using the Engine(Active Object) we can get the Node of the Active Object
	 */
	
	protected void doAttach()
	{
		
		PAFileProviderEngine engine = this.filesystem.getClient();
		try {
			
			this.node = PAActiveObject.getActiveObjectNode(engine);
			
		} catch (NodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			
			this.root = doStuffFordoAttach(this.node);
			
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * This method uses the node which is being mounted, to get it's File System.
	 * The root of the File System is assigned here
	 * 
	 * @param node
	 * @return
	 * @throws FileSystemException
	 */
	
	protected FileObject doStuffFordoAttach(Node node) throws FileSystemException
	{
		FileSystem fs = ProActiveVFS.getVFS(node);
		FileObject fileRoot = fs.getRoot();
		
		return fileRoot;
	}

	@Override
	protected long doGetContentSize() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	protected InputStream doGetInputStream() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	/**
	 * This method is for PAFileProvider
	 * The URI used is of the Active Object.
	 * So this method should return Imaginary file type
	 */
	 
	protected FileType doGetType() throws Exception {
		
		return FileType.IMAGINARY;         
	}


	@Override
	protected String[] doListChildren() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void ListChildren()
	{
		
		
		
	}
}