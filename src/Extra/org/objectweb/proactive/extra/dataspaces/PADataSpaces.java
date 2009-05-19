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
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;


// TODO we mention "expected to be readable/writable" - maybe we can verify it?
// TODO add docs about data sharing - do not pass FileObject, only URI!
// or add general docs about various ways of accessing and sharing data through this API
/**
 * The ProActive Data Spaces API.
 * <p>
 * The API provide ways to access existing data spaces and files within these data spaces, it also
 * gives ability to define new data spaces. All operations performed through these API concern data
 * spaces owned by caller's application, registered in this application Naming Service.
 * <p>
 * Note that all <code>resolve*</code> method call might block for a while if there is a need to
 * mount a data space or request for access information. All these methods return FileObject
 * instance, that should be closed by caller of this method.
 * 
 * @see FileObject
 */
public class PADataSpaces {

    /**
     * Default input and output spaces name.
     */
    public static final String DEFAULT_IN_OUT_NAME = "default";

    private PADataSpaces() {
    }

    /**
     * Returns file handle to the <i>default input data space</i>. This method call is equal to
     * {@link #resolveDefaultInput(String)} with null path argument.
     * 
     * @return file handle to the default input data space of caller's application
     * @throws SpaceNotFoundException
     *             when there is no default input data space defined
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveDefaultInputBlocking(long)
     * @see #resolveDefaultInput(String)
     */
    public static FileObject resolveDefaultInput() throws SpaceNotFoundException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutput(SpaceType.INPUT, null);
    }

    /**
     * Returns file handle to file specified by path in the <i>default input data space</i>, as
     * defined in application descriptor or dynamically set through API during application
     * execution.
     * <p>
     * Returned file handle can be directly used to perform operations on the file/directory,
     * regardless of the underlying protocol. If specified file doesn't exist, one should call
     * {@link FileObject#createFile()} or {@link FileObject#createFolder()} method. Closing returned
     * FileObject is a caller's responsibility.
     * <p>
     * Input data space content is expected to be readable from any node of this application if it
     * was defined correctly. It is intended to provide any form of input to the application.
     * 
     * @param path
     *            path of a file in the default input data space; <code>null</code> denotes request
     *            for data space root
     * @return file handle to file specified by path in the default input data space of caller's
     *         application
     * @throws SpaceNotFoundException
     *             when there is no default input data space defined
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveDefaultInputBlocking(String, long)
     */
    public static FileObject resolveDefaultInput(String path) throws SpaceNotFoundException,
            FileSystemException, NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutput(SpaceType.INPUT, path);
    }

    /**
     * Returns file handle to the <i>default output data space</i>. This method call is equal to
     * {@link #resolveDefaultOutput(String)} with null path argument.
     * 
     * @return file handle to the default output data space of caller's application
     * @throws SpaceNotFoundException
     *             when there is no default output data space defined
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveDefaultOutputBlocking(long)
     * @see #resolveDefaultOutput(String)
     */
    public static FileObject resolveDefaultOutput() throws SpaceNotFoundException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutput(SpaceType.OUTPUT, null);
    }

    /**
     * Returns file handle to file specified by path in the <i>default output data space</i>, as
     * defined in application descriptor or dynamically set through API during application
     * execution.
     * <p>
     * Returned file handle can be directly used to perform operations on the file/directory,
     * regardless of the underlying protocol. If specified file doesn't exist, one should call
     * {@link FileObject#createFile()} or {@link FileObject#createFolder()} method. Closing returned
     * FileObject is a caller's responsibility.
     * <p>
     * Output data space content is expected to be writable from any node of this application if it
     * was defined correctly. It is intended to store globally any computation results. Writes
     * synchronization is a developer’s responsibility.
     * 
     * @param path
     *            path of a file in the default output data space; <code>null</code> denotes request
     *            for data space root
     * @return file handle to file specified by path in the default output data space of caller's
     *         application
     * @throws SpaceNotFoundException
     *             when there is no default output data space defined
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveDefaultOutputBlocking(String, long)
     */
    public static FileObject resolveDefaultOutput(String path) throws SpaceNotFoundException,
            FileSystemException, NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutput(SpaceType.OUTPUT, path);
    }

    /**
     * Blocking version of {@link #resolveDefaultInput()} for a case when caller want to wait until
     * default input is defined.
     * <p>
     * This method is intended to provide simple (and possibly inefficient) way to synchronize input
     * definition and usage. It repeatedly queries Data Spaces' Naming Service for default input
     * space availability, so it blocks until default input is defined or specified timeout expires.
     * 
     * @param timeoutMillis
     *            timeout for blocking wait, in milliseconds
     * @return file handle to the default input data space of caller's application
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     *             when timeout expired and space is still not available
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveDefaultInput()
     */
    public static FileObject resolveDefaultInputBlocking(long timeoutMillis) throws IllegalArgumentException,
            ProActiveTimeoutException, FileSystemException, NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutputBlocking(timeoutMillis, SpaceType.INPUT, null);
    }

    /**
     * Blocking version of {@link #resolveDefaultInput(String)} for a case when caller want to wait
     * until default input is defined.
     * <p>
     * This method is intended to provide simple (and possibly inefficient) way to synchronize input
     * definition and usage. It repeatedly queries Data Spaces' Naming Service for default input
     * space availability, so it blocks until default input is defined or specified timeout expires.
     * 
     * @param path
     *            path of a file in the default input data space; <code>null</code> denotes request
     *            for data space root
     * @param timeoutMillis
     *            timeout for blocking wait, in milliseconds
     * @return file handle to file specified by path in the default input data space of caller's
     *         application
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     *             when timeout expired and space is still not available
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveDefaultInput(String)
     */
    public static FileObject resolveDefaultInputBlocking(String path, long timeoutMillis)
            throws IllegalArgumentException, ProActiveTimeoutException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutputBlocking(timeoutMillis, SpaceType.INPUT, path);
    }

    /**
     * Blocking version of {@link #resolveDefaultOutput()} for a case when caller want to wait until
     * default output is defined.
     * <p>
     * This method is intended to provide simple (and possibly inefficient) way to synchronize
     * output definition and usage. It repeatedly queries Data Spaces' Naming Service for default
     * output space availability, so it blocks until default output is defined or specified timeout
     * expires.
     * 
     * @param timeoutMillis
     *            timeout for blocking wait, in milliseconds
     * @return file handle to the default output data space of caller's application
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     *             when timeout expired and space is still not available
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveDefaultOutputBlocking(long timeoutMillis)
            throws IllegalArgumentException, ProActiveTimeoutException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutputBlocking(timeoutMillis, SpaceType.OUTPUT, null);
    }

    /**
     * Blocking version of {@link #resolveDefaultOutput(String)} for a case when caller want to wait
     * until default output is defined.
     * <p>
     * This method is intended to provide simple (and possibly inefficient) way to synchronize
     * output definition and usage. It repeatedly queries Data Spaces' Naming Service for default
     * output space availability, so it blocks until default output is defined or specified timeout
     * expires.
     * 
     * @param path
     *            path of a file in the default output data space; <code>null</code> denotes request
     *            for data space root
     * @param timeoutMillis
     *            timeout for blocking wait, in milliseconds
     * @return file handle to file specified by path in the default output data space of caller's
     *         application
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     *             when timeout expired and space is still not available
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static FileObject resolveDefaultOutputBlocking(String path, long timeoutMillis)
            throws IllegalArgumentException, ProActiveTimeoutException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveDefaultInputOutputBlocking(timeoutMillis, SpaceType.OUTPUT, path);
    }

    /**
     * Returns file handle to the <i>input data space</i> with specific name. This method call is
     * equal to {@link #resolveInput(String, String)} with null path argument.
     * 
     * @param name
     *            name of an input data space to resolve
     * @return file handle to the input data space with provided name, for caller's application
     * @throws SpaceNotFoundException
     *             when there is no input data space defined with specified name
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveInputBlocking(long)
     * @see #resolveInput(String, String)
     */
    public static FileObject resolveInput(String name) throws SpaceNotFoundException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutput(name, SpaceType.INPUT, null);
    }

    /**
     * Returns file handle to file specified by path in the <i>input data space</i> with specific
     * name, as defined in application descriptor or dynamically set through API during application
     * execution.
     * <p>
     * Returned file handle can be directly used to perform operations on the file/directory,
     * regardless of the underlying protocol. If specified file doesn't exist, one should call
     * {@link FileObject#createFile()} or {@link FileObject#createFolder()} method. Closing returned
     * FileObject is a caller's responsibility.
     * <p>
     * Input data space content is expected to be readable from any node of this application if it
     * was defined correctly. It is intended to provide any form of input to the application.
     * 
     * @param name
     *            name of an input data space to resolve
     * @param path
     *            path of a file in the named input data space; <code>null</code> denotes request
     *            for data space root
     * @return file handle to file specified by path in the input data space with provided name, for
     *         caller's application
     * @throws SpaceNotFoundException
     *             when there is no input data space defined with specified name
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveInputBlocking(String, String, long)
     */
    public static FileObject resolveInput(String name, String path) throws SpaceNotFoundException,
            FileSystemException, NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutput(name, SpaceType.INPUT, path);
    }

    /**
     * Returns file handle to the <i>output data space</i> with specific name. This method call is
     * equal to {@link #resolveOutput(String, String)} with null path argument.
     * 
     * @param name
     *            name of an output data space to resolve
     * @return file handle to the output data space with provided name, for caller's application
     * @throws SpaceNotFoundException
     *             when there is no output data space defined with specified name
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveOutputBlocking(long)
     * @see #resolveOutput(String, String)
     */
    public static FileObject resolveOutput(String name) throws SpaceNotFoundException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutput(name, SpaceType.OUTPUT, null);
    }

    /**
     * Returns file handle to file specified by path in the <i>output data space</i> with specific
     * name, as defined in application descriptor or dynamically set through API during application
     * execution.
     * <p>
     * Returned file handle can be directly used to perform operations on the file/directory,
     * regardless of the underlying protocol. If specified file doesn't exist, one should call
     * {@link FileObject#createFile()} or {@link FileObject#createFolder()} method. Closing returned
     * FileObject is a caller's responsibility.
     * <p>
     * Output data space content is expected to be writable from any node of this application if it
     * was defined correctly. It is intended to store globally any computation results. Writes
     * synchronization is a developer’s responsibility.
     * 
     * @param name
     *            name of an output data space to resolve
     * @param path
     *            path of a file in the named output data space; <code>null</code> denotes request
     *            for data space root
     * @return file handle to file specified by path in the output data space with provided name,
     *         for caller's application
     * @throws SpaceNotFoundException
     *             when there is no output data space defined with specified name
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveOutputBlocking(String, String, long)
     */
    public static FileObject resolveOutput(String name, String path) throws SpaceNotFoundException,
            FileSystemException, NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutput(name, SpaceType.OUTPUT, path);
    }

    /**
     * Blocking version of {@link #resolveInput(String)} for a case when caller want to wait until
     * input with specific name is defined.
     * <p>
     * This method is intended to provide simple (and possibly inefficient) way to synchronize input
     * definition and usage. It repeatedly queries Data Spaces' Naming Service for input space with
     * a specific name availability, so it blocks until this input is defined or specified timeout
     * expires.
     * 
     * @param name
     *            name of an input data space to resolve
     * @param timeoutMillis
     *            timeout for blocking wait, in milliseconds
     * @return file handle to the input data space with specified name, for caller's application
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     *             when timeout expired and space is still not available
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveInput(String)
     */
    public static FileObject resolveInputBlocking(String name, long timeoutMillis)
            throws IllegalArgumentException, ProActiveTimeoutException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutputBlocking(name, timeoutMillis, SpaceType.INPUT, null);
    }

    /**
     * Blocking version of {@link #resolveInput(String, String)} for a case when caller want to wait
     * until input with specific name is defined.
     * <p>
     * This method is intended to provide simple (and possibly inefficient) way to synchronize input
     * definition and usage. It repeatedly queries Data Spaces' Naming Service for input space with
     * a specific name availability, so it blocks until this input is defined or specified timeout
     * expires.
     * 
     * @param name
     *            name of an input data space to resolve
     * @param path
     *            path of a file in the named input data space; <code>null</code> denotes request
     *            for data space root
     * @param timeoutMillis
     *            timeout for blocking wait, in milliseconds
     * @return file handle to file specified by path in the input data space with provided name, for
     *         caller's application
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     *             when timeout expired and space is still not available
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveInput(String, String)
     */
    public static FileObject resolveInputBlocking(String name, String path, long timeoutMillis)
            throws IllegalArgumentException, ProActiveTimeoutException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutputBlocking(name, timeoutMillis, SpaceType.INPUT, path);
    }

    /**
     * Blocking version of {@link #resolveOutput(String)} for a case when caller want to wait until
     * output with specific name is defined.
     * <p>
     * This method is intended to provide simple (and possibly inefficient) way to synchronize
     * output definition and usage. It repeatedly queries Data Spaces' Naming Service for output
     * space with a specific name availability, so it blocks until this output is defined or
     * specified timeout expires.
     * 
     * @param name
     *            name of an output data space to resolve
     * @param timeoutMillis
     *            timeout for blocking wait, in milliseconds
     * @return file handle to the output data space with specified name, for caller's application
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     *             when timeout expired and space is still not available
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveOutput(String)
     */
    public static FileObject resolveOutputBlocking(String name, long timeoutMillis)
            throws IllegalArgumentException, ProActiveTimeoutException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutputBlocking(name, timeoutMillis, SpaceType.OUTPUT, null);
    }

    /**
     * Blocking version of {@link #resolveOutput(String, String)} for a case when caller want to
     * wait until output with specific name is defined.
     * <p>
     * This method is intended to provide simple (and possibly inefficient) way to synchronize
     * output definition and usage. It repeatedly queries Data Spaces' Naming Service for output
     * space with a specific name availability, so it blocks until this output is defined or
     * specified timeout expires.
     * 
     * @param name
     *            name of an output data space to resolve
     * @param path
     *            path of a file in the named output data space; <code>null</code> denotes request
     *            for data space root
     * @param timeoutMillis
     *            timeout for blocking wait, in milliseconds
     * @return file handle to file specified by path in the output data space with provided name,
     *         for caller's application
     * @throws IllegalArgumentException
     *             specified timeout is not positive integer
     * @throws ProActiveTimeoutException
     *             when timeout expired and space is still not available
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveOutput(String, String)
     */
    public static FileObject resolveOutputBlocking(String name, String path, long timeoutMillis)
            throws IllegalArgumentException, ProActiveTimeoutException, FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveInputOutputBlocking(name, timeoutMillis, SpaceType.OUTPUT, path);
    }

    /**
     * Returns file handle to calling Active Object's <i>scratch data space</i>. This method call is
     * equal to {@link #resolveScratchForAO(String)} with null path argument.
     * 
     * @return file handle to the scratch for calling Active Object
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when scratch data space is not configured on caller's node or this node is not
     *             configured for Data Spaces application at all
     * @see #resolveScratchForAO(String)
     */
    public static FileObject resolveScratchForAO() throws FileSystemException, NotConfiguredException {
        return getMyDataSpacesImpl().resolveScratchForAO(null);
    }

    /**
     * Returns file handle to file specified by path in calling Active Object's <i>scratch data
     * space</i>. If such a scratch has not existed before, it is created in its node scratch data
     * space (as configured for a node, usually in deployment descriptor).
     * <p>
     * Returned file handle can be directly used to perform operations on the file/directory,
     * regardless of the underlying protocol. If specified file doesn't exist, one should call
     * {@link FileObject#createFile()} or {@link FileObject#createFolder()} method. Closing returned
     * FileObject is a caller's responsibility.
     * <p>
     * Returned scratch is expected to be writable by this Active Object and readable by other
     * Active Objects of this application, if it was defined correctly. It is intended to store any
     * temporary results of computation and possibly share them with other Active Objects. These
     * results will be most probably automatically removed after application terminates.
     * 
     * @param path
     *            path of a file in the scratch for calling Active Object; <code>null</code> denotes
     *            request for data space root
     * @return file handle to file specified by path in the scratch for calling Active Object
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when scratch data space is not configured on caller's node or this node is not
     *             configured for Data Spaces application at all
     */
    public static FileObject resolveScratchForAO(String path) throws FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveScratchForAO(path);
    }

    /**
     * Returns names of every registered <i>input data spaces</i> for caller's application known at
     * time this method is called.
     * <p>
     * If inputs are available at static application descriptor, every name is guaranteed to be
     * returned. Any other input's name that was successfully defined by
     * {@link #addInput(String, String, String)} or {@link #addDefaultInput(String, String)} methods
     * call (that locally precedes this call or precedes it in a global real-time), is also
     * returned.
     * <p>
     * This method does not cause intputs to be mounted, i.e. it doesn’t cause local VFS view to be
     * refreshed.
     * 
     * @return set of all names of inputs defined for caller's application before the moment of this
     *         call; default input name is denoted as {@value #DEFAULT_IN_OUT_NAME}
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static Set<String> getAllKnownInputNames() throws NotConfiguredException {
        return getMyDataSpacesImpl().getAllKnownInputOutputNames(SpaceType.INPUT);
    }

    /**
     * Returns names of every registered <i>output data spaces</i> for caller's application known at
     * time this method is called.
     * <p>
     * If outputs are available at static application descriptor, every name is guaranteed to be
     * returned. Any other output's name that was successfully defined by
     * {@link #addOutput(String, String, String)} or {@link #addDefaultOutput(String, String)}
     * methods call (that locally precedes this call or precedes it in a global real-time), is also
     * returned.
     * <p>
     * This method does not cause outputs to be mounted, i.e. it doesn’t cause local VFS view to be
     * refreshed.
     * 
     * @return set of all names of outputs defined for caller's application before the moment of
     *         this call; default input name is denoted as {@value #DEFAULT_IN_OUT_NAME}
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static Set<String> getAllKnownOutputNames() throws NotConfiguredException {
        return getMyDataSpacesImpl().getAllKnownInputOutputNames(SpaceType.OUTPUT);
    }

    /**
     * Returns mapping of input names to file handles to every <i>input data space</i> of caller's
     * application known at this time.
     * <p>
     * If inputs are available at static application descriptor, every of such input is guaranteed
     * to be returned. Any other input's name that was successfully defined by
     * {@link #addInput(String, String, String)} or {@link #addDefaultInput(String, String)} methods
     * call (that locally precedes this call or precedes it in a global real-time), is also
     * returned.
     * <p>
     * Returned file handles can be directly used to perform operations on the file/directory,
     * regardless of the underlying protocol. Every input is mounted in result of this call, i.e. it
     * does refresh local VFS view. Closing returned FileObjects is a caller's responsibility.
     * <p>
     * Input data spaces content is expected to be readable from any node of this application if it
     * was defined correctly. It is intended to provide any form of input to the application. Names
     * of input spaces are application-level contract.
     * 
     * @return map of all names of inputs defined for caller's application before the moment of this
     *         call to file handles to these inputs; default input name is denoted as
     *         {@value #DEFAULT_IN_OUT_NAME}
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #getAllKnownInputNames()
     */
    public static Map<String, FileObject> resolveAllKnownInputs() throws FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveAllKnownInputsOutputs(SpaceType.INPUT);
    }

    /**
     * Returns mapping of output names to file handles to every <i>output data space</i> of caller's
     * application known at this time.
     * <p>
     * If outputs are available at static application descriptor, every of such output is guaranteed
     * to be returned. Any other output's name that was successfully defined by
     * {@link #addOutput(String, String, String)} or {@link #addDefaultOutput(String, String)}
     * methods call (that locally precedes this call or precedes it in a global real-time), is also
     * returned.
     * <p>
     * Returned file handles can be directly used to perform operations on the file/directory,
     * regardless of the underlying protocol. Every output is mounted in result of this call, i.e.
     * it does refresh local VFS view. Closing returned FileObjects is a caller's responsibility.
     * <p>
     * Output data spaces content is expected to be readable from any node of this application if it
     * was defined correctly. It is intended to provide any form of output to the application. Names
     * of output spaces are application-level contract.
     * 
     * @return map of all names of outputs defined for caller's application before the moment of
     *         this call to file handles to these outputs; default output name is denoted as
     *         {@value #DEFAULT_IN_OUT_NAME}
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #getAllKnownOutputNames()
     */
    public static Map<String, FileObject> resolveAllKnownOutputs() throws FileSystemException,
            NotConfiguredException {
        return getMyDataSpacesImpl().resolveAllKnownInputsOutputs(SpaceType.OUTPUT);
    }

    /**
     * Returns file handle pointed by any valid URI within an existing data space in the application
     * (another AO’s scratch, input or output).
     * <p>
     * Returned file handle can be directly used to perform operations on the file/directory,
     * regardless of the underlying protocol. Closing returned FileObject is a caller's
     * responsibility.
     * <p>
     * Returned file handle should be readable, but not necessarily writable. This kind of
     * capabilities checking is caller’s responsibility or it can be implied from a calling objects
     * contract (e.g. data space type of a URI being passed is known).
     * <p>
     * Note that URI from another application should work if passed here if both application share
     * some lifecycle period and they are configured to use the same Naming Service.
     * 
     * @param uri
     *            valid URI within an existing data space, as returned by
     *            {@link #getURI(FileObject)}
     * @return handle for specified file
     * @throws MalformedURIException
     *             passed URI is invalid or not complete
     * @throws SpaceNotFoundException
     *             specified URI points to invalid data space
     * @throws FileSystemException
     *             indicates VFS related exception
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #getURI(FileObject)
     */
    public static FileObject resolveFile(String uri) throws MalformedURIException, SpaceNotFoundException,
            FileSystemException, NotConfiguredException {
        return getMyDataSpacesImpl().resolveFile(uri);
    }

    /**
     * Returns URI for a given {@link FileObject}. URI remains valid in the whole application, hence
     * it can be passed to another AO and resolved there.
     * 
     * @param fileObject
     * @return valid URI for specified file object
     * @see {@link #resolveFile(String)}
     */
    public static String getURI(FileObject fileObject) {
        return DataSpacesImpl.getURI(fileObject);
    }

    /**
     * Adds <i>default input data space</i> definition, during the application execution.
     * <p>
     * Input must have a global access URL defined, that is used to access this data space from
     * remote nodes. Local (to caller's node) absolute access path may be used, to achieve better
     * performance by accessing data locally on this node. Provided remote and local access ways
     * should guarantee that input is readable from any node, Active Object.
     * <p>
     * Returned URI of a created input data space can be safely passed to another Active Object of
     * this application. It can be also safely access by {@link #resolveDefaultInput()} or
     * {@link #resolveDefaultInputBlocking(long)} by any other Active Object after this method
     * returns.
     * 
     * @param url
     *            Access URL to this space, used for accessing data from remote nodes. URL defines
     *            which protocol is used to access the data from remote node, and some additional
     *            information for protocol like path, sometimes user name and password.
     * @param path
     *            Local path to access input data. This path is local to caller's host. May be
     *            <code>null</code> if there is no local access.
     * @return URI of a created input data space
     * @throws ConfigurationException
     *             when specified configuration is wrong or not sufficient
     * @throws SpaceAlreadyRegisteredException
     *             if default input has been already registered for this application
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveDefaultInput()
     * @see #resolveDefaultInputBlocking(long)
     */
    public static String addDefaultInput(String url, String path) throws ConfigurationException,
            SpaceAlreadyRegisteredException, NotConfiguredException {
        return getMyDataSpacesImpl().addDefaultInputOutput(url, path, SpaceType.INPUT);
    }

    /**
     * Adds <i>default output data space</i> definition, during the application execution.
     * <p>
     * Output must have a global access URL defined, that is used to access this data space from
     * remote nodes. Local (to caller's node) absolute access path may be used, to achieve better
     * performance by accessing data locally on this node. Provided remote and local access ways
     * should guarantee that output is readable from any node, Active Object.
     * <p>
     * Returned URI of a created output data space can be safely passed to another Active Object of
     * this application. It can be also safely access by {@link #resolveDefaultOutput()} or
     * {@link #resolveDefaultOutputBlocking(long)} by any other Active Object after this method
     * returns.
     * 
     * @param url
     *            Access URL to this space, used for accessing data from remote nodes. URL defines
     *            which protocol is used to access the data from remote node, and some additional
     *            information for protocol like path, sometimes user name and password.
     * @param path
     *            Local path to access output data. This path is local to caller's host. May be
     *            <code>null</code> if there is no local access.
     * @return URI of a created output data space
     * @throws ConfigurationException
     *             when specified configuration is wrong or not sufficient
     * @throws SpaceAlreadyRegisteredException
     *             if default output has been already registered for this application
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     * @see #resolveDefaultOutput()
     * @see #resolveDefaultOutputBlocking(long)
     */
    public static String addDefaultOutput(String url, String path) throws ConfigurationException,
            SpaceAlreadyRegisteredException, NotConfiguredException {
        return getMyDataSpacesImpl().addDefaultInputOutput(url, path, SpaceType.OUTPUT);
    }

    /**
     * Adds <i>input data space</i> definition with a provided name, during the application
     * execution.
     * <p>
     * Input <b>name</b> must be unique for the application, while name
     * {@value #DEFAULT_IN_OUT_NAME} is reserved for default input space.
     * <p>
     * Input must have a global access URL defined, that is used to access this data space from
     * remote nodes. Local (to caller's node) absolute access path may be used, to achieve better
     * performance by accessing data locally on this node. Provided remote and local access ways
     * should guarantee that input is readable from any node, Active Object.
     * <p>
     * Returned URI of a created input data space can be safely passed to another Active Object of
     * this application. Given input name (which might be constant in code) can also be safely used
     * by any other Active Object after this method returns.
     * 
     * @param name
     *            name of defined input, contract of application; can not be empty or contain
     *            slashes '/'; default input name is reserved, denoted as
     *            {@value #DEFAULT_IN_OUT_NAME}
     * @param url
     *            Access URL to this space, used for accessing data from remote nodes. URL defines
     *            which protocol is used to access the data from remote node, and some additional
     *            information for protocol like path, sometimes user name and password.
     * @param path
     *            Local path to access input data. This path is local to caller's host. May be
     *            <code>null</code> if there is no local access.
     * @return URI of a created input data space
     * @throws ConfigurationException
     *             when specified configuration is wrong or not sufficient
     * @throws SpaceAlreadyRegisteredException
     *             if any input with specified name has been already registered for this application
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static String addInput(String name, String url, String path) throws ConfigurationException,
            SpaceAlreadyRegisteredException, NotConfiguredException {
        return getMyDataSpacesImpl().addInputOutput(name, url, path, SpaceType.INPUT);
    }

    /**
     * Adds <i>output data space</i> definition with a provided name, during the application
     * execution.
     * <p>
     * Output <b>name</b> must be unique for the application, while name
     * {@value #DEFAULT_IN_OUT_NAME} is reserved for default output space.
     * <p>
     * Output must have a global access URL defined, that is used to access this data space from
     * remote nodes. Local (to caller's node) absolute access path may be used, to achieve better
     * performance by accessing data locally on this node. Provided remote and local access ways
     * should guarantee that output is writable from any node, Active Object.
     * <p>
     * Returned URI of a created output data space can be safely passed to another Active Object of
     * this application. Given output name (which might be constant in code) can also be safely used
     * by any other Active Object after this method returns.
     * 
     * @param name
     *            name of defined output, contract of application; can not be empty or contain
     *            slashes '/'; default output name is reserved, denoted as
     *            {@value #DEFAULT_IN_OUT_NAME}
     * @param url
     *            Access URL to this space, used for accessing data from remote nodes. URL defines
     *            which protocol is used to access the data from remote node, and some additional
     *            information for protocol like path, sometimes user name and password.
     * @param path
     *            Local path to access output data. This path is local to caller's host. May be
     *            <code>null</code> if there is no local access.
     * @return URI of a created output data space
     * @throws ConfigurationException
     *             when specified configuration is wrong or not sufficient
     * @throws SpaceAlreadyRegisteredException
     *             if any output with specified name has been already registered for this
     *             application
     * @throws NotConfiguredException
     *             when caller's node is not configured for Data Spaces application
     */
    public static String addOutput(String name, String url, String path)
            throws SpaceAlreadyRegisteredException, NotConfiguredException, ConfigurationException {
        return getMyDataSpacesImpl().addInputOutput(name, url, path, SpaceType.OUTPUT);
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
