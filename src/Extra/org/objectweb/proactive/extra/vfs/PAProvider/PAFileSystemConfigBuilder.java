package org.objectweb.proactive.extra.vfs.PAProvider;

import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemOptions;

public class PAFileSystemConfigBuilder 
extends FileSystemConfigBuilder{

	private static PAFileSystemConfigBuilder builder = new PAFileSystemConfigBuilder();
	
	public static PAFileSystemConfigBuilder getInstance()
	{
		return builder;
	}
	
	public PAFileSystemConfigBuilder()
	{
		
	}
	
	
	/**
     * Set the charset used for url encoding<br>
     *
     * @param chaset the chaset
     */
    public void setUrlCharset(FileSystemOptions opts, String chaset)
    {
        setParam(opts, "urlCharset", chaset);
    }

    /**
     * Get the charset used for url encoding<br>
     *
     * @return the chaset
     */
    public String getUrlCharset(FileSystemOptions opts)
    {
        return (String) getParam(opts, "urlCharset");
    }
    
    
	@Override
	protected Class getConfigClass() {
		// TODO Auto-generated method stub
		return PAFileSystem.class;
	}

}
