package org.objectweb.proactive.extra.vfs_akapur.PAProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PAFileEntryParserImpl 
implements PAFileEntryParser {
	
	
	 /**
     * The constructor for a FTPFileEntryParserImpl object.
     */
    public PAFileEntryParserImpl()
    {
    }

    /**
     * Reads the next entry using the supplied BufferedReader object up to
     * whatever delemits one entry from the next.  This default implementation
     * simply calls BufferedReader.readLine().
     *
     * @param reader The BufferedReader object from which entries are to be
     * read.
     *
     * @return A string representing the next ftp entry or null if none found.
     * @exception java.io.IOException thrown on any IO Error reading from the reader.
     */
    public String readNextEntry(BufferedReader reader) throws IOException
    {
        return reader.readLine();
    }
    /**
     * This method is a hook for those implementors (such as
     * VMSVersioningFTPEntryParser, and possibly others) which need to
     * perform some action upon the FTPFileList after it has been created
     * from the server stream, but before any clients see the list.
     *
     * This default implementation removes entries that do not parse as files.
     *
     * @param original Original list after it has been created from the server stream
     *
     * @return <code>original</code> unmodified.
     */
     public List<String> preParse(List<String> original) {
         Iterator<String> it = original.iterator();
         while (it.hasNext()){
            String entry = it.next();
            if (null == parsePAEntry(entry)) {
                it.remove();
            }
         }
         return original;
     }

	public PAFile parsePAEntry(String listEntry) {
		// TODO Auto-generated method stub
		return null;
	}

}
