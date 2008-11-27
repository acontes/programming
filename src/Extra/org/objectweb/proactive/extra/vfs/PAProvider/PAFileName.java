package org.objectweb.proactive.extra.vfs.PAProvider;



import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileName;

public class PAFileName 
extends AbstractFileName {

	
	private final String rootFile;
	
	protected PAFileName(final String scheme,
			             final String rootFile,
			             final String path,
			             final FileType type)
	{
		super(scheme, path, type);
        this.rootFile = rootFile;
	}
	
	public String getRootFile()
	{
		return rootFile;
	}

	@Override
	protected void appendRootUri(StringBuffer buffer, boolean addPassword) {
		
		buffer.append(getScheme());
        buffer.append("://");
        buffer.append(rootFile);
		
	}

	@Override
	public FileName createName(String path, FileType type) {
		
		return new PAFileName(getScheme(), rootFile, path, type);
	}
	
	

}
