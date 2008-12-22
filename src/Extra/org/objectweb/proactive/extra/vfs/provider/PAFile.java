package org.objectweb.proactive.extra.vfs.provider;
import java.io.Serializable;
import java.util.Calendar;

public class PAFile implements Serializable
{
	private static final long serialVersionUID = 1L;
	/** A constant indicating an FTPFile is a file. ***/
    public static final int FILE_TYPE = 0;
    /** A constant indicating an FTPFile is a directory. ***/
    public static final int DIRECTORY_TYPE = 1;
    /** A constant indicating an FTPFile is a symbolic link. ***/
    public static final int SYMBOLIC_LINK_TYPE = 2;
    /** A constant indicating an FTPFile is of unknown type. ***/
    public static final int UNKNOWN_TYPE = 3;

    /** A constant indicating user access permissions. ***/
    public static final int USER_ACCESS = 0;
    /** A constant indicating group access permissions. ***/
    public static final int GROUP_ACCESS = 1;
    /** A constant indicating world access permissions. ***/
    public static final int WORLD_ACCESS = 2;

    /** A constant indicating file/directory read permission. ***/
    public static final int READ_PERMISSION = 0;
    /** A constant indicating file/directory write permission. ***/
    public static final int WRITE_PERMISSION = 1;
    /**
     * A constant indicating file execute permission or directory listing
     * permission.
     ***/
    public static final int EXECUTE_PERMISSION = 2;

    int _type;
    long _size;
    String _name, _link;
    Calendar _date;

    /*** Creates an empty PAFile. ***/
    public PAFile()
    {
        _type = UNKNOWN_TYPE;
        _size = 0;
        _date = null;
        _name = null;
    }
    public PAFile(String relpath)
    {
    	_link = relpath;
    }

    /***
     * Determine if the file is a directory.
     * <p>
     * @return True if the file is of type <code>DIRECTORY_TYPE</code>, false if
     *         not.
     ***/
    public boolean isDirectory()
    {
        return (_type == DIRECTORY_TYPE);
    }

    /***
     * Determine if the file is a regular file.
     * <p>
     * @return True if the file is of type <code>FILE_TYPE</code>, false if
     *         not.
     ***/
    public boolean isFile()
    {
        return (_type == FILE_TYPE);
    }

    /***
     * Determine if the file is a symbolic link.
     * <p>
     * @return True if the file is of type <code>UNKNOWN_TYPE</code>, false if
     *         not.
     ***/
    public boolean isSymbolicLink()
    {
        return (_type == SYMBOLIC_LINK_TYPE);
    }

    /***
     * Determine if the type of the file is unknown.
     * <p>
     * @return True if the file is of type <code>UNKNOWN_TYPE</code>, false if
     *         not.
     ***/
    public boolean isUnknown()
    {
        return (_type == UNKNOWN_TYPE);
    }


    /***
     * Set the type of the file (<code>DIRECTORY_TYPE</code>,
     * <code>FILE_TYPE</code>, etc.).
     * <p>
     * @param type  The integer code representing the type of the file.
     ***/
    public void setType(int type)
    {
        _type = type;
    }


    /***
     * Return the type of the file (one of the <code>_TYPE</code> constants),
     * e.g., if it is a directory, a regular file, or a symbolic link.
     * <p>
     * @return The type of the file.
     ***/
    public int getType()
    {
        return _type;
    }


    /***
     * Set the name of the file.
     * <p>
     * @param name  The name of the file.
     ***/
    public void setName(String name)
    {
        _name = name;
    }

    /***
     * Return the name of the file.
     * <p>
     * @return The name of the file.
     ***/
    public String getName()
    {
        return _name;
    }


    /**
     * Set the file size in bytes.
     * @param size The file size in bytes.
     */
    public void setSize(long size)
    {
        _size = size;
    }


    /***
     * Return the file size in bytes.
     * <p>
     * @return The file size in bytes.
     ***/
    public long getSize()
    {
        return _size;
    }

    /***
     * If the PAFile is a symbolic link, use this method to set the name of the
     * file being pointed to by the symbolic link.
     * <p>
     * @param link  The file pointed to by the symbolic link.
     ***/
    public void setLink(String link)
    {
        _link = link;
    }


    /***
     * If the PAFile is a symbolic link, this method returns the name of the
     * file being pointed to by the symbolic link.  Otherwise it returns null.
     * <p>
     * @return The file pointed to by the symbolic link (null if the PAFile
     *         is not a symbolic link).
     ***/
    public String getLink()
    {
        return _link;
    }


    /***
     * Set the file timestamp.  This usually the last modification time.
     * The parameter is not cloned, so do not alter its value after calling
     * this method.
     * <p>
     * @param date A Calendar instance representing the file timestamp.
     ***/
    public void setTimestamp(Calendar date)
    {
        _date = date;
    }

    /***
     * Returns the file timestamp.  This usually the last modification time.
     * <p>
     * @return A Calendar instance representing the file timestamp.
     ***/
    public Calendar getTimestamp()
    {
        return _date;
    }

    /***
     * Returns a string representation of the PAFile information.  This
     * will be the raw PA server listing that was used to initialize the
     * PAFile instance.
     * <p>
     * @return A string representation of the PAFile information.
     ***/
    @Override
    public String toString()
    {
        return _link;
    }

}
