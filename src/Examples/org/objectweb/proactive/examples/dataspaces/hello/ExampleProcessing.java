package org.objectweb.proactive.examples.dataspaces.hello;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extra.dataspaces.PADataSpaces;
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

    public void writeIntoScratchFile(String fname, String content) throws NotConfiguredException, IOException {
        final FileObject scratch = PADataSpaces.resolveScratchForAO();
        final FileObject dummy = scratch.resolveFile(fname);

        dummy.createFile();

        OutputStream os = dummy.getContent().getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writer.write(content);
        writer.close();
        scratch.close();
    }

    public StringWrapper computePartials(String inputName) {

        try {
            final FileObject inputFile = PADataSpaces.resolveInput(inputName);
            final InputStream is = inputFile.getContent().getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            int lines = 0;

            while (reader.readLine() != null)
                lines++;

            reader.close();
            inputFile.close();

            StringBuffer sb = new StringBuffer();
            sb.append(inputName).append(": ").append(lines).append('\n');
            writeIntoScratchFile(PARTIAL_RESULTS_FILENAME, sb.toString());
            logger.info("partial results written: " + sb.toString());

            return new StringWrapper(PADataSpaces.getURI(PADataSpaces.resolveScratchForAO()));

        } catch (SpaceNotFoundException e) {
            e.printStackTrace();
        } catch (NotConfiguredException e) {
            e.printStackTrace();
        } catch (FileSystemException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void gatherPartials(Set<String> partialResultsURIs) throws MalformedURIException {
        try {
            final List<String> results = new ArrayList<String>();
            final FileObject outputSpace = PADataSpaces.resolveDefaultOutput();

            for (String uri : partialResultsURIs) {
                final FileObject scratchSpace = PADataSpaces.resolveFile(uri);
                final FileObject partialResultsFile = scratchSpace.resolveFile(PARTIAL_RESULTS_FILENAME);

                final InputStream is = partialResultsFile.getContent().getInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                results.add(reader.readLine());
                partialResultsFile.close();
            }

            final FileObject outputFile = outputSpace.resolveFile(FINAL_RESULTS_FILENAME);
            outputFile.createFile();

            final OutputStream os = outputFile.getContent().getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

            for (String line : results) {
                writer.write(line);
                writer.write('\n');
            }

            writer.close();
            outputFile.close();
            outputSpace.close();

            logger.info("Results gathered, partial results no: " + results.size());

        } catch (SpaceNotFoundException e) {
            e.printStackTrace();
        } catch (NotConfiguredException e) {
            e.printStackTrace();
        } catch (FileSystemException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
