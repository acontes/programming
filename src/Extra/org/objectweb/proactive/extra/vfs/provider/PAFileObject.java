package org.objectweb.proactive.extra.vfs.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import org.apache.commons.vfs.util.MonitorInputStream;

/**
 * An PA file Object.
 *
 * @author <a href="mailto:kamran.qadir@sophia.inria.fr">Kamran Qadir</a>
 */
public class PAFileObject
    extends AbstractFileObject
{
    private Log log = LogFactory.getLog(PAFileObject.class);

    private static final Map EMPTY_PA_FILE_MAP = Collections.unmodifiableMap(new TreeMap());

    private final PAFileSystem paFs;
    private String relPath;

    // Cached info
    private PAFile fileInfo;
    private Map children;
    private FileObject linkDestination;

	private boolean inRefresh=false;

	protected PAFileObject(final FileName name,
                            final PAFileSystem fileSystem,
                            final FileName rootName)
        throws FileSystemException
    {
        super(name, fileSystem);
        paFs = fileSystem;
        //this.relPath = UriParser.decode(rootName.getRelativeName(name));
        this.relPath = "/tmp/scratch/"+name.getBaseName()+"/";
        /*if (".".equals(relPath))
        {
            // do not use the "." as path against the ftp-server
            // e.g. the uu.net ftp-server do a recursive listing then
            // this.relPath = UriParser.decode(rootName.getPath());
            // this.relPath = ".";
            this.relPath = null;
        }
        else
        {
            this.relPath = relPath;
        }*/
/*        System.out.println(" PAFile Object Name " + name.getBaseName());
        System.out.println(" Real path "+ this.relPath);*/
    }

    /**
     * Called by child file objects, to locate their ftp file info.
     *
     * @param name  the filename in its native form ie. without uri stuff (%nn)
     * @param flush recreate children cache
     */
    private PAFile getChildFile(final String name, final boolean flush) throws IOException
    {
        if (flush)
        {
 			children = null;
        }
        this.relPath = "/tmp/scratch/"+name+"/";
        //this.relPath = name;
        // List the children of this file
        doGetChildren();

        // Look for the requested child
        //System.out.println("^ In get Child File " + name);
        
        /*Set keys = children.keySet();         // The set of keys in the map.
        Iterator keyIter = keys.iterator();
        System.out.println("The map contains the following associations:");
        while (keyIter.hasNext()) {
           Object key = keyIter.next();  // Get the next key.
           Object value = children.get(key);  // Get the value for that key.
           System.out.println( "   (" + key + "," + value + ")" );
        }*/

        PAFile paFile = (PAFile) children.get(name);
        //System.out.println( "  Child 1 PAFile : " + paFile.getLink());
        return paFile;
    }

    /**
     * Fetches the children of this file, if not already cached.
     */
    private void doGetChildren() throws IOException
    {
    	if (children != null)
        {
            return;
        }

        PAClient client = PAFileSystem.getClient();
        try
        {
            final PAFile[] tmpChildren = client.listFiles(relPath);
            
            //System.out.println("^ In do Get Children"+ this.relPath);
            for (PAFile pf:tmpChildren) {
            	System.out.println("	" + pf.getLink());
            }
            
            if (tmpChildren == null || tmpChildren.length == 0)
            {
                children = EMPTY_PA_FILE_MAP;
            }
            else
            {
                children = new TreeMap();

                // Remove '.' and '..' elements
                for (int i = 0; i < tmpChildren.length; i++)
                {
                    final PAFile child = (PAFile)tmpChildren[i];
                    if (child == null)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug(Messages.getString("vfs.provider.pa/invalid-directory-entry.debug",
                                new Object[]
                                    {
                                        new Integer(i), relPath
                                    }));
                        }
                        continue;
                    }
                   children.put(child.getName(), child);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Attaches this file object to its file resource.
     */
    protected void doAttach()
        throws IOException
    {
        // Get the parent folder to find the info for this file
        getInfo(false);
    	
    }

    /**
     * Fetches the info for this file.
     */
    private void getInfo(boolean flush) throws IOException
    {
        final PAFileObject parent = (PAFileObject) FileObjectUtils.getAbstractFileObject(getParent());
        
        PAFile newFileInfo;
        if (parent != null)
        {
        	//System.out.println("GetBase Name File Name " + getName().getBaseName());
            //newFileInfo = parent.getChildFile(UriParser.decode(getName().getBaseName()), flush);
        	newFileInfo = parent.getChildFile(getName().getBaseName(), flush);
        	
        }
        else
        {
            // Assume the root is a directory and exists
            newFileInfo = new PAFile();
            newFileInfo.setType(PAFile.DIRECTORY_TYPE);
        }
        //this.fileInfo.setLink("/tmp/scratch/a.txt");
        //System.out.println("File Name " + newFileInfo.getName());
        this.fileInfo = newFileInfo;
    }

	/**
	 *
	 * @throws FileSystemException
	 */
	public void refresh() throws FileSystemException
	{
		if (!inRefresh)
		{
			try
			{
				inRefresh = true;
				super.refresh();
				try
				{
					// this will tell the parent to recreate its children collection
					getInfo(true);
				}
				catch (IOException e)
				{
					throw new FileSystemException(e);
				}
			}
			finally
			{
				inRefresh = false;
			}
		}
	}

	/**
     * Detaches this file object from its file resource.
     */
    protected void doDetach()
    {
        this.fileInfo = null;
        children = null;
    }

    /**
     * Called when the children of this file change.
     */
    protected void onChildrenChanged(FileName child, FileType newType)
    {
        if (children != null && newType.equals(FileType.IMAGINARY))
        {
            try
            {
                children.remove(UriParser.decode(child.getBaseName()));
            }
            catch (FileSystemException e)
            {
                throw new RuntimeException(e.getMessage());
            }
        }
        else
        {
            // if child was added we have to rescan the children
            // TODO - get rid of this
            children = null;
        }
    }

    /**
     * Called when the type or content of this file changes.
     */
    protected void onChange() throws IOException
    {
        children = null;

        if (getType().equals(FileType.IMAGINARY))
        {
            // file is deleted, avoid server lookup
            this.fileInfo = null;
            return;
        }

        getInfo(true);
    }

    /**
     * Determines the type of the file, returns null if the file does not
     * exist.
     */
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

        throw new FileSystemException("vfs.provider.pa/get-type.error", getName());
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

    protected FileObject[] doListChildrenResolved() throws Exception
    {
    	//System.out.println("^^^ do list Children Resolved the path to FILE "+ relPath);
        if (this.fileInfo.isSymbolicLink())
        {
            return getLinkDestination().getChildren();
        }

        return null;
    }

    /**
     * Lists the children of the file.
     */
    protected String[] doListChildren()
        throws Exception
    {
    	//System.out.println("^^ do list Children "+ this.getRelPath());
    	// List the children of this file
        doGetChildren();

        // TODO - get rid of this children stuff
        final String[] childNames = new String[children.size()];
        int childNum = -1;
        Iterator iterChildren = children.values().iterator();
        while (iterChildren.hasNext())
        {
            childNum++;
            final PAFile child = (PAFile) iterChildren.next();
            childNames[childNum] = child.getName();
        }

        return UriParser.encode(childNames);
    }

    /**
     * Deletes the file.
     */
    protected void doDelete() throws Exception
    {
        boolean ok = false;
        final PAClient Client = PAFileSystem.getClient();
        try
        {
            if (this.fileInfo.isDirectory())
            {
                ok = Client.removeDirectory(relPath);
            }
            else
            {
                ok = Client.deleteFile(relPath);
            }
        }
        catch(Exception e)
        {
        e.printStackTrace();
        }

        if (!ok)
        {
            throw new FileSystemException("vfs.provider.pa/delete-file.error", getName());
        }
        this.fileInfo = null;
        children = EMPTY_PA_FILE_MAP;
    }

    /**
     * Renames the file
     */
    protected void doRename(FileObject newfile) throws Exception
    {
        boolean ok = false;
        final PAClient Client = PAFileSystem.getClient();
        try
        {
            String oldName = getName().getPath();
            String newName = newfile.getName().getPath();
            ok = Client.rename(oldName, newName);
        }
        catch(Exception e)
        {
        e.printStackTrace();
        }
        
        if (!ok)
        {
            throw new FileSystemException("vfs.provider.pa/rename-file.error", new Object[]{getName().toString(), newfile});
        }
        this.fileInfo = null;
        children = EMPTY_PA_FILE_MAP;
    }

    /**
     * Creates this file as a folder.
     */
    protected void doCreateFolder()
        throws Exception
    {
        boolean ok = false;
        final PAClient client = PAFileSystem.getClient();
        try
        {
            ok = client.makeDirectory(relPath);
        }
        catch(Exception e)
        {
        e.printStackTrace();
        }
        
        if (!ok)
        {
            throw new FileSystemException("vfs.provider.pa/create-folder.error", getName());
        }
    }

    /**
     * Returns the size of the file content (in bytes).
     */
    protected long doGetContentSize() throws Exception
    {
        if (this.fileInfo.isSymbolicLink())
        {
            return getLinkDestination().getContent().getSize();
        }
        else
        {
            return this.fileInfo.getSize();
        }
    }

    /**
     * get the last modified time on an ftp file
     *
     * @see org.apache.commons.vfs.provider.AbstractFileObject#doGetLastModifiedTime()
     */
    protected long doGetLastModifiedTime() throws Exception
    {
        if (this.fileInfo.isSymbolicLink())
        {
            return getLinkDestination().getContent().getLastModifiedTime();
        }
        else
        {
            Calendar timestamp = this.fileInfo.getTimestamp();
            if (timestamp == null)
            {
                return 0L;
            }
            else
            {
                return (timestamp.getTime().getTime());
            }
        }
    }

    /**
     * Creates an input stream to read the file content from.
     */
    protected InputStream doGetInputStream() throws Exception
    {
        System.out.println(" In doGet input stream "+fileInfo.getName());
    	final PAClient client = PAFileSystem.getClient();
        final InputStream instr = client.readDataClient(relPath);
        return new PAInputStream(client, instr);
    }

   String getRelPath()
    {
        return relPath;
    }

    PAInputStream getInputStream(long filePointer) throws IOException
    {
        System.out.println(" In Get Input stream "+fileInfo.getName());
        final PAClient client = PAFileSystem.getClient();
        final InputStream instr = client.readDataClient(fileInfo.getName());
        if (instr == null)
        {
            throw new FileSystemException("vfs.provider.pa/input-error.debug", new Object[]
                {
                    this.getName()                    
                });
        }
        return new PAInputStream(client, instr);
    }

    /**
     * An InputStream that monitors for end-of-file.
     */
    class PAInputStream
        extends MonitorInputStream
    {
        private final PAClient client;

        public PAInputStream(final PAClient client, final InputStream in)
        {
            super(in);
            this.client = client;
        }

        void abort() throws IOException
        {
            //client.abort();
            //close();
        }
    }
}
