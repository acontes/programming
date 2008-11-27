package org.objectweb.proactive.extra.vfs.PAProvider;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.vfs.ProActiveVFS;

public class EngineService implements InitActive {
	
	
	private FileSystem myFileSystem;
	
	public EngineService()
	{
		
	}

	public EngineService(FileSystem fs)
	{
		this.myFileSystem = fs;
	}
	
	@Override
	public void initActivity(Body body) {
		
		
		
	}
	
	public FileSystem getMyFileSystem()
	{
		return this.myFileSystem;
	}
	
	
	public void getFileObjects(PAFileProviderEngine engine) throws NodeException, FileSystemException
	{
		
					
        
		PAFileProviderEngine.allfiles =  fs.getRoot().findFiles(new AllFileSelector());
			
			
			for(FileObject file: PAFileProviderEngine.allfiles)
			{
				System.out.println(file.toString());
			}
			
		}
	
		
	}	
	


