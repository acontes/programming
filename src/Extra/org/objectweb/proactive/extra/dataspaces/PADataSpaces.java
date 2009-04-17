/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.extra.dataspaces.exceptions.DataSpacesConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;

// TODO fix the javadoc!
/**
 * The ProActive Data Spaces API. (delegates method calls to DataSpacesImpl)
 */
public class PADataSpaces {

	private PADataSpaces() {
	}

	/**
	 * <p>
	 * Returns file handle for the <i>default input data space</i>, as defined
	 * in application descriptor or dynamically set through API during
	 * application execution. Returned file handle can be directly used to
	 * perform operations on the file/directory, regardless of the underlying
	 * protocol.
	 * </p>
	 * <p>
	 * Input is expected to be readable.
	 * </p>
	 * 
	 * TODO it can block when input needs to be mounted (timeout exception is
	 * being thrown after some configurable period).
	 *
	 * @see {@link #resolveDefaultInputBlocking(long)}
	 * @return File handle for the default input data space
	 * @throws SpaceNotFoundException
	 *             when no default input data space defined
	 * @throws FileSystemException
	 *             indicates VFS related exception
	 */
	public static FileObject resolveDefaultInput() throws FileSystemException, SpaceNotFoundException {
		return DataSpacesNodes.getDataSpacesImpl().resolveDefaultInputOutput(SpaceType.INPUT);
	}

	/**
	 * <p>
	 * Returns file handle for the <i>default output data space</i>, as defined
	 * in application descriptor or dynamically set through API during
	 * application execution. Returned file handle can be directly used to
	 * perform operations on the file/directory, regardless of the underlying
	 * protocol. Writes synchronization is a developer’s responsibility.
	 * </p>
	 * <p>
	 * Output is expected to be writable from any node.
	 * </p>
	 * 
	 * @see {@link #resolveDefaultOutputBlocking(long)}
	 * @return File handle for the default output data space
	 * @throws SpaceNotFoundException
	 *             when no default input data space defined
	 * @throws FileSystemException
	 *             indicates VFS related exception
	 * @throws SpaceNotFoundException
	 */
	public static FileObject resolveDefaultOutput() throws FileSystemException, SpaceNotFoundException {
		return DataSpacesNodes.getDataSpacesImpl().resolveDefaultInputOutput(SpaceType.OUTPUT);
	}

	/**
	 * <p>
	 * Returns file handle for calling Active Object's <i>scratch data
	 * space</i>. If such a scratch has not existed before, it is created in its
	 * node scratch data space (as configured in deployment descriptor).
	 * </p>
	 * <p>
	 * Returned scratch is expected to be writable by this Active Object and
	 * readable by others.
	 * </p>
	 * 
	 * @return
	 * @throws FileSystemException
	 * @throws DataSpacesConfigurationException
	 */
	public static FileObject resolveScratchForAO() throws FileSystemException,
			DataSpacesConfigurationException {

		return DataSpacesNodes.getDataSpacesImpl().resolveScratchForAO();
	}

	/**
	 * Returns names of every deﬁned inputs known at this time. It does not
	 * necessarily mean that they are already mountable (nodes and ProActive-
	 * Providers may not have started yet) nor mounted. If inputs are available
	 * at static application descriptor, every name is guaranteed to be
	 * returned. If some another addInput() method returned before this call was
	 * made (locally precede this call or precede it in global real-time), name
	 * of this input is also returned. This method doesn’t cause inputs to be
	 * mounted, i.e. it doesn’t cause local VFS view to be refreshed.
	 * 
	 * @return
	 */
	public static Set<String> getAllKnownInputNames() {
		return DataSpacesNodes.getDataSpacesImpl().getAllKnownInputOutputNames(SpaceType.INPUT);
	}

	/**
	 * Analogous method for outputs.
	 * 
	 * @return
	 */
	public static Set<String> getAllKnownOutputNames() {
		return DataSpacesNodes.getDataSpacesImpl().getAllKnownInputOutputNames(SpaceType.OUTPUT);
	}

