/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.webservices.soap.ProActiveProvider;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;


// TODO mention that returned FileObject need to be closed explicitly
/**
 * The ProActive Data Spaces API.
 * <p>
 * Note that all <code>resolve*</code> method call might block for a while if there is a need to
 * mount a data space.
 */
public class PADataSpaces {

    private PADataSpaces() {
    }

    /**
     * Returns file handle for the <i>default input data space</i>, as defined in application
     * descriptor or dynamically set through API during application execution. Returned file handle
     * can be directly used to perform operations on the file/directory, regardless of the
     * underlying protocol.
     * <p>
     * Input is expected to be readable.
     * </p>
     * 
     * @see {@link #resolveDefaultInputBlocking(long)}
     * @return File handle for the default input data space
     * @throws SpaceNotFoundException
     *             when no default input data space defined
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveDefaultInput() throws FileSystemException, SpaceNotFoundException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutput(SpaceType.INPUT);
    }

    /**
     * Returns file handle for the <i>default output data space</i>, as defined in application
     * descriptor or dynamically set through API during application execution. Returned file handle
     * can be directly used to perform operations on the file/directory, regardless of the
     * underlying protocol.
     * <p>
     * Output is expected to be writable from any node. Writes synchronization is a developer’s
     * responsibility.
     * </p>
     * 
     * @see {@link #resolveDefaultOutputBlocking(long)}
     * @return File handle for the default output data space
     * @throws SpaceNotFoundException
     *             when no default input data space defined
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws SpaceNotFoundException
     *             when no default output data space defined
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveDefaultOutput() throws FileSystemException, SpaceNotFoundException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutput(SpaceType.OUTPUT);
    }

    /**
     * Returns file handle for calling Active Object's <i>scratch data space</i>. If such a scratch
     * has not existed before, it is created in its node scratch data space (as configured in
     * deployment descriptor).
     * <p>
     * Returned scratch is expected to be writable by this Active Object and readable by others.
     * </p>
     * 
     * @return
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when scratch data space is not configured or caller's node is not configured for
     *             Data Spaces application
     */
    public static FileObject resolveScratchForAO() throws FileSystemException, NotConfiguredException {

        return getMyDataSpacesImpl().resolveScratchForAO();
    }

