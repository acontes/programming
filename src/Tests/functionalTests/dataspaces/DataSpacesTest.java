package functionalTests.dataspaces;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAException;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extra.dataspaces.PADataSpaces;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;


public class DataSpacesTest extends GCMFunctionalTestDataSpaces {
    private static final String ADDED_INPUT_NAME = "another_input";
    private static final String OUTPUT_FILE_NAME = "some_file.txt";
    private static final String OUTPUT_FILE_CONTENT = "didum";
    private Node node1;
    private Node node2;
    private TestActiveObject ao1;
    private TestActiveObject ao2;

    public DataSpacesTest() {
        super(2, 1);
    }

    @Before
    public void createTestActiveObjects() throws ActiveObjectCreationException, NodeException {
        node1 = getANode();
        node2 = getANode();
        ao1 = (TestActiveObject) PAActiveObject.newActive(TestActiveObject.class.getName(), null, node1);
        ao2 = (TestActiveObject) PAActiveObject.newActive(TestActiveObject.class.getName(), null, node2);
        // no need to @After, as whole GCMApp will be killed
    }

    @Test
    public void action() throws SpaceNotFoundException, NotConfiguredException, IOException,
            MalformedURIException, ProActiveTimeoutException, SpaceAlreadyRegisteredException,
            ConfigurationException {
        // read inputs:
        assertEquals(INPUT_FILE_CONTENT, ao1.readDefaultInputFile(INPUT_FILE_NAME));
        assertEquals(INPUT_FILE_CONTENT, ao2.readDefaultInputFile(INPUT_FILE_NAME));
        assertEquals(INPUT_FILE_CONTENT, ao1.readInputFile(INPUT_NAME, INPUT_FILE_NAME));
        assertEquals(INPUT_FILE_CONTENT, ao2.readInputFile(INPUT_NAME, INPUT_FILE_NAME));

        // write to outputs:
        final String defaultOutputFileUri = ao1.writeDefaultOutputFile(OUTPUT_FILE_NAME, OUTPUT_FILE_CONTENT);
        assertEquals(OUTPUT_FILE_CONTENT, ao2.readFile(defaultOutputFileUri));
        assertEquals(OUTPUT_FILE_CONTENT, ao1.readFile(defaultOutputFileUri));

        final String outputFileUri = ao1.writeOutputFile(OUTPUT_NAME, OUTPUT_FILE_NAME, OUTPUT_FILE_CONTENT);
        assertEquals(OUTPUT_FILE_CONTENT, ao2.readFile(outputFileUri));
        assertEquals(OUTPUT_FILE_CONTENT, ao1.readFile(outputFileUri));

        // write to scratch:
        final String scratchFileUri = ao1.writeScratchFile(OUTPUT_FILE_NAME, OUTPUT_FILE_CONTENT);
        assertEquals(OUTPUT_FILE_CONTENT, ao2.readFile(scratchFileUri));
        assertEquals(OUTPUT_FILE_CONTENT, ao1.readFile(scratchFileUri));

        // read inputs names:
        final HashSet<String> expectedInputs = new HashSet<String>();
        expectedInputs.add(PADataSpaces.DEFAULT_IN_OUT_NAME);
        expectedInputs.add(INPUT_NAME);
        assertEquals(expectedInputs, ao1.getAllKnownInputsNames());
        assertEquals(expectedInputs, ao2.getAllKnownInputsNames());

        // read outputs names:
        final HashSet<String> expectedOutputs = new HashSet<String>();
        expectedOutputs.add(PADataSpaces.DEFAULT_IN_OUT_NAME);
        expectedOutputs.add(OUTPUT_NAME);
        assertEquals(expectedInputs, ao1.getAllKnownOutputsNames());
        assertEquals(expectedInputs, ao2.getAllKnownOutputsNames());

        // blocking resolve input + add input:
        // (exceptions for readInputFileBlocking - to make it asynchronous)
        PAException.tryWithCatch(new Class[] { SpaceNotFoundException.class, NotConfiguredException.class,
                IOException.class, ProActiveTimeoutException.class });
        try {
            final StringWrapper contentWrapper = ao2.readInputFileBlocking(ADDED_INPUT_NAME, INPUT_FILE_NAME,
                    30000);
            ao1.addInputSpace(ADDED_INPUT_NAME, inputDir.getAbsolutePath());
            assertEquals(INPUT_FILE_CONTENT, contentWrapper.stringValue());
            PAException.endTryWithCatch();
        } finally {
            PAException.removeTryWithCatch();
        }
        expectedInputs.add(ADDED_INPUT_NAME);
        assertEquals(expectedInputs, ao1.getAllKnownInputsNames());
        assertEquals(expectedInputs, ao2.getAllKnownInputsNames());

        // TODO tests for deployer node?
        // TODO tests for 2 nodes on the same JVM?
    }

