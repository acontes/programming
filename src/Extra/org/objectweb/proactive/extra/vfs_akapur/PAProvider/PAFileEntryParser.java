package org.objectweb.proactive.extra.vfs_akapur.PAProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public interface PAFileEntryParser {
	
	
	/**
     * Parses a line of an FTP server file listing and converts it into a usable
     * format in the form of an <code> FTPFile </code> instance.  If the
     * file listing line doesn't describe a file, <code> null </code> should be
     * returned, otherwise a <code> FTPFile </code> instance representing the
     * files in the directory is returned.
     * <p>
     * @param listEntry A line of text from the file listing
     * @return An FTPFile instance corresponding to the supplied entry
     */
    PAFile parsePAEntry(String listEntry);

    /**
     * Reads the next entry using the supplied BufferedReader object up to
     * whatever delemits one entry from the next.  Implementors must define
     * this for the particular ftp system being parsed.  In many but not all
     * cases, this can be defined simply by calling BufferedReader.readLine().
     *
     * @param reader The BufferedReader object from which entries are to be
     * read.
     *
     * @return A string representing the next ftp entry or null if none found.
     * @exception IOException thrown on any IO Error reading from the reader.
     */
    String readNextEntry(BufferedReader reader) throws IOException;


    /**
     * This method is a hook for those implementors (such as
     * VMSVersioningFTPEntryParser, and possibly others) which need to
     * perform some action upon the FTPFileList after it has been created
     * from the server stream, but before any clients see the list.
     *
     * The default implementation can be a no-op.
     *
     * @param original Original list after it has been created from the server stream
     *
     * @return Original list as processed by this method.
     */
    List<String> preParse(List<String> original);


}
