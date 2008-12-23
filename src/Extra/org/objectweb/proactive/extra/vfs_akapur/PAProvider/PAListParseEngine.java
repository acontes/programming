package org.objectweb.proactive.extra.vfs_akapur.PAProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class PAListParseEngine {
	
	
	private List<String> entries = new LinkedList<String>();
    private ListIterator<String> _internalIterator = entries.listIterator();

    PAFileEntryParser parser = null;

    public PAListParseEngine(PAFileEntryParser parser) {
        this.parser = parser;
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
    public void readServerList(InputStream stream, String encoding)
    throws IOException
    {
        this.entries = new LinkedList<String>();
        readStream(stream, encoding);
        this.parser.preParse(this.entries);
        resetIterator();
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
     *
     * @deprecated The version of this method which takes an encoding should be used.
    */
    public void readServerList(InputStream stream)
    throws IOException
    {
        readServerList(stream, null);
    }
    


    /**
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
     */
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

    /**
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
     */
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

    /**
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
     */
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

    /**
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
     */
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

    /**
     * convenience method to allow clients to know whether this object's
     * internal iterator's current position is at the end of the list.
     *
     * @return true if internal iterator is not at end of list, false
     * otherwise.
     */
    public boolean hasNext() {
        return _internalIterator.hasNext();
    }

    /**
     * convenience method to allow clients to know whether this object's
     * internal iterator's current position is at the beginning of the list.
     *
     * @return true if internal iterator is not at beginning of list, false
     * otherwise.
     */
    public boolean hasPrevious() {
        return _internalIterator.hasPrevious();
    }

    /**
     * resets this object's internal iterator to the beginning of the list.
     */
    public void resetIterator() {
        this._internalIterator = this.entries.listIterator();
    }

}