	/**
	 * Returns map of input names to ﬁle handles for every input known at this
	 * time (see getAllInputNames()). This call might block for a while if there
	 * is a need to wait for start up of some input provider or some input need
	 * to be mounted. Every input is mounted in result of this call, i.e. it
	 * does refresh local VFS view. Returned input handles are expected to be
	 * readable.
	 * 
	 * @return
	 * @throws FileSystemException
	 */
	public static Map<String, FileObject> resolveAllKnownInputs() throws FileSystemException {
		return DataSpacesNodes.getDataSpacesImpl().resolveAllKnownInputsOutputs(SpaceType.INPUT);
	}

	/**
	 * Analogous method for outputs.
	 * 
	 * @return
	 * @throws FileSystemException
	 */
	public static Map<String, FileObject> resolveAllKnownOutputs() throws FileSystemException {
		return DataSpacesNodes.getDataSpacesImpl().resolveAllKnownInputsOutputs(SpaceType.OUTPUT);
	}

	/**
	 * Blocking version of {@link #resolveDefaultInput()} for a case when no
	 * default input is defined. Method blocks until default input is defined or
	 * timeout expires.
	 * 
	 * @param timeoutMillis
	 * @return File handle for the default input data space
	 * @throws FileSystemException
	 *             indicates VFS related exception
	 * @throws IllegalArgumentException
	 *             specified timeout is not positive integer
	 * @throws ProActiveTimeoutException
	 */
	public static FileObject resolveDefaultInputBlocking(long timeoutMillis) throws IllegalArgumentException,
			FileSystemException, ProActiveTimeoutException {

		return DataSpacesNodes.getDataSpacesImpl().resolveDefaultInputOutputBlocking(timeoutMillis,
				SpaceType.INPUT);
	}

	/**
	 * Blocking version of {@link #resolveDefaultOutput()} for a case when no
	 * default output is defined. Method blocks until default input is defined
	 * or timeout expires.
	 * 
	 * @param timeoutMillis
	 * @return File handle for the default output data space
	 * @throws FileSystemException
	 *             indicates VFS related exception
	 * @throws IllegalArgumentException
	 *             specified timeout is not positive integer
	 * @throws ProActiveTimeoutException
	 */
	public static FileObject resolveDefaultOutputBlocking(long timeoutMillis)
			throws IllegalArgumentException, FileSystemException, ProActiveTimeoutException {

		return DataSpacesNodes.getDataSpacesImpl().resolveDefaultInputOutputBlocking(timeoutMillis,
				SpaceType.OUTPUT);
	}

	/**
	 * Returns ﬁle handle for any valid URI within some existing dataspace in
	 * application (another AO’s scratch, input or output). This call might
	 * block for a while if there is a need to wait for start up of some remote
	 * provider or it needs to be mounted. Returned ﬁle handle should be
	 * readable, but not necessarily writable. This kind of capabilities
	 * checking is caller’s respon- sibility or it can be implied from some
	 * objects contract (e.g. you know that input URI is passed to you).
	 * 
	 * @param uri
	 * @return
	 * @throws MalformedURIException
	 * @throws FileSystemException
	 * @throws SpaceNotFoundException
	 */
	public static FileObject resolveFile(String uri) throws FileSystemException, MalformedURIException,
			SpaceNotFoundException {

		return DataSpacesNodes.getDataSpacesImpl().resolveFile(uri);
	}

	/**
	 * Returns URI for a given ﬁle object, which is valid in the whole
	 * application. I.e. it can be passed to another AO and should be resolvable
	 * there.
	 * 
	 * @param fileObject
	 * @return
	 */
	public static String getURI(FileObject fileObject) {
		return DataSpacesNodes.getDataSpacesImpl().getURI(fileObject);
	}

