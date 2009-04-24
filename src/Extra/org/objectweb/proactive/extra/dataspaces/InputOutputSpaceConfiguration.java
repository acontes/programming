/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;


/**
 * Stores information needed to configure an instance of an input or output data space. It
 * introduces additional information required for input/output - name of a space to be configured.
 * 
 * @see SpaceConfiguration
 */
public class InputOutputSpaceConfiguration extends SpaceConfiguration {
    /**
     * This factory method is shorthand for
     * {@link #createConfiguration(String, String, String, String, SpaceType)} with input space as a
     * type.
     * 
     * @param url
     *            Access URL to this space, used for accessing data from remote nodes. URL defines
     *            which protocol is used to access the data from remote node, and some additional
     *            information for protocol like path, sometimes user name and password. May be
     *            <code>null</code> when remote access is not specified yet.
     * @param path
     *            Local path to access input data. This path is local to host with hostname
     *            specified in <code>hostname</code> argument. May be <code>null</code> if there is
     *            no local access.
     * @param hostname
     *            Name of host where data are stored. It is always used in conjunction with a path
     *            attribute. Input data can be accessed locally on host with that name. May be
     *            <code>null</code> only if path is <code>null</code>.
     * @param name
     *            Name of input data space to be created, unique per target application. Note that
     *            {@value DataSpacesURI#DEFAULT_IN_OUT_NAME} value is reserved for default input
     *            space. Can not be <code>null</code> .
     * @throws ConfigurationException
     *             when provided arguments doesn't form correct configuration (no access, no
     *             hostname for path)
     */
    public static InputOutputSpaceConfiguration createInputSpaceConfiguration(String url, String path,
            String hostname, String name) throws ConfigurationException {

        return new InputOutputSpaceConfiguration(url, path, hostname, SpaceType.INPUT, name);
    }

    /**
     * This factory method is shorthand for
     * {@link #createConfiguration(String, String, String, String, SpaceType)} with output space as
     * a type.
     * 
     * @param url
     *            Access URL to this space, used for accessing data from remote nodes. URL defines
     *            which protocol is used to access the data from remote node, and some additional
     *            information for protocol like path, sometimes user name and password. May be
     *            <code>null</code> when remote access is not specified yet.
     * @param path
     *            Local path to access output data. This path is local to host with hostname
     *            specified in <code>hostname</code> argument. May be <code>null</code> if there is
     *            no local access.
     * @param hostname
     *            Name of host where data are stored. It is always used in conjunction with a path
     *            attribute. Output data can be accessed locally on host with that name. May be
     *            <code>null</code> only if path is <code>null</code>.
     * @param name
     *            Name of output data space to be created, unique per target application. Note that
     *            {@value DataSpacesURI#DEFAULT_IN_OUT_NAME} value is used for default input
     *            (output) space. Can not be <code>null</code>.
     * @throws ConfigurationException
     *             when provided arguments doesn't form correct configuration (no access, no
     *             hostname for path)
     */
    public static InputOutputSpaceConfiguration createOutputSpaceConfiguration(String url, String path,
            String hostname, String name) throws ConfigurationException {

        return new InputOutputSpaceConfiguration(url, path, hostname, SpaceType.OUTPUT, name);
    }

    /**
     * Creates input or output data space configuration. This configuration may be incomplete (see
     * {@link #isComplete()}), but at least one access method has to be specified - local or remote.
     * 
     * @param url
     *            Access URL to this space, used for accessing data from remote nodes. URL defines
     *            which protocol is used to access the data from remote node, and some additional
     *            information for protocol like path, sometimes user name and password. May be
     *            <code>null</code> when remote access is not specified yet.
     * @param path
     *            Local path to access input (output) data. This path is local to host with hostname
     *            specified in <code>hostname</code> argument. May be <code>null</code> if there is
     *            no local access.
     * @param hostname
     *            Name of host where data are stored. It is always used in conjunction with a path
     *            attribute. Input (output) data can be accessed locally on host with that name. May
     *            be <code>null</code> only if path is <code>null</code>.
     * @param spaceType
     *            Input or output data space type.
     * @param name
     *            Name of input (output) data space to be created, unique per target application.
     *            Note that {@value DataSpacesURI#DEFAULT_IN_OUT_NAME} value is used for default
     *            output space. Can not be <code>null</code>.
     * @throws ConfigurationException
     *             when provided arguments doesn't form correct configuration (no access, no
     *             hostname for path)
     */
    public static InputOutputSpaceConfiguration createConfiguration(String url, String path, String hostname,
            String name, SpaceType type) throws ConfigurationException {
        return new InputOutputSpaceConfiguration(url, path, hostname, type, name);
    }

    private final String name;

    // TODO: now, somebody may provide us input-output space configuration
    // with no URL (meaning - start provider), specify valid local path and
    // wrong hostname; may be we should have some factories for that
    private InputOutputSpaceConfiguration(final String url, final String path, final String hostname,
            final SpaceType spaceType, final String name) throws ConfigurationException {
        super(url, path, hostname, spaceType);
        this.name = name;

        if (spaceType != SpaceType.INPUT && spaceType != SpaceType.OUTPUT)
            throw new IllegalArgumentException("Invalid space type for InputOutputSpaceConfiguration");

        if (name == null)
            throw new ConfigurationException("Name cannot be null");
    }

    /**
     * @return name of a space to be configured
     */
    public String getName() {
        return name;
    }
}