    /**
     * Returns names of every registered <i>input data space</i> known at time this method is
     * executed.
     * <p>
     * If inputs are available at static application descriptor, every name is guaranteed to be
     * returned. Any other input's name that was successfully defined by
     * {@link #addInput(String, String, String)} method call (that locally precedes this call or
     * precedes it in a global real-time), is also returned.
     * </p>
     * <p>
     * This method doesn’t cause inputs to be mounted, i.e. it doesn’t cause local VFS view to be
     * refreshed.
     * </p>
     * 
     * @return all names of inputs defined before the moment of this call
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static Set<String> getAllKnownInputNames() throws NotConfiguredException {
        return getMyDataSpacesImpl().getAllKnownInputOutputNames(SpaceType.INPUT);
    }

    /**
     * Returns names of every registered <i>output data space</i> known at time this method is
     * executed.
     * <p>
     * If outputs are available at static application descriptor, every name is guaranteed to be
     * returned. Any other output's name that was successfully defined by
     * {@link #addOutput(String, String, String)} method call (that locally precedes this call or
     * precedes it in a global real-time), is also returned.
     * </p>
     * <p>
     * This method doesn’t cause outputs to be mounted, i.e. it doesn’t cause local VFS view to be
     * refreshed.
     * </p>
     * 
     * @return all names of outputs defined before the moment of this call
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static Set<String> getAllKnownOutputNames() throws NotConfiguredException {
        return getMyDataSpacesImpl().getAllKnownInputOutputNames(SpaceType.OUTPUT);
    }

    /**
     * Returns mapping of input names to file handles for every <i>input data space</i> known at
     * this time. Every input is mounted in result of this call, i.e. it does refresh local VFS
     * view.
     * <p>
     * Returned input handles are expected to be readable.
     * </p>
     * 
     * @see {@link #getAllKnownInputNames()}
     * @return
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static Map<String, FileObject> resolveAllKnownInputs() throws FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveAllKnownInputsOutputs(SpaceType.INPUT);
    }

    /**
     * Returns mapping of output names to file handles for every <i>output data space</i> known at
     * this time. Every output is mounted in result of this call, i.e. it does refresh local VFS
     * view.
     * <p>
     * Returned output handles are expected to be writable. Writes synchronization is a developer’s
     * responsibility.
     * </p>
     * 
     * @see {@link #getAllKnownInputNames()}
     * @return
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static Map<String, FileObject> resolveAllKnownOutputs() throws FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveAllKnownInputsOutputs(SpaceType.OUTPUT);
    }

    /**
     * <p>
     * Blocking version of {@link #resolveDefaultInput()} for a case when no default input is
     * defined. Method blocks until default input is defined or timeout expires.
     * </p>
     * 
     * @param timeoutMillis
     * @return File handle for the default input data space
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveDefaultInputBlocking(long timeoutMillis) throws IllegalArgumentException,
            FileSystemException, ProActiveTimeoutException, NotConfiguredException {

        return getMyDataSpacesImpl().resolveDefaultInputOutputBlocking(timeoutMillis, SpaceType.INPUT);
    }

    /**
     * Blocking version of {@link #resolveDefaultOutput()} for a case when no default output is
     * defined. Method blocks until default input is defined or timeout expires.
     * 
     * @param timeoutMillis
     * @return File handle for the default output data space
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveDefaultOutputBlocking(long timeoutMillis)
            throws IllegalArgumentException, FileSystemException, ProActiveTimeoutException,
            NotConfiguredException {

        return getMyDataSpacesImpl().resolveDefaultInputOutputBlocking(timeoutMillis, SpaceType.OUTPUT);
    }

    /**
     * Returns file handle for any valid URI within an existing data space in the application
     * (another AO’s scratch, input or output).
     * <p>
     * Returned file handle should be readable, but not necessarily writable. This kind of
     * capabilities checking is caller’s responsibility or it can be implied from a objects contract
     * (e.g. data space type of a URI being passed is known).
     * </p>
     * 
     * @param uri
     *            valid URI within an existing data space
     * @return handle for specified file
     * @throws MalformedURIException
     *             passed URI is invalid or not complete
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws SpaceNotFoundException
     *             specified URI points to invalid data space
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveFile(String uri) throws FileSystemException, MalformedURIException,
            SpaceNotFoundException, NotConfiguredException {

        return getMyDataSpacesImpl().resolveFile(uri);
    }

    /**
     * Returns URI for a given {@link FileObject}. URI remains valid in the whole application, hence
     * it can be passed to another AO and resolved there.
     * 
     * @see {@link #resolveFile(String)}
     * @param fileObject
     * @return valid URI for specified file object
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static String getURI(FileObject fileObject) throws NotConfiguredException {
        return getMyDataSpacesImpl().getURI(fileObject);
    }

    /**
     * Returns file handle for an <i>input data space</i> with specific name. In the case of no
     * input defined with that name, an exception is thrown.
     * <p>
     * Returned input is expected to be readable.
     * <p>
     * 
     * @param name
     *            of an input data space to resolve
     * @return file handle of resolved data space
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws SpaceNotFoundException
     *             when there is no input data space with specified name
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveInput(String name) throws FileSystemException, SpaceNotFoundException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutput(name, SpaceType.INPUT);
    }

    /**
     * Returns file handle for an <i>output data space</i> with specific name. In the case of no
     * output defined with that name, an exception is thrown.
     * <p>
     * Returned output is expected to be writable. Writes synchronization is a developer’s
     * responsibility.
     * </p>
     * 
     * @param name
     *            of an output data space to resolve
     * @return file handle of resolved data space
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws SpaceNotFoundException
     *             when there is no output data space with specified name
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveOutput(String name) throws FileSystemException, SpaceNotFoundException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutput(name, SpaceType.OUTPUT);
    }

    /**
     * Blocking version of {@link #resolveInput(String)} for a case when no input is defined with
     * specified name. Method blocks until input with specified name is defined or timeout expires.
     * 
     * @param name
     *            of an input data space to resolve
     * @param timeoutMillis
     * @return File handle for the input data space with specified name
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveInputBlocking(String name, long timeoutMillis)
            throws FileSystemException, IllegalArgumentException, ProActiveTimeoutException,
            NotConfiguredException {

        return getMyDataSpacesImpl().resolveInputOutputBlocking(name, timeoutMillis, SpaceType.INPUT);
    }

    /**
     * Blocking version of {@link #resolveOutput(String)} for a case when no output is defined with
     * specified name. Method blocks until output with specified name is defined or timeout expires.
     * 
     * @param name
     *            of an output data space to resolve
     * @param timeoutMillis
     * @return File handle for the output data space with specified name
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveOutputBlocking(String name, long timeoutMillis)
            throws FileSystemException, IllegalArgumentException, ProActiveTimeoutException,
            NotConfiguredException {

        return getMyDataSpacesImpl().resolveInputOutputBlocking(name, timeoutMillis, SpaceType.OUTPUT);
    }

    /**
     * Adds <i>input data space</i> definition with a provided name during the application
     * execution. Input <b>name</b> must be unique for the application. Input with empty,
     * <code>null</code> or <code>"default"</code> name parameter becomes the application's default
     * input data space.
     * <p>
     * Input must have a local path and/or global access URL defied:
     * <ul>
     * <li>
     * In the case of path-based local-only access defined, data space is exposed for remote access
     * through VFS {@link ProActiveProvider}. <b>Path</b> given for a local access is resolved in
     * the context of a caller’s node's local file system.</li>
     * <li>
     * In case of remote access defined through <b>URL</b>, local access definition is optional, it
     * may be used internally only due to performance reasons.</li>
     * </ul>
     * Returned URI of a created input data space can be safely passed to another Active Object.
     * Given input name (which might be constant in code) can also be safely used by any other
     * Active Object after this method returns.
     * 
     * @param name
     * @param path
     * @param url
     * @return URI of a created input data space
     * @throws SpaceAlreadyRegisteredException
     *             if any input with specified name has been already registered
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @throws ConfigurationException
     *             when specified configuration is wrong or not sufficient
     */
    public static String addInput(String name, String path, String url)
            throws SpaceAlreadyRegisteredException, NotConfiguredException, ConfigurationException {

        return getMyDataSpacesImpl().addInputOutput(name, path, url, SpaceType.INPUT);
    }

