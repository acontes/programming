package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.temp.TemporaryFileProvider;
import org.apache.commons.vfs.util.RandomAccessMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.proactive.extra.dataspaces.DataSpacesFileObject;
import org.objectweb.proactive.extra.dataspaces.DataSpacesFileObjectImpl;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.LimitingPolicy;
import org.objectweb.proactive.extra.dataspaces.VFSFactory;


/**
 * Test for write limiting and keeping (Abstract)FileObject behavior (which is not so obvious,
 * especially regarding unusual behavior like non-existing file, null array etc.).
 */
public class DataSpacesFileObjectImplTest {
    private static final String CHILD_NAME = "abc";
    private static final DataSpacesURI fakeUri = DataSpacesURI.createScratchSpaceURI(0, "0", "1");
    private FileObject realFile;
    private FileObject anotherFile;
    private DataSpacesFileObjectImpl readOnlyFile;
    private DataSpacesFileObjectImpl readWriteFile;
    private DefaultFileSystemManager manager;

    @Before
    public void setUp() throws Exception {
        manager = VFSFactory.createDefaultFileSystemManager();
        manager.addProvider("tmpfs", new TemporaryFileProvider());

        realFile = manager.resolveFile("tmpfs:///test1/test2");

        readWriteFile = new DataSpacesFileObjectImpl(realFile, fakeUri, realFile.getName().getPath());
        readOnlyFile = new DataSpacesFileObjectImpl(realFile, fakeUri, realFile.getName().getPath());
        readOnlyFile.setLimitingPolicy(new ReadOnlyPolicy());

        anotherFile = manager.resolveFile("tmpfs:///test2");
        anotherFile.createFile();

        assertFalse(readOnlyFile.exists());
        assertFalse(readWriteFile.exists());
        assertTrue(anotherFile.exists());
    }

    private void createRealFile() throws FileSystemException {
        realFile.createFile();
        assertTrue(readOnlyFile.exists());
    }

    private void createRealFolder() throws FileSystemException {
        realFile.createFolder();
        assertTrue(readOnlyFile.exists());
    }

    private void createRealFileChild() throws FileSystemException {
        realFile.createFolder();
        final FileObject childFile = realFile.resolveFile(CHILD_NAME);
        childFile.createFile();
        assertTrue(childFile.exists());
    }

    @After
    public void tearDown() throws Exception {
        if (realFile != null) {
            realFile.close();
            realFile = null;
        }
        if (manager != null) {
            manager.close();
            manager = null;
        }
    }

    @Test
    public void testReadOnlyCreateFile() throws FileSystemException {
        try {
            readOnlyFile.createFile();
            fail("Expected exception");
        } catch (FileSystemException e) {
        }
        assertFalse(readOnlyFile.exists());
    }

    @Test
    public void testReadOnlyCreateFolder() throws FileSystemException {
        try {
            readOnlyFile.createFolder();
            fail("Expected exception");
        } catch (FileSystemException e) {
        }
        assertFalse(readOnlyFile.exists());
    }

    @Test
    public void testReadOnlyDelete() throws FileSystemException {
        createRealFile();

        try {
            readOnlyFile.delete();
            fail("Expected exception");
        } catch (FileSystemException e) {
        }
        assertTrue(readOnlyFile.exists());
    }

    @Test
    public void testReadOnlyDeleteFileSelector() throws FileSystemException {
        createRealFile();

        try {
            readOnlyFile.delete(Selectors.SELECT_ALL);
            fail("Expected exception");
        } catch (FileSystemException e) {
        }
        assertTrue(readOnlyFile.exists());
    }

    @Test
    public void testReadOnlyIsWriteable() throws FileSystemException {
        assertFalse(readOnlyFile.isWriteable());
    }

    @Test
    public void testReadOnlyCanRenameToFileObjectSource() {
        assertFalse(readOnlyFile.canRenameTo(anotherFile));
    }

    // limitation of AbstractLimitingFileObject
    @Ignore
    @Test
    public void testReadOnlyCanRenameToFileObjectDesination() {
        assertFalse(anotherFile.canRenameTo(readOnlyFile));
    }