	/**
	 * Returns ﬁle handle for an input with speciﬁc name. This call might block
	 * for a while if there is a need to wait for start up of input provider or
	 * input needs to be mounted (timeout exception is being thrown after some
	 * conﬁgurable period). Returned input is expected to be readable. In case
	 * of no input deﬁned with that name, it throws an exception.
	 * 
	 * @param name
	 * @return
	 * @throws FileSystemException
	 * @throws SpaceNotFoundException
	 */
	public static FileObject resolveInput(String name) throws FileSystemException, SpaceNotFoundException {
		return DataSpacesNodes.getDataSpacesImpl().resolveInputOutput(name, SpaceType.INPUT);
	}

	/**
	 * Analogous method for output.
	 * 
	 * @param name
	 * @return
	 * @throws FileSystemException
	 * @throws SpaceNotFoundException
	 */
	public static FileObject resolveOutput(String name) throws FileSystemException, SpaceNotFoundException {
		return DataSpacesNodes.getDataSpacesImpl().resolveInputOutput(name, SpaceType.OUTPUT);
	}

	/**
	 * Similar to resolveInput(name), except that in case of no input deﬁned
	 * with that name, method would block until such an input is deﬁned or
	 * timeout expires.
	 * 
	 * @param name
	 * @param timeoutMillis
	 * @return
	 * @throws ProActiveTimeoutException
	 * @throws IllegalArgumentException
	 *             specified timeout is not positive integer
	 * @throws FileSystemException
	 */
	public static FileObject resolveInputBlocking(String name, long timeoutMillis)
			throws FileSystemException, IllegalArgumentException, ProActiveTimeoutException {

		return DataSpacesNodes.getDataSpacesImpl().resolveInputOutputBlocking(name, timeoutMillis,
				SpaceType.INPUT);
	}

	/**
	 * Analogous blocking method for accessing output.
	 * 
	 * @param name
	 * @param timeoutMillis
	 * @return
	 * @throws ProActiveTimeoutException
	 * @throws IllegalArgumentException
	 *             specified timeout is not positive integer
	 * @throws FileSystemException
	 */
	public static FileObject resolveOutputBlocking(String name, long timeoutMillis)
			throws FileSystemException, IllegalArgumentException, ProActiveTimeoutException {

		return DataSpacesNodes.getDataSpacesImpl().resolveInputOutputBlocking(name, timeoutMillis,
				SpaceType.OUTPUT);
	}

	/**
	 * Add input with a provided name during application execution. Input with
	 * empty (null — implicitly "default") or "default" name becomes an
	 * application default input. Input name must be unique for the application,
	 * i.e. if some input with provided name is already registered, this method
	 * throws exception. Input must have local path and/or global access URL
	 * deﬁned. In case of path-based local-only access deﬁned, space is exposed
	 * for remote access through VFS ProActiveProvider; path given for local
	 * access is resolved in context of caller’s node local ﬁle system. In case
	 * of remote access deﬁned through URL, local access deﬁnition is optional,
	 * it may be used internally only for performance reasons. Returned URI of
	 * created input data space can be safely passed to another Active Objects.
	 * Given input name (which might be constant in code) can also be safely
	 * used by other Active Objects after this method returns.
	 * 
	 * @param name
	 * @param path
	 * @param url
	 * @return
	 */
	public static String addInput(String name, String path, String url) {
		return DataSpacesNodes.getDataSpacesImpl().addInputOutput(name, path, url, SpaceType.INPUT);
	}

	/**
	 * Analogous method for output.
	 * 
	 * @param name
	 * @param path
	 * @param url
	 * @return
	 */
	public static String addOutput(String name, String path, String url) {
		return DataSpacesNodes.getDataSpacesImpl().addInputOutput(name, path, url, SpaceType.OUTPUT);
	}

	/*
	 * public static FileObject resolveScratchForAO(String node: ) {
	 * 
	 * }
	 */
}