    /**
     * Adds <i>output data space</i> definition with a provided name during the application
     * execution. Output <b>name</b> must be unique for the application. Output with empty,
     * <code>null</code> or <code>"default"</code> name parameter becomes the application's default
     * output data space.
     * <p>
     * Output must have a local path and/or global access URL defied:
     * <ul>
     * <li>
     * In the case of path-based local-only access defined, data space is exposed for remote access
     * through VFS {@link ProActiveProvider}. <b>Path</b> given for a local access is resolved in
     * the context of a caller’s node's local file system.</li>
     * <li>
     * In case of remote access defined through <b>URL</b>, local access definition is optional, it
     * may be used internally only due to performance reasons.</li>
     * </ul>
     * Returned URI of a created output data space can be safely passed to another Active Object.
     * Given output name (which might be constant in code) can also be safely used by any other
     * Active Object after this method returns.
     * 
     * @param name
     * @param path
     * @param url
     * @return URI of a created output data space
     * @throws SpaceAlreadyRegisteredException
     *             if any input with specified name has been already registered
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @throws ConfigurationException
     *             when specified configuration is wrong or not sufficient
     */
    public static String addOutput(String name, String path, String url)
            throws SpaceAlreadyRegisteredException, NotConfiguredException, ConfigurationException {

        return getMyDataSpacesImpl().addInputOutput(name, path, url, SpaceType.OUTPUT);
    }

    private static DataSpacesImpl getMyDataSpacesImpl() throws NotConfiguredException {
        final Node n = Utils.getCurrentNode();
        return DataSpacesNodes.getDataSpacesImpl(n);
    }

    /*
     * public static FileObject resolveScratchForAO(String node: ) {
     * 
     * }
     */
}
