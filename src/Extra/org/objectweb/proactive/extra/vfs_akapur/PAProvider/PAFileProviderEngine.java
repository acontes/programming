package org.objectweb.proactive.extra.vfs_akapur.PAProvider;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;


import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

/**
 *This is meant to be the ProActive server.... just like an FTP Server.
 *The server is implemented as an internal service active object
 *A URL is created for the server. the client will use this url to connect.
 * @author akapur
 *
 */


public class PAFileProviderEngine 
implements ProActiveInternalObject, InitActive

{
	
	
	static PAFileProviderEngine singletonPAFileProviderEngine;
	private FileSystem fileSystem;
	private String protocol;
	private String port;
	static int i = 1;
	static FileObject[] allfiles;
	Vector<FileSystem> fsPool;
	//private static Node currentNode;
	//private static FileSystem fs;
	private List<String> entries = new LinkedList<String>();
    private ListIterator<String> _internalIterator = entries.listIterator();

    PAFileEntryParser parser = null;
	
	public PAFileProviderEngine()
	{
		
	}
	
	public PAFileProviderEngine(PAFileEntryParser parser) {
        this.parser = parser;
    }
	
	
	
	
    /**
     * Creates an Active Object(PAFileProviderEngine)
     * Creates the URL of the Active Object created, to be looked up
     * Registers the URL
     * 
     * @param node
     * @throws NodeException
     */
		
	public synchronized void startPAFileProviderEngine(Node node) throws NodeException
	{
	
		String protocol = node.getNodeInformation().getProtocol();
		int port;
		
		/*if(singletonPAFileProviderEngine == null)
		{*/
		
			try {
				
				
				PAFileProviderEngine singletonPAFileProviderEngine = (PAFileProviderEngine)PAActiveObject.newActive(PAFileProviderEngine.class.getName(), null ,node);
									
			   
				if(protocol.equalsIgnoreCase("rmi"))
				{
					port = 1099;
				}
				
				else if(protocol.equalsIgnoreCase("http"))
				{
					port = 8080;
				}
				
				else
				{
					port = 9100;
				}
				
								
				String url =   protocol + "://" + node.getVMInformation().getHostName() + ":" + port + "/"+ "VFS_"+ node.getNodeInformation().getName();	
				//String url =   protocol + "://localhost:" + port + "/"+ "VFS_"+ node.getNodeInformation().getName();	
				
							
				PAActiveObject.register(singletonPAFileProviderEngine,url);	
				
				System.out.println(" Engine Start and Registered at : " + node.getNodeInformation().getURL());
				
				
				
				
			} catch (ActiveObjectCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}	
		
	//}
		
	
    /**
     * The local Engine will have the knowledge of the local VFS and consequently of the local file objects
	 * This method provides that knowledge
     * 
     * @param engine : The engine reference
     * @throws NodeException 
     * @throws FileSystemException
     */
	
	/*public void getFileObjects(PAFileProviderEngine engine) throws NodeException, FileSystemException
	{
		 Node currentNode = PAActiveObject.getActiveObjectNode(engine);
		 
		 this.fileSystem = CreateNode.getVFSfromMap(currentNode);
		      
		 PAFileProviderEngine.allfiles =  this.fileSystem.getRoot().findFiles(new AllFileSelector());
			
			
			for(FileObject file: PAFileProviderEngine.allfiles)
			{
				System.out.println(file.toString());
			}
			
	}*/
	

	
	public void initActivity(Body body) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	public void getFileObjects(String pathName)
	{
		System.out.println("Inside Engine.....going to refer to the local OS File System");
		System.out.println("PAth Name is: ");
		System.out.println(pathName);
		File localFile = new File("/tmp/scratch/" + pathName);
		System.out.println(localFile.exists());
	}
    

	
	public PAFile[] getFiles(String relPath) throws IOException
    {
		System.out.println(" Inside PAFileProviderEngine: Trying to get Files @ " + relPath);
		
		/*if(relPath.contains("."))
		{
			String strings[] = relPath.split(".");
			relPath = strings[0].concat(strings[1]);
		}*/
		
		
		List<PAFile> tmpResults = new LinkedList<PAFile>();
        File tempFile = new File(relPath);
        while(tempFile.list() != null)
        {
        	System.out.println(tempFile.getCanonicalPath());
        	   for(String fileNames : tempFile.list())
        	   {
        		   File child = new File(relPath+ "/" + fileNames);
        		   PAFile tmp = new PAFile();
        		   tmp.setName(child.getAbsolutePath());
        		   tmpResults.add(tmp);
        	   }	       
		
         }
        
        return tmpResults.toArray(new PAFile[0]);
    }   
         
         
	
    /**
     * handle the initial reading and pre-parsing of the list returned by
     * the server.  After this method has completed, this object will contain
     * a list of unparsed entries (Strings) each referring to a unique file
     * on the server.
     *
     * @param stream input stream provided by the server socket.
     *
     * @exception IOException
     *                   thrown on any failure to read from the sever.
     */
   /* public void readServerList(InputStream stream, String encoding)
    throws IOException
    {
        this.entries = new LinkedList<String>();
        readStream(stream, encoding);
        this.parser.preParse(this.entries);
        resetIterator();
    }
    
    *//**
     * handle the initial reading and pre-parsing of the list returned by
     * the server.  After this method has completed, this object will contain
     * a list of unparsed entries (Strings) each referring to a unique file
     * on the server.
     *
     * @param stream input stream provided by the server socket.
     *
     * @exception IOException
     *                   thrown on any failure to read from the sever.
     *
     * @deprecated The version of this method which takes an encoding should be used.
    *//*
    public void readServerList(InputStream stream)
    throws IOException
    {
        readServerList(stream, null);
    }
    


    *//**
     * Internal method for reading the input into the <code>entries</code> list.
     * After this method has completed, <code>entries</code> will contain a
     * collection of entries (as defined by
     * <code>PAFileEntryParser.readNextEntry()</code>), but this may contain
     * various non-entry preliminary lines from the server output, duplicates,
     * and other data that will not be part of the final listing.
     *
     * @param stream The socket stream on which the input will be read.
     * @param encoding The encoding to use.
     *
     * @exception IOException
     *                   thrown on any failure to read the stream
     *//*
    private void readStream(InputStream stream, String encoding) throws IOException
    {
        BufferedReader reader;
        if (encoding == null)
        {
            reader = new BufferedReader(new InputStreamReader(stream));
        }
        else
        {
            reader = new BufferedReader(new InputStreamReader(stream, encoding));
        }
        
        String line = this.parser.readNextEntry(reader);

        while (line != null)
        {
            this.entries.add(line);
            line = this.parser.readNextEntry(reader);
        }
        reader.close();
    }

    *//**
     * Returns an array of at most <code>quantityRequested</code> PAFile
     * objects starting at this object's internal iterator's current position.
     * If fewer than <code>quantityRequested</code> such
     * elements are available, the returned array will have a length equal
     * to the number of entries at and after after the current position.
     * If no such entries are found, this array will have a length of 0.
     *
     * After this method is called this object's internal iterator is advanced
     * by a number of positions equal to the size of the array returned.
     *
     * @param quantityRequested
     * the maximum number of entries we want to get.
     *
     * @return an array of at most <code>quantityRequested</code> PAFile
     * objects starting at the current position of this iterator within its
     * list and at least the number of elements which  exist in the list at
     * and after its current position.
     * <p><b> 
     * NOTE:</b> This array may contain null members if any of the 
     * individual file listings failed to parse.  The caller should 
     * check each entry for null before referencing it.
     *//*
    public PAFile[] getNext(int quantityRequested) {
        List<PAFile> tmpResults = new LinkedList<PAFile>();
        int count = quantityRequested;
        while (count > 0 && this._internalIterator.hasNext()) {
            String entry = this._internalIterator.next();
            PAFile temp = this.parser.parsePAEntry(entry);
            tmpResults.add(temp);
            count--;
        }
        return tmpResults.toArray(new PAFile[0]);

    }

    *//**
     * Returns an array of at most <code>quantityRequested</code> PAFile
     * objects starting at this object's internal iterator's current position,
     * and working back toward the beginning.
     *
     * If fewer than <code>quantityRequested</code> such
     * elements are available, the returned array will have a length equal
     * to the number of entries at and after after the current position.
     * If no such entries are found, this array will have a length of 0.
     *
     * After this method is called this object's internal iterator is moved
     * back by a number of positions equal to the size of the array returned.
     *
     * @param quantityRequested
     * the maximum number of entries we want to get.
     *
     * @return an array of at most <code>quantityRequested</code> PAFile
     * objects starting at the current position of this iterator within its
     * list and at least the number of elements which  exist in the list at
     * and after its current position.  This array will be in the same order
     * as the underlying list (not reversed).
     * <p><b> 
     * NOTE:</b> This array may contain null members if any of the 
     * individual file listings failed to parse.  The caller should 
     * check each entry for null before referencing it.
     *//*
    public PAFile[] getPrevious(int quantityRequested) {
        List<PAFile> tmpResults = new LinkedList<PAFile>();
        int count = quantityRequested;
        while (count > 0 && this._internalIterator.hasPrevious()) {
            String entry = this._internalIterator.previous();
            PAFile temp = this.parser.parsePAEntry(entry);
            tmpResults.add(0,temp);
            count--;
        }
        return tmpResults.toArray(new PAFile[0]);
    }

    *//**
     * Returns an array of PAFile objects containing the whole list of
     * files returned by the server as read by this object's parser.
     *
     * @return an array of PAFile objects containing the whole list of
     *         files returned by the server as read by this object's parser.
     * <p><b> 
     * NOTE:</b> This array may contain null members if any of the 
     * individual file listings failed to parse.  The caller should 
     * check each entry for null before referencing it.
     * @exception IOException
     *//*
    public PAFile[] getFiles()
    throws IOException
    {
        List<PAFile> tmpResults = new LinkedList<PAFile>();
        Iterator<String> iter = this.entries.iterator();
        while (iter.hasNext()) {
            String entry = iter.next();
            PAFile temp = this.parser.parsePAEntry(entry);
            tmpResults.add(temp);
        }
        return tmpResults.toArray(new PAFile[0]);

    }

    *//**
     * convenience method to allow clients to know whether this object's
     * internal iterator's current position is at the end of the list.
     *
     * @return true if internal iterator is not at end of list, false
     * otherwise.
     *//*
    public boolean hasNext() {
        return _internalIterator.hasNext();
    }

    *//**
     * convenience method to allow clients to know whether this object's
     * internal iterator's current position is at the beginning of the list.
     *
     * @return true if internal iterator is not at beginning of list, false
     * otherwise.
     *//*
    public boolean hasPrevious() {
        return _internalIterator.hasPrevious();
    }

    *//**
     * resets this object's internal iterator to the beginning of the list.
     *//*
    public void resetIterator() {
        this._internalIterator = this.entries.listIterator();
    }*/

		
}	
		
		
	