    @Test
    public void testReadOnlyCopyFromFileObjectFileSelector() throws FileSystemException {
        try {
            readOnlyFile.copyFrom(anotherFile, Selectors.SELECT_ALL);
            fail("Expected exception");
        } catch (FileSystemException e) {
        }
        assertFalse(readOnlyFile.exists());
    }

    @Test
    public void testReadOnlyMoveToFileObjectSource() throws FileSystemException {
        createRealFile();

        try {
            readOnlyFile.moveTo(anotherFile);
            fail("Expected exception");
        } catch (FileSystemException e) {
        }
        assertTrue(readOnlyFile.exists());
    }

    @Test
    public void testReadOnlyMoveToFileObjectDestination() throws FileSystemException {
        createRealFile();
        try {
            anotherFile.moveTo(readOnlyFile);
            fail("Expected exception");
        } catch (FileSystemException e) {
        }
        assertTrue(anotherFile.exists());
    }

    @Test
    public void testReadOnlyFindFilesFileSelector() throws FileSystemException {
        createRealFileChild();

        final FileObject result[] = readOnlyFile.findFiles(Selectors.SELECT_CHILDREN);
        assertEquals(1, result.length);
        assertFalse(result[0].isWriteable());
    }

    @Test
    public void testReadOnlyFindFilesFileSelectorNonExisting() throws FileSystemException {
        final FileObject result[] = readOnlyFile.findFiles(Selectors.SELECT_CHILDREN);
        assertNull(result);
    }

    @Test
    public void testReadOnlyFindFilesFileSelectorBooleanList() throws FileSystemException {
        createRealFileChild();

        final ArrayList<FileObject> result = new ArrayList<FileObject>();
        readOnlyFile.findFiles(Selectors.SELECT_CHILDREN, true, result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isWriteable());
    }

    @Test
    public void testReadOnlyResolveFileString() throws FileSystemException {
        createRealFileChild();

        final FileObject childFile = readOnlyFile.resolveFile(CHILD_NAME);
        assertNotNull(childFile);
        assertFalse(childFile.isWriteable());
    }

    @Test
    public void testReadOnlyResolveFileStringNameScope() throws FileSystemException {
        createRealFileChild();

        final FileObject childFile = readOnlyFile.resolveFile(CHILD_NAME, NameScope.CHILD);
        assertNotNull(childFile);
        assertFalse(childFile.isWriteable());
    }

    @Test
    public void testReadOnlyGetChildStringExisting() throws FileSystemException {
        createRealFileChild();

        final FileObject childFile = readOnlyFile.getChild(CHILD_NAME);
        assertNotNull(childFile);
        assertFalse(childFile.isWriteable());
    }

    @Test
    public void testReadOnlyGetChildStringNonExisting() throws FileSystemException {
        createRealFolder();

        final FileObject childFile = readOnlyFile.getChild(CHILD_NAME);
        assertNull(childFile);
    }

    @Test
    public void testReadOnlyGetChildrenExisting() throws FileSystemException {
        createRealFileChild();

        final FileObject childrenFiles[] = readOnlyFile.getChildren();
        assertNotNull(childrenFiles);
        assertEquals(1, childrenFiles.length);
        assertFalse(childrenFiles[0].isWriteable());
    }

    @Test
    public void testReadOnlyGetChildrenNonExisting() throws FileSystemException {
        createRealFolder();

        final FileObject childrenFiles[] = readOnlyFile.getChildren();
        assertNotNull(childrenFiles);
        assertEquals(0, childrenFiles.length);
    }

    @Test
    public void testReadOnlyGetParent() throws FileSystemException {
        final FileObject parent = readOnlyFile.getParent();
        assertNotNull(parent);
        assertFalse(parent.isWriteable());
    }

    // FIXME 
    /*
     * @Test public void testReadOnlyGetParentForRoot() throws FileSystemException { final
     * DataSpacesFileObjectImpl root = new DataSpacesFileObjectImpl(readOnlyFile.getFileSystem()
     * .getRoot(), fakeUri);
     * 
     * root.setLimitingPolicy(new ReadOnlyPolicy());
     * 
     * final FileObject parent = root.getParent(); assertNull(parent); }
     */

