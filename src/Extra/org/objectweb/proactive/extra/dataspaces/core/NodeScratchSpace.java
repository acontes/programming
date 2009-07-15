package org.objectweb.proactive.extra.dataspaces.core;

import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;


/**
 * Manages and configures base scratch data spaces directories of a Node, then acts as a producer
 * and maintainer of {@link ApplicationScratchSpace} objects that represent concrete scratch data
 * space for particular application.
 * <p>
 * Lifecycle of objects implementing this interface is like following:
 * <ul>
 * <li>instance is associated with a {@link Node} and {@link BaseScratchSpaceConfiguration}, which
 * are set through {@link #init(Node, BaseScratchSpaceConfiguration)} method. It performs basic
 * preliminary initialization that is independent from concrete application. It need to be called
 * before any further use of this instance.</li>
 * <li>for each application executed at previously specified node, {@link #initForApplication(long)
 * )} is called to acquire instance of {@link ApplicationScratchSpace}. It should be called only
 * once per each application.</li>
 * <li>when no more scratch space is going to be used on specified node, instance is closed by
 * {@link #close()} call, which cleans up base scratch data spaces directories.
 * </ul>
 * <p>
 * Instances of this class are thread-safe.
 * 
 * @see ApplicationScratchSpace
 */
public interface NodeScratchSpace {
    /**
     * Initializes instance (and all related configuration objects) on a specified node and performs
     * file system initialization and accessing tests, basing on provided configuration.
     * <p>
     * Provided configuration should have a remote access URL already defined. Local access (if is
     * defined) will be used for accessing directories of scratch data space for this node.
     * <p>
     * Any existing files in directory specified by configuration will be silently deleted.
     * <p>
     * This method can be called only once for each instance. Once called,
     * {@link ApplicationScratchSpace} instances can be returned by
     * {@link #initForApplication(long))}. Once initialized, this instance must be closed by
     * {@link #close()} method. If initialization fails, there is no need to close it explicitly.
     * 
     * @param node
     *            node to install scratch space for
     * @param conf
     *            base scratch space configuration with URL defined
     * @throws IllegalStateException
     *             when instance has been already configured
     * @throws FileSystemException
     *             when problem occurred during accessing remote or local file system
     * @throws ConfigurationException
     *             when checking FS capabilities fails
     */
    public void init(Node node, BaseScratchSpaceConfiguration baseScratchConfiguration)
            throws FileSystemException, ConfigurationException, IllegalStateException;

    /**
     * Initializes scratch data space for an application that is running on a Node for which
     * NodeScratchSpace has been configured and initialized by
     * {@link #init(Node, BaseScratchSpaceConfiguration)}.
     * <p>
     * Local access will be used (if is defined) for accessing scratch data space. Any potentially
     * existing files on this scratch Data Space directory will be silently deleted in result of
     * this call. Subsequent calls for the same application will result in undefined behavior.
     * 
     * @param appId
     *            id of application running on node
     * @return instance giving access to manage and use scratch data space, that need to be closed
     *         explicitly
     * @throws FileSystemException
     *             when problem occurred during accessing remote or local file system
     * @throws IllegalStateException
     *             when this instance is not initialized
     */
    public ApplicationScratchSpace initForApplication(long appId) throws FileSystemException,
            IllegalStateException;

    /**
     * Close any opened resources and cleans all node-related scratch space files. If no other
     * scratch data space remains within this runtime, runtime-related files are also removed.
     * <p>
     * Before calling this method, returned {@link ApplicationScratchSpace} instance should be
     * closed.
     * <p>
     * Subsequent calls may result in an undefined behavior.
     * 
     * @throws IllegalStateException
     *             when this instance is not initialized
     */
    public abstract void close() throws IllegalStateException;

}