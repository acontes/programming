package org.objectweb.proactive.examples.dataspaces.hello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extra.dataspaces.PADataSpaces;
import org.objectweb.proactive.extra.dataspaces.exceptions.DataSpacesException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;


@ActiveObject
public class ExampleProcessing implements Serializable {

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    private static final String FINAL_RESULTS_FILENAME = "final_results.txt";
    private static final String PARTIAL_RESULTS_FILENAME = "partial_results.txt";

    public ExampleProcessing() {
    }

    /**
     * Resolves AO's scratch and creates there a file with provided content.
     * 
     * @param fname
     *            name of a file to create
     * @param content
     *            file content to write
     * @throws NotConfiguredException
     *             when scratch data space hasn't been configured
     */
    public void writeIntoScratchFile(String fname, String content) throws NotConfiguredException {
        FileObject scratch = null;
        FileObject file = null;
        OutputStreamWriter writer = null;

        try {
            scratch = PADataSpaces.resolveScratchForAO();
            file = scratch.resolveFile(fname);
            file.createFile();
            writer = getWriter(file);
            writer.write(content);
        } catch (IOException e) {
            logger.error("Exception while IO operation", e);
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
            }
            try {
                if (file != null)
                    file.close();
            } catch (FileSystemException e) {
            }
            try {
                if (scratch != null)
                    scratch.close();
            } catch (FileSystemException e) {
            }
        }
    }

    /**
     * Computes number of lines of a document from specified input data space name, and writes
     * partial results into a file in it's scratch. URI of AO's scratch data space is returned.
     * 
     * @param inputName
     *            name of input data space containing document to process
     * @return URI of AO's scratch or <code>null</code> if any IO operation has failed.
     * @throws SpaceNotFoundException
     *             if specified input data space cannot be resolved
     * @throws NotConfiguredException
     *             this AO's scratch hasn't been configured
     * 
     */
    public StringWrapper computePartials(String inputName) throws SpaceNotFoundException,
            NotConfiguredException {

        FileObject inputFile = null;
        BufferedReader reader = null;
        int lines = 0;

        try {
            inputFile = PADataSpaces.resolveInput(inputName);
            reader = getReader(inputFile);

            while (reader.readLine() != null)
                lines++;

            StringBuffer sb = new StringBuffer();
            sb.append(inputName).append(": ").append(lines).append('\n');
            writeIntoScratchFile(PARTIAL_RESULTS_FILENAME, sb.toString());
            logger.info("partial results written: " + sb.toString());

            return new StringWrapper(PADataSpaces.getURI(PADataSpaces.resolveScratchForAO()));

        } catch (IOException e) {
            logger.error("Exception while IO operation", e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
            }
            try {
                if (inputFile != null)
                    inputFile.close();
            } catch (FileSystemException e) {
            }
        }
        return null;
    }

    /**
     * Gathers all partials results from specified scratches into one output file within output data
     * space.
     * 
     * @param partialResults
     *            list of scratch URIs containing partial results
     * @throws MalformedURIException
     *             when any specified URI is not correctly formed
     * @throws DataSpacesException
     *             when resolving default output has failed
     */
    public void gatherPartials(List<String> partialResults) throws MalformedURIException, DataSpacesException {

        FileObject outputSpace = null;
        FileObject outputFile = null;
        FileObject scratchSpace = null;
        FileObject partialResultsFile = null;
        OutputStreamWriter writer = null;
        BufferedReader reader = null;

        try {
            final List<String> results = new ArrayList<String>();

            outputSpace = PADataSpaces.resolveDefaultOutput();

            for (String uri : partialResults) {
                try {
                    scratchSpace = PADataSpaces.resolveFile(uri);
                } catch (Exception e) {
                    logger.error("Resolving one's scratch has failed, trying to continue", e);
                    continue;
                }
                partialResultsFile = scratchSpace.resolveFile(PARTIAL_RESULTS_FILENAME);

                reader = getReader(partialResultsFile);
                try {
                    results.add(reader.readLine());
                } catch (Exception x) {
                    logger.error("Partial results reading error", x);
                } finally {
                    reader.close();
                }
            }

            outputFile = outputSpace.resolveFile(FINAL_RESULTS_FILENAME);
            outputFile.createFile();
            writer = getWriter(outputFile);

            for (String line : results)
                if (line != null) {
                    writer.write(line);
                    writer.write('\n');
                }
            logger.info("Results gathered, partial results count: " + results.size());
        } catch (IOException e) {
            logger.error("Exception while IO operation", e);
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
            }
            try {
                if (outputFile != null)
                    outputFile.close();
            } catch (FileSystemException e3) {
            }
            try {
                if (outputSpace != null)
                    outputSpace.close();
            } catch (FileSystemException e2) {
            }
            try {
                if (scratchSpace != null)
                    scratchSpace.close();
            } catch (FileSystemException e1) {
            }
            try {
                if (partialResultsFile != null)
                    partialResultsFile.close();
            } catch (FileSystemException e) {
            }
        }
    }

    /**
     * Returns BufferedWriter of specified file's content.
     * 
     * @param outputFile
     *            file of which content writer is to be returned
     * @return writer of a file's content
     * @throws FileSystemException
     */
    private OutputStreamWriter getWriter(final FileObject outputFile) throws FileSystemException {
        OutputStream os = outputFile.getContent().getOutputStream();
        return new OutputStreamWriter(os);
    }

    /**
     * Returns BufferedReader of specified file's content.
     * 
     * @param inputFile
     *            file of which content reader is to be returned
     * @return reader of a file's content
     * @throws FileSystemException
     */
    private BufferedReader getReader(final FileObject inputFile) throws FileSystemException {
        final InputStream is = inputFile.getContent().getInputStream();
        return new BufferedReader(new InputStreamReader(is));
    }
}