    @Test
    public void testReadOnlyGetContentInputStream() throws IOException {
        createRealFile();

        final FileContent content = readOnlyFile.getContent();
        try {
            content.getInputStream().close();
        } finally {
            content.close();
        }
    }

    @Test
    public void testReadOnlyGetContentOutputStream() throws IOException {
        createRealFile();

        final FileContent content = readOnlyFile.getContent();
        try {
            content.getOutputStream();
            fail("Expected exception");
        } catch (FileSystemException x) {
        } finally {
            content.close();
        }
    }

    @Test
    public void testReadOnlyGetContentRandomInputStream() throws IOException {
        createRealFile();

        final FileContent content = readOnlyFile.getContent();
        try {
            content.getRandomAccessContent(RandomAccessMode.READ).close();
        } finally {
            content.close();
        }
    }

    @Test
    public void testReadOnlyGetContentRandomOutputStream() throws IOException {
        createRealFile();

        final FileContent content = readOnlyFile.getContent();
        try {
            content.getRandomAccessContent(RandomAccessMode.READWRITE).close();
            fail("Expected exception");
        } catch (FileSystemException x) {
        } finally {
            content.close();
        }
    }

    //FIXME: VFS bug/limitation: VFS-259
    @Ignore
    @Test
    public void testReadOnlyGetContentGetFile() throws FileSystemException {
        final FileObject sameFile = readOnlyFile.getContent().getFile();
        assertFalse(sameFile.isWriteable());
    }

    @Test
    public void testReadWriteCreateFile() throws FileSystemException {
        readWriteFile.createFile();
        assertTrue(readWriteFile.exists());
    }

    @Test
    public void testReadWriteCreateFolder() throws FileSystemException {
        readWriteFile.createFolder();
        assertTrue(readWriteFile.exists());
    }

    @Test
    public void testReadWriteDelete() throws FileSystemException {
        createRealFile();
        readWriteFile.delete();
        assertFalse(readWriteFile.exists());
    }

    @Test
    public void testReadWriteDeleteFileSelector() throws FileSystemException {
        createRealFile();
        readWriteFile.delete(Selectors.SELECT_ALL);
        assertFalse(readWriteFile.exists());
    }

    @Test
    public void testReadWriteIsWriteable() throws FileSystemException {
        createRealFile();
        assertTrue(readWriteFile.isWriteable());
    }

    @Test
    public void testReadWriteCanRenameToFileObjectSource() {
        assertTrue(readWriteFile.canRenameTo(anotherFile));
    }

    @Test
    public void testReadWriteCanRenameToFileObjectDesination() {
        assertTrue(anotherFile.canRenameTo(readWriteFile));
    }

    @Test
    public void testReadWriteCopyFromFileObjectFileSelector() throws FileSystemException {
        readWriteFile.copyFrom(anotherFile, Selectors.SELECT_ALL);
        assertTrue(readWriteFile.exists());
    }

    @Test
    public void testReadWriteMoveToFileObjectSource() throws FileSystemException {
        createRealFile();

        readWriteFile.moveTo(anotherFile);
        assertFalse(readWriteFile.exists());
    }

    @Test
    public void testReadWriteMoveToFileObjectDestination() throws FileSystemException {
        createRealFile();
        anotherFile.moveTo(readWriteFile);
        assertFalse(anotherFile.exists());
    }

    @Test
    public void testReadWriteFindFilesFileSelector() throws FileSystemException {
        createRealFileChild();

        final FileObject result[] = readWriteFile.findFiles(Selectors.SELECT_CHILDREN);
        assertEquals(1, result.length);
        assertTrue(result[0].isWriteable());
    }

    @Test
    public void testReadWriteFindFilesFileSelectorNonExisting() throws FileSystemException {
        final FileObject result[] = readWriteFile.findFiles(Selectors.SELECT_CHILDREN);
        assertNull(result);
    }