    @ActiveObject
    public static class TestActiveObject implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 3003545576343285983L;

        private static String readAndCloseFile(final FileObject fo) throws FileSystemException, IOException {
            try {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(fo.getContent()
                        .getInputStream()));
                try {
                    return reader.readLine();
                } finally {
                    reader.close();
                }
            } finally {
                fo.close();
            }
        }

        private static String writeAndCloseFile(final FileObject fo, String content)
                throws FileSystemException, IOException {
            try {
                final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fo.getContent()
                        .getOutputStream()));
                try {
                    writer.write(content);
                    return PADataSpaces.getURI(fo);
                } finally {
                    writer.close();
                }
            } finally {
                fo.close();
            }
        }

        public TestActiveObject() {
        }

        public String readDefaultInputFile(String fileName) throws SpaceNotFoundException,
                NotConfiguredException, IOException {
            final FileObject fo = PADataSpaces.resolveDefaultInput(fileName);
            try {
                return readAndCloseFile(fo);
            } finally {
                fo.close();
            }
        }

        public String readInputFile(String inputName, String fileName) throws SpaceNotFoundException,
                NotConfiguredException, IOException {
            final FileObject fo = PADataSpaces.resolveInput(inputName, fileName);
            return readAndCloseFile(fo);
        }

        public String readFile(String uri) throws SpaceNotFoundException, NotConfiguredException,
                IOException, MalformedURIException {
            final FileObject fo = PADataSpaces.resolveFile(uri);
            return readAndCloseFile(fo);
        }

        public String writeDefaultOutputFile(String fileName, String content) throws FileSystemException,
                IOException, SpaceNotFoundException, NotConfiguredException {
            final FileObject fo = PADataSpaces.resolveDefaultOutput(fileName);
            return writeAndCloseFile(fo, content);
        }

        public String writeOutputFile(String outputName, String fileName, String content)
                throws FileSystemException, IOException, SpaceNotFoundException, NotConfiguredException {
            final FileObject fo = PADataSpaces.resolveOutput(outputName, fileName);
            return writeAndCloseFile(fo, content);
        }

        public String writeScratchFile(String fileName, String content) throws FileSystemException,
                IOException, SpaceNotFoundException, NotConfiguredException {
            final FileObject fo = PADataSpaces.resolveScratchForAO(fileName);
            return writeAndCloseFile(fo, content);
        }

        public String writeFile(String uri, String content) throws FileSystemException, IOException,
                SpaceNotFoundException, NotConfiguredException, MalformedURIException {
            final FileObject fo = PADataSpaces.resolveFile(uri);
            return writeAndCloseFile(fo, content);
        }

        public Set<String> getAllKnownInputsNames() throws NotConfiguredException {
            return PADataSpaces.getAllKnownInputNames();
        }

        public Set<String> getAllKnownOutputsNames() throws NotConfiguredException {
            return PADataSpaces.getAllKnownInputNames();
        }

        public StringWrapper readInputFileBlocking(String inputName, String fileName, long timeout)
                throws SpaceNotFoundException, NotConfiguredException, IOException, ProActiveTimeoutException {
            final FileObject fo = PADataSpaces.resolveInputBlocking(inputName, fileName, timeout);
            return new StringWrapper(readAndCloseFile(fo));
        }

        public void addInputSpace(String inputName, String url) throws SpaceAlreadyRegisteredException,
                ConfigurationException, NotConfiguredException {
            PADataSpaces.addInput(inputName, url, null);
        }
    }

}