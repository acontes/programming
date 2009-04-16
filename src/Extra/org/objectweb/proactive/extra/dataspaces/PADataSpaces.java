/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs.FileObject;

/**
 * The ProActive Data Spaces API. (delegates method calls to DataSpacesImpl)
 */
public class PADataSpaces {

	private PADataSpaces() {
	}

	/**
	 * FileObject Returns ﬁle handle for the default input data space, as deﬁned
	 * in applica- tion descriptor or (optionally) dynamically set through API
	 * during appli- cation execution. This call might block for a while if
	 * there is a need to wait for start up of input provider or input needs to
	 * be mounted (timeout excep- tion is being thrown after some conﬁgurable
	 * period). Returned ﬁle object handle can be directly used to perform
	 * operations on the ﬁle/directory, regardless of the underlying protocol.
	 * Input is expected to be readable. In case of no (default) input deﬁned,
	 * it throws an exception (compare to resolveDefaultInputBlocking()).
	 * 
	 * @return
	 */
	public static FileObject resolveDefaultInput() {
		return null;
	}

	/**
	 * Analogous method for accessing default output. Output is expected to be
	 * writable from any node. Writes synchronization is a developer’s responsi-
	 * bility.
	 * 
	 * @return
	 */
	public static FileObject resolveDefaultOutput() {
		return null;
	}

	/**
	 * Similar to resolveDefaultInput(), except that in case of no default in-
	 * put deﬁned, method would block until such an input is deﬁned or timeout
	 * expires.
	 * 
	 * @return
	 */
	public static FileObject resolveScratchForAO() {
		return null;
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
		return null;
	}

	/**
	 * Analogous method for outputs.
	 * 
	 * @return
	 */
	public static Set<String> getAllKnownOutputNames() {
		return null;
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
	 */
	public static Map<String, FileObject> resolveAllKnownInputs() {
		return null;
	}

	/**
	 * Analogous method for outputs.
	 * 
	 * @return
	 */
	public static Map<String, FileObject> resolveAllKnownOutputs() {
		return null;
	}

	/**
	 * Similar to resolveInput(name), except that in case of no input deﬁned
	 * with that name, method would block until such an input is deﬁned or
	 * timeout expires.
	 * 
	 * @param timeoutMillis
	 * @return
	 */
	public static FileObject resolveDefaultInputBlocking(long timeoutMillis) {
		return null;
	}

	/**
	 * Analogous blocking method for accessing output.
	 * 
	 * @param timeoutMillis
	 * @return
	 */
	public static FileObject resolveDefaultOutputBlocking(long timeoutMillis) {
		return null;
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
	 */
	public static FileObject resolveFile(String uri) {
		return null;
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
		return null;
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
	 */
	public static FileObject resolveInput(String name) {
		return null;
	}

	/**
	 * Analogous method for output.
	 * 
	 * @param name
	 * @return
	 */
	public static FileObject resolveOutput(String name) {
		return null;
	}

	/**
	 * Similar to resolveInput(name), except that in case of no input deﬁned
	 * with that name, method would block until such an input is deﬁned or
	 * timeout expires.
	 * 
	 * @param name
	 * @param timeoutMillis
	 * @return
	 */
	public static FileObject resolveInputBlocking(String name, long timeoutMillis) {
		return null;
	}

	/**
	 * Analogous blocking method for accessing output.
	 * 
	 * @param name
	 * @param timeoutMillis
	 * @return
	 */
	public static FileObject resolveOutputBlocking(String name, long timeoutMillis) {
		return null;
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
		return null;
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
		return null;
	}

	/*
	 * public static FileObject resolveScratchForAO(String node: ) {
	 * 
	 * }
	 */

}