    @Test
    public void testReadWriteFindFilesFileSelectorBooleanList() throws FileSystemException {
        createRealFileChild();

        final ArrayList<FileObject> result = new ArrayList<FileObject>();
        readWriteFile.findFiles(Selectors.SELECT_CHILDREN, true, result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isWriteable());
    }

    @Test
    public void testReadWriteResolveFileString() throws FileSystemException {
        createRealFileChild();

        final FileObject childFile = readWriteFile.resolveFile(CHILD_NAME);
        assertNotNull(childFile);
        assertTrue(childFile.isWriteable());
    }

    @Test
    public void testReadWriteResolveFileStringNameScope() throws FileSystemException {
        createRealFileChild();

        final FileObject childFile = readWriteFile.resolveFile(CHILD_NAME, NameScope.CHILD);
        assertNotNull(childFile);
        assertTrue(childFile.isWriteable());
    }

    @Test
    public void testReadWriteGetChildStringExisting() throws FileSystemException {
        createRealFileChild();

        final FileObject childFile = readWriteFile.getChild(CHILD_NAME);
        assertNotNull(childFile);
        assertTrue(childFile.isWriteable());
    }

    @Test
    public void testReadWriteGetChildStringNonExisting() throws FileSystemException {
        createRealFolder();

        final FileObject childFile = readWriteFile.getChild(CHILD_NAME);
        assertNull(childFile);
    }

    @Test
    public void testReadWriteGetChildrenExisting() throws FileSystemException {
        createRealFileChild();

        final FileObject childrenFiles[] = readWriteFile.getChildren();
        assertNotNull(childrenFiles);
        assertEquals(1, childrenFiles.length);
        assertTrue(childrenFiles[0].isWriteable());
    }

    @Test
    public void testReadWriteGetChildrenNonExisting() throws FileSystemException {
        createRealFolder();

        final FileObject childrenFiles[] = readWriteFile.getChildren();
        assertNotNull(childrenFiles);
        assertEquals(0, childrenFiles.length);
    }

    @Test
    public void testWriteOnlyGetParent() throws FileSystemException {
        final FileObject parent = readWriteFile.getParent();
        assertNotNull(parent);
        assertTrue(parent.isWriteable());
    }

    // FIXME
    /*
     * @Test public void testReadWriteGetParentForRoot() throws FileSystemException { final
     * DataSpacesFileObjectImpl root = new DataSpacesFileObjectImpl(readOnlyFile.getFileSystem()
     * .getRoot(), fakeUri);
     * 
     * final FileObject parent = root.getParent(); assertNull(parent); }
     */

    @Test
    public void testReadWriteGetContentInputStream() throws IOException {
        createRealFile();

        final FileContent content = readWriteFile.getContent();
        try {
            content.getInputStream().close();
        } finally {
            content.close();
        }
    }

    @Test
    public void testReadWriteGetContentOutputStream() throws IOException {
        createRealFile();

        final FileContent content = readWriteFile.getContent();
        try {
            content.getOutputStream();
        } finally {
            content.close();
        }
    }

    @Test
    public void testReadWriteGetContentRandomInputStream() throws IOException {
        createRealFile();

        final FileContent content = readWriteFile.getContent();
        try {
            content.getRandomAccessContent(RandomAccessMode.READ).close();
        } finally {
            content.close();
        }
    }

    @Test
    public void testReadWriteGetContentRandomOutputStream() throws IOException {
        createRealFile();

        final FileContent content = readWriteFile.getContent();
        try {
            content.getRandomAccessContent(RandomAccessMode.READWRITE).close();
        } finally {
            content.close();
        }
    }

    @Test
    public void testReadWriteGetContentGetFile() throws FileSystemException {
        final FileObject sameFile = readWriteFile.getContent().getFile();
        assertTrue(sameFile.isWriteable());
    }

    private static class ReadOnlyPolicy implements LimitingPolicy {
        public boolean isReadOnly() {
            return true;
        }

        public LimitingPolicy newInstance(DataSpacesFileObject newFileObject) {
            return this;
        }
    }
}
