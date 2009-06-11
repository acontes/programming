package org.objectweb.proactive.examples.dataspaces.hello;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extra.dataspaces.PADataSpaces;
import org.objectweb.proactive.extra.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.DataSpacesException;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;


@ActiveObject
public class ExampleProcessing implements Serializable {

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    private static final String FINAL_RESULTS_FILENAME = "final_results.txt";
    private static final String PARTIAL_RESULTS_FILENAME = "partial_results.txt";

    /**
     * Returns BufferedWriter of specified file's content.
     * 
     * @param outputFile
     *            file of which content writer is to be returned
     * @return writer of a file's content
     * @throws FileSystemException
     */
    private static OutputStreamWriter getWriter(final DataSpacesFileObject outputFile)
            throws FileSystemException {
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
    private static BufferedReader getReader(final DataSpacesFileObject inputFile) throws FileSystemException {
        final InputStream is = inputFile.getContent().getInputStream();
        return new BufferedReader(new InputStreamReader(is));
    }

    private static void closeResource(Object resource) {
        if (resource == null)
            return;

        try {
            if (resource instanceof Closeable)
                ((Closeable) resource).close();
            else if (resource instanceof DataSpacesFileObject)
                ((DataSpacesFileObject) resource).close();
        } catch (IOException e) {
            ProActiveLogger.logEatedException(logger, e);
        }
    }

    public ExampleProcessing() {
    }

    /**
     * Creates file within AO's scratch and writes content there.
     * 
     * @param fileName
     *            name of a file to create within AO's scratch
     * @param content
     *            file content to write
     * @return URI of written file
     * @throws NotConfiguredException
     *             when scratch data space hasn't been configured
     * @throws IOException
     *             when IO exception occurred during file writing
     * @throws ConfigurationException
     *             when wrong configuration has been provided (capabilities of a FS)
     * 
     */
    public String writeIntoScratchFile(String fileName, String content) throws NotConfiguredException,
            IOException, ConfigurationException {
        DataSpacesFileObject file = null;
        OutputStreamWriter writer = null;

        try {
            file = PADataSpaces.resolveScratchForAO(fileName);
            file.createFile();
            writer = getWriter(file);
            writer.write(content);

            return PADataSpaces.getURI(file);
        } catch (IOException e) {
            logger.error("Exception while IO operation", e);
            throw e;
        } finally {
            closeResource(writer);
            closeResource(file);
        }
    }

    /**
     * Computes number of lines of a document from specified input data space name, and writes
     * partial results into a file in it's scratch. URI of file within AO's scratch data space is
     * returned.
     * 
     * @param inputName
     *            name of input data space containing document to process
     * @return URI of file with partial results
     * @throws SpaceNotFoundException
     *             if specified input data space cannot be resolved
     * @throws NotConfiguredException
     *             this AO's scratch hasn't been configured
     * @throws IOException
     *             when IO exception occurred during writing partial results
     * @throws ConfigurationException
     *             when wrong configuration has been provided (capabilities of a FS)
     */
    public StringWrapper computePartials(String inputName) throws SpaceNotFoundException,
            NotConfiguredException, IOException, ConfigurationException {

        logger.info("Processing input " + inputName);
        DataSpacesFileObject inputFile = null;
        BufferedReader reader = null;
        int lines = 0;

        try {
            inputFile = PADataSpaces.resolveInput(inputName);
            reader = getReader(inputFile);

            while (reader.readLine() != null)
                lines++;

            StringBuffer sb = new StringBuffer();
            sb.append(inputName).append(": ").append(lines).append('\n');
            String fileUri = writeIntoScratchFile(PARTIAL_RESULTS_FILENAME, sb.toString());
            logger.info("partial results written: " + sb.toString());

            return new StringWrapper(fileUri);
        } catch (IOException e) {
            logger.error("Exception while IO operation", e);
            throw e;
        } finally {
            closeResource(reader);
            closeResource(inputFile);
        }
    }

    /**
     * Gathers all partials results from specified scratches into one output file within output data
     * space.
     * 
     * @param partialResults
     *            list of file URIs containing partial results
     * @throws MalformedURIException
     *             when any specified URI is not correctly formed
     * @throws DataSpacesException
     *             when resolving default output has failed
     * @throws IOException
     *             when IO exception occurred during writing final results (failures in reading
     *             partial results are ignored)
     */
    public void gatherPartials(List<StringWrapper> partialResults) throws MalformedURIException,
            DataSpacesException, IOException {
        logger.info("Gathering and aggregating partial results");

        final List<String> results = new ArrayList<String>();
        for (StringWrapper uriWrapped : partialResults) {
            DataSpacesFileObject partialResultsFile = null;
            BufferedReader reader = null;
            try {
                partialResultsFile = PADataSpaces.resolveFile(uriWrapped.stringValue());
                reader = getReader(partialResultsFile);
                results.add(reader.readLine());
            } catch (IOException x) {
                logger.error("Reading one's partial result file failed, trying to continue", x);
            } finally {
                closeResource(reader);
                closeResource(partialResultsFile);
            }
        }

        DataSpacesFileObject outputFile = null;
        OutputStreamWriter writer = null;
        try {
            outputFile = PADataSpaces.resolveDefaultOutput(FINAL_RESULTS_FILENAME);
            outputFile.createFile();
            writer = getWriter(outputFile);

            for (String line : results)
                if (line != null) {
                    writer.write(line);
                    writer.write('\n');
                }
            logger.info("Results gathered, partial results number: " + results.size());
        } catch (IOException e) {
            logger.error("Exception while IO operation", e);
            throw e;
        } finally {
            closeResource(writer);
            closeResource(outputFile);
        }
    }
}
