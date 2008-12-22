package org.objectweb.proactive.extra.vfs.provider;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.apache.commons.vfs.provider.AbstractFileSystem;

public class PAFileObject1 extends AbstractFileObject implements FileObject {
    private PAFile file;
    private FileType type;
    private final Set children;

    PAFileObject1(final FileName name, final AbstractFileSystem fs) {
        super(name, fs);
        this.type = FileType.IMAGINARY;
        this.children = new HashSet();
    }

    PAFileObject1(final FileName name, final PAFileSystem fs) {
        super(name, fs);
        this.children = new HashSet();
    }

    /**
     * @param childName
     */
    void attachChild(final FileName childName) {
        this.children.add(childName.getBaseName());
    }

    /**
     */
    public boolean isWriteable() {
        return false;
    }

    protected FileType doGetType() {
        return this.type;
    }

    protected String[] doListChildren() {
        return (String[]) this.children.toArray(new String[this.children.size()]);
    }

	protected long doGetContentSize() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	protected InputStream doGetInputStream() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
    
	/**
     * Attaches this file object to its file resource.
     */
    protected void doAttach() throws Exception
    {
       /* if (file == null)
        {
            file = createPAFile(getName());
        }*/
    }

    protected void doDetach() throws Exception
    {
        // file closed through content-streams
        file = null;
    }
    
    private PAFile createPAFile(FileName fileName)
    {
        //SmbFileName smbFileName = (SmbFileName) fileName;

        //String path = smbFileName.getUriWithoutAuth();
    	String path = fileName.getBaseName();
		PAFile file = null;

		try {
			if (file.isDirectory() && !file.toString().endsWith("/"))
				file = new PAFile(path + "/");
			else
				file = new PAFile(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
    }
}