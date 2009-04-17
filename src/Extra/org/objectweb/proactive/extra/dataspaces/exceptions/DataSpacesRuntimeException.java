/**
 *
 */
package org.objectweb.proactive.extra.dataspaces.exceptions;

import org.objectweb.proactive.core.ProActiveRuntimeException;

/**
 *
 *
 */
public class DataSpacesRuntimeException extends ProActiveRuntimeException {

	/**
	 *
	 */
	public DataSpacesRuntimeException() {
	}

	/**
	 * @param message
	 */
	public DataSpacesRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataSpacesRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public DataSpacesRuntimeException(Throwable cause) {
		super(cause);
	}

}
