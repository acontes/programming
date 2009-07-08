package org.objectweb.proactive.extra.vfsprovider.client;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.objectweb.proactive.extra.vfsprovider.exceptions.StreamNotFoundException;
import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;


/**
 * Error-handling utils class.
 */
class Utils {
    public static IOException generateAndLogIOExceptionWrongStreamType(Log log, WrongStreamTypeException e) {
        log
                .error("File server unexpectedly does not allow to perform some type of operation on an opened stream");
        return new IOException(
            "File server unexpectedly does not allow to perform some type of operation on an opened stream",
            e);
    }

    public static IOException generateAndLogIOExceptionStreamNotFound(Log log, StreamNotFoundException e) {
        log.error("File server unexpectedly closed (possibly reopened) file stream");
        return new IOException("File server unexpectedly closed (possibly reopened) file stream", e);
    }

    public static IOException generateAndLogIOExceptionCouldNotReopen(Log log, Exception x) {
        log.error("Could not reopen stream correctly");
        return new IOException("Could not reopen stream correctly", x);
    }
}
