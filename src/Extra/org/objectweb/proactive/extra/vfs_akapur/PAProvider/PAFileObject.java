package org.objectweb.proactive.extra.vfs_akapur.PAProvider;

/**
 * The PAFileObject will resolve the PA type URIs to refer to the real location of the file.
 * 
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.apache.commons.vfs.provider.UriParser;
import org.apache.commons.vfs.util.FileObjectUtils;
import org.apache.commons.vfs.util.Messages;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;


public class PAFileObject 
extends AbstractFileObject
implements FileObject
{
	
	private final PAFileSystem paFileSystem; 
	private final String relPath;
	private Map children;
    private FileObject linkDestination;
	
	private FileObject root;
	private PAFile fileInfo;
	private static final Map EMPTY_FTP_FILE_MAP = Collections.unmodifiableMap(new TreeMap());
	private Log log = LogFactory.getLog(PAFileObject.class);
	private Node node;

	protected PAFileObject(final FileName name,final PAFileSystem fs, final FileName rootName) throws FileSystemException 
	{
		
		
		super(name, fs);
		this.paFileSystem = fs;
		this.relPath = "/tmp/scratch/";// + UriParser.decode(rootName.getRelativeName(name));
		//this.relPath = "/tmp/scratch/"+ virtualRelPath.substring(4);
		System.out.println("PAFileObject constructor");
		System.out.println("Relative Path of the File in PAFileObject Constructor is : "  + this.relPath);
		System.out.println("RootName in PAFileObject Constructor:" + rootName);
		System.out.println("Name in PAFileObject Constructor:" + name.getPath());
		
	}
	
		
	private PAFile getChildFile(final String name, final boolean flush) throws IOException
    {
        if (flush)
        {
 			children = null;
        }

        doGetChildren();

        // Look for the requested child
        PAFile paFile = (PAFile) children.get(name);
        return paFile;
    }
	
	
	private void doGetChildren(String name) throws NodeException
	{
		System.out.println("In Do get Children");
				
		final PAClient client = paFileSystem.getClient();
		
		System.out.println("Relative Path is: " + name);
		
		final PAFile[] tmpChildren = client.listFiles(name);
		
		
		
		//Node node = PAActiveObject.getActiveObjectNode();
		
		
		
		//client.getFileObjects(node)
		//File files = client.getFileObjects(node);
	}
	
	private void doGetChildren() throws IOException
    {
        if (children != null)
        {
            return;
        }

        final PAClient client = paFileSystem.getClient();
        try
        {
            final PAFile[] tmpChildren = client.listFiles(relPath);
            if (tmpChildren == null || tmpChildren.length == 0)
            {
                children = EMPTY_FTP_FILE_MAP;
            }
            else
            {
                children = new TreeMap();

                // Remove '.' and '..' elements
                for (int i = 0; i < tmpChildren.length; i++)
                {
                    final PAFile child = tmpChildren[i];
                    if (child == null)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug(Messages.getString("vfs.provider.ftp/invalid-directory-entry.debug",
                                new Object[]
                                    {
                                        new Integer(i), relPath
                                    }));
                        }
                        continue;
                    }
                    if (!".".equals(child.getName())
                        && !"..".equals(child.getName()))
                    {
                        children.put(child.getName(), child);
                    }
                }
            }
        }
        
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
	
	
	/**
	 * I think we should attach the root of the File System at the node being mounted
	 * Using the Engine(Active Object) we can get the Node of the Active Object
	 */
	
	protected void doAttach()
	{
		System.out.println("Inside doAttach Method of PAFileObject");
		
		/*PAClient client = this.paFileSystem.getClient();
		PAFileProviderEngine engineRef = client.getEngine();
		try {
			this.node = PAActiveObject.getActiveObjectNode(engineRef);
		} catch (NodeException e) {
			
			e.printStackTrace();
		}*/
		
		try {
			doStuffFordoAttach(false);
		} catch (FileSystemException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
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
	 * @throws IOException 
	 */
	
	protected void doStuffFordoAttach(Boolean flush) throws IOException
	{
		//engine.getFileObjects(node);
		System.out.println("Inside doStuff MEthod");
		final PAFileObject parent = (PAFileObject) FileObjectUtils.getAbstractFileObject(getParent());
		PAFile newFileInfo;
		
		System.out.println("Parent in doStuff");
		System.out.println(parent.getName());
        if (parent != null)
        {
            newFileInfo = parent.getChildFile(UriParser.decode(getName().getBaseName()), flush);
            System.out.println(newFileInfo);
        }
        else
        {
            // Assume the root is a directory and exists
            newFileInfo = new PAFile();
            newFileInfo.setType(PAFile.DIRECTORY_TYPE);
        }
        
		/*newFileInfo = new PAFile();
        newFileInfo.setType(PAFile.UNKNOWN_TYPE);*/
		//this.fileInfo = null;
        this.fileInfo = newFileInfo;
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
	 
	/*protected FileType doGetType() throws Exception {
		
		return FileType.FOLDER;         
	}*/
	
	protected FileType doGetType()
    throws Exception
{
    if (this.fileInfo == null)
    {
        return FileType.IMAGINARY;
    }
    else if (this.fileInfo.isDirectory())
    {
        return FileType.FOLDER;
    }
    else if (this.fileInfo.isFile())
    {
        return FileType.FILE;
    }
    else if (this.fileInfo.isSymbolicLink())
    {
        return getLinkDestination().getType();
    }

    throw new FileSystemException("vfs.provider.ftp/get-type.error", getName());
}
	


	private FileObject getLinkDestination() throws FileSystemException
    {
        if (linkDestination == null)
        {
            final String path = this.fileInfo.getLink();
       
            FileName relativeTo = getName().getParent();
            if (relativeTo == null)
            {
                relativeTo = getName();
            }
            FileName linkDestinationName = getFileSystem().getFileSystemManager().resolveName(relativeTo, path);
            linkDestination = getFileSystem().resolveFile(linkDestinationName);
        }

        return linkDestination;
    }


	@Override
	protected String[] doListChildren() throws Exception {
		
		System.out.println("Inside diListChildren()");
		doGetChildren("abc");
		
		return null;
	}
	
	protected void ListChildren()
	{
		
		
		
	}
}