package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.VFSFactory;
import org.objectweb.proactive.extra.dataspaces.adapter.vfs.VFSFileObjectAdapter;
import org.objectweb.proactive.extra.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;


public class VFSFileObjectAdapterTest {
    
    private static final long appId = 1;
    private static final String runtimeId = "rt1";
    private static final String nodeId = "node1";
    private static final String activeObjectId = "ao1";
    private static final String path = "dir/file.txt";
    private static final DataSpacesURI mountingPointURI = DataSpacesURI.createScratchSpaceURI(appId,
            runtimeId, nodeId, activeObjectId);
    
    private static DefaultFileSystemManager fileSystemManager;
    private DataSpacesFileObject dsFileObject;
    private FileObject adaptee;
    private File testDir;
    private File someDir;
    private File someFile;
    private File rootDir;
    private File differentDir;
    private String differentDirPath;
    private String rootDirPath;
    
    @BeforeClass
    static public void init() throws org.apache.commons.vfs.FileSystemException {
        fileSystemManager = VFSFactory.createDefaultFileSystemManager();
    }
    
    @AfterClass
    static public void close() {
        fileSystemManager.close();
    }
    
    @Before
    public void setUp() throws IOException, MalformedURIException {
        
        testDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-VFSFileObjectAdapterTest");
        differentDir = new File(testDir, "different");
        rootDir = new File(testDir, "root");
        someDir = new File(rootDir, "dir");
        someFile = new File(someDir, "file.txt");
        assertTrue(someDir.mkdirs());
        assertTrue(differentDir.mkdir());
        assertTrue(someFile.createNewFile());
        
        rootDirPath = rootDir.getCanonicalPath();
        differentDirPath = differentDir.getCanonicalPath();
        
        final FileObject rootFileObject = fileSystemManager.resolveFile("file://" + rootDirPath);
        final FileName mountintPointFileName = rootFileObject.getName();
        adaptee = rootFileObject.resolveFile(path);
        
        dsFileObject = new VFSFileObjectAdapter(adaptee, mountingPointURI, mountintPointFileName);
    }
    
    @After
    public void tearDown() {
        testAndDelete(someFile);
        testAndDelete(someDir);
        testAndDelete(rootDir);
        testAndDelete(differentDir);
        testAndDelete(testDir);
        rootDir = null;
        differentDir = null;
        testDir = null;
        someDir = null;
        someFile = null;
        
        adaptee = null;
        dsFileObject = null;
    }
    
    @Test
    public void testGetURI1() throws org.apache.commons.vfs.FileSystemException, MalformedURIException,
            FileSystemException {
        final FileObject rootFileObject = fileSystemManager.resolveFile("file://" + rootDirPath);
        final FileName mountintPointFileName = rootFileObject.getName();
        final FileObject rootAdaptee = rootFileObject;
        final DataSpacesFileObject fo = new VFSFileObjectAdapter(rootAdaptee, mountingPointURI,
            mountintPointFileName);

        assertEquals("vfs:///1/scratch/rt1/node1/ao1", fo.getURI());
    }

    @Test
    public void testGetURI2() throws FileSystemException {
        assertEquals("vfs:///1/scratch/rt1/node1/ao1/dir/file.txt", dsFileObject.getURI());
    }
    
    @Test
    public void testGetParent() throws FileSystemException {
        DataSpacesFileObject parent = dsFileObject.getParent();
        assertIsSomeDir(parent);
    }

    @Test
    public void testResolveParent() throws FileSystemException {
        DataSpacesFileObject parent = dsFileObject.resolveFile("..");
        assertIsSomeDir(parent);
    }
    
    @Test(expected = FileSystemException.class)
    public void testResolveExceedsRoot() throws FileSystemException {
        dsFileObject.resolveFile("../../../");
    }
    
    @Test(expected = FileSystemException.class)
    public void testGetParentExceedsRoot() throws FileSystemException {
        DataSpacesFileObject grandParent = dsFileObject.getParent().getParent();
        grandParent.getParent();
    }
    
    @Test(expected = MalformedURIException.class)
    public void testMismatchedRoot() throws MalformedURIException, org.apache.commons.vfs.FileSystemException {
        final FileName diffName;
        diffName = fileSystemManager.resolveFile(differentDirPath).getName();
        new VFSFileObjectAdapter(adaptee, mountingPointURI, diffName);
        }
    
    private void assertIsSomeDir(DataSpacesFileObject parent) throws FileSystemException {
        assertEquals("vfs:///1/scratch/rt1/node1/ao1/dir", parent.getURI());
        final List<DataSpacesFileObject> desc = parent.getChildren();
        assertEquals(1, desc.size());
        assertTrue(desc.contains(dsFileObject));
        assertEquals(dsFileObject, parent.getChild("file.txt"));
    }
    
    private void testAndDelete(File f) {
        if (f != null)
            assertTrue(f.delete());
    }
}
