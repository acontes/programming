/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

//TODO javadocs
public class InputOutputSpaceConfiguration extends SpaceConfiguration {
	/**
	 * This factory method is shorthand for
	 * {@link #createConfiguration(String, String, String, String, SpaceType)}
	 * with input space as a type.
	 * 
	 * @param url
	 *            Access URL to this input (output), used for accessing from
	 *            remote nodes. URL defines which protocol is used to access the
	 *            data from remote node, and some additional information for
	 *            protocol like path, sometimes user name and password.
	 * @param path
	 *            Local path to input (output) data. This path is local to host
	 *            with host name specified in <code>hostname</code> attribute.
	 * @param hostname
	 *            name of host where data are stored. It is always used in
	 *            conjunction with a path attribute. Input (output) data can be
	 *            accessed locally on host with that name. This attribute may
	 *            consist of special variable <code>#{deployer}</code> meaning
	 *            that the path is local to the deployer.
	 * @param spaceType
	 *            input or output data space type
	 * @param name
	 *            Unique name of input (output) data space. Default value is
	 *            just name "default", which implies being default input
	 *            (output). e.g. id="instances"
	 * @throws ConfigurationException
	 *             when one of above's contract condition fails
	 */
	public static InputOutputSpaceConfiguration createInputSpaceConfiguration(String url, String path,
			String hostname, String name) throws ConfigurationException {

		return new InputOutputSpaceConfiguration(url, path, hostname, SpaceType.INPUT, name);
	}

	/**
	 * This factory method is shorthand for
	 * {@link #createConfiguration(String, String, String, String, SpaceType)}
	 * with input space as a type.
	 * 
	 * @param url
	 *            Access URL to this input (output), used for accessing from
	 *            remote nodes. URL defines which protocol is used to access the
	 *            data from remote node, and some additional information for
	 *            protocol like path, sometimes user name and password.
	 * @param path
	 *            Local path to input (output) data. This path is local to host
	 *            with host name specified in <code>hostname</code> attribute.
	 * @param hostname
	 *            name of host where data are stored. It is always used in
	 *            conjunction with a path attribute. Input (output) data can be
	 *            accessed locally on host with that name. This attribute may
	 *            consist of special variable <code>#{deployer}</code> meaning
	 *            that the path is local to the deployer.
	 * @param spaceType
	 *            input or output data space type
	 * @param name
	 *            Unique name of input (output) data space. Default value is
	 *            just name "default", which implies being default input
	 *            (output). e.g. id="instances"
	 * @throws ConfigurationException
	 *             when one of above's contract condition fails
	 */
	public static InputOutputSpaceConfiguration createOutputSpaceConfiguration(String url, String path,
			String hostname, String name) throws ConfigurationException {

		return new InputOutputSpaceConfiguration(url, path, hostname, SpaceType.OUTPUT, name);
	}

	/**
	 * When local path and hostname are not specified and URL is specified,
	 * protocol from URL is used to access data locally. If remote access (URL)
	 * is not specified and only local path and hostname are specified, default
	 * ProActive provider is started, hence remote access is always possible. At
	 * least one access (remote or local) must be defined.
	 * 
	 * @param url
	 *            Access URL to this input (output), used for accessing from
	 *            remote nodes. URL defines which protocol is used to access the
	 *            data from remote node, and some additional information for
	 *            protocol like path, sometimes user name and password.
	 * @param path
	 *            Local path to input (output) data. This path is local to host
	 *            with host name specified in <code>hostname</code> attribute.
	 * @param hostname
	 *            name of host where data are stored. It is always used in
	 *            conjunction with a path attribute. Input (output) data can be
	 *            accessed locally on host with that name. This attribute may
	 *            consist of special variable <code>#{deployer}</code> meaning
	 *            that the path is local to the deployer.
	 * @param spaceType
	 *            input or output data space type
	 * @param name
	 *            Unique name of input (output) data space. Default value is
	 *            just name "default", which implies being default input
	 *            (output). e.g. id="instances"
	 * @throws ConfigurationException
	 *             when one of above's contract condition fails
	 */
	public static InputOutputSpaceConfiguration createConfiguration(String url, String path, String hostname,
			String name, SpaceType type) throws ConfigurationException {
		return new InputOutputSpaceConfiguration(url, path, hostname, type, name);
	}

	private final String name;

	// FIXME IMO we shouldn't handle #{deployer} at this level; #{deployer}
	// is very context-specific, it should be rather handled in
	// deployment code on deployer side perhaps
	// TODO: now, somebody may provide us input-output space configuration
	// with no URL (meaning - start provider), specify valid local path and
	// wrong hostname; may be we should have some factories for that
	private InputOutputSpaceConfiguration(String url, String path, String hostname, SpaceType spaceType,
			String name) throws ConfigurationException {
		super(url, path, hostname, spaceType);
		this.name = name;

		if (spaceType != SpaceType.INPUT && spaceType != SpaceType.OUTPUT)
			throw new IllegalArgumentException("Invalid space type for InputOutputSpaceConfiguration");

		if (name == null)
			throw new ConfigurationException("Name cannot be null");
	}

	public String getName() {
		return name;
	}
}