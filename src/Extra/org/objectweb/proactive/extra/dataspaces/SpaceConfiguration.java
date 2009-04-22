/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

// TODO provide setters or make it immutable with <code>withXXX</code> methods creating new instance
// (Use case: scratch space configuration) 
// TODO check where it is created
/**
 * Stores information needed to configure an instance of a data space.
 */
public abstract class SpaceConfiguration {

	protected final String url;

	protected final String path;

	protected final SpaceType spaceType;

	/**
	 * @see {@link ScratchSpaceConfiguration#ScratchSpaceConfiguration(String, String)}
	 * @throws ConfigurationException
	 */
	public static ScratchSpaceConfiguration createScratchSpaceConfiguration(String url, String path)
			throws ConfigurationException {

		return new ScratchSpaceConfiguration(path, path);
	}

	/**
	 * @see {@link InputOutputSpaceConfiguration#InputOutputSpaceConfiguration(String, String, String, SpaceType, String)}
	 * @throws ConfigurationException
	 */
	public static InputOutputSpaceConfiguration createInputSpaceConfiguration(String url, String path,
			String hostname, String name) throws ConfigurationException {

		return new InputOutputSpaceConfiguration(url, path, hostname, SpaceType.INPUT, name);
	}

	/**
	 * @see {@link InputOutputSpaceConfiguration#InputOutputSpaceConfiguration(String, String, String, SpaceType, String)}
	 * @throws ConfigurationException
	 */
	public static InputOutputSpaceConfiguration createOutputSpaceConfiguration(String url, String path,
			String hostname, String name) throws ConfigurationException {

		return new InputOutputSpaceConfiguration(url, path, hostname, SpaceType.OUTPUT, name);
	}

	/**
	 * @see {@link InputOutputSpaceConfiguration#InputOutputSpaceConfiguration(String, String, String, SpaceType, String)}
	 * @throws ConfigurationException
	 */
	public static InputOutputSpaceConfiguration createInputOutputSpaceConfiguration(String url, String path,
			String hostname, String name, SpaceType type) throws ConfigurationException {

		if (type != SpaceType.INPUT && type != SpaceType.OUTPUT)
			throw new IllegalArgumentException("Use input or output data space type");

		return new InputOutputSpaceConfiguration(url, path, hostname, type, name);
	}

	protected SpaceConfiguration(String url, String path, SpaceType spaceType) {
		this.url = url;
		this.path = path;
		this.spaceType = spaceType;
	}

	public String getUrl() {
		return url;
	}

	public String getPath() {
		return path;
	}

	public abstract String getHostname() throws ConfigurationException;

	public SpaceType getType() {
		return spaceType;
	}

	public static class InputOutputSpaceConfiguration extends SpaceConfiguration {

		protected final String hostname;

		protected final String name;

		/**
		 * Note: When local path and hostname are not specified and URL is
		 * specified, protocol from URL is used to access data locally. If
		 * remote access (URL) is not specified and only local path and hostname
		 * are specified, default ProActive provider is started, hence remote
		 * access is always possible. At least one access (remote or local) must
		 * be defined.
		 *
		 * @param url
		 *            Access URL to this input (output), used for accessing from
		 *            remote nodes. URL defines which protocol is used to access
		 *            the data from remote node, and some additional information
		 *            for protocol like path, sometimes user name and password.
		 *
		 *
		 * @param path
		 *            Local path to input (output) data. This path is local to
		 *            host with host name specified in <code>hostname</code>
		 *            attribute.
		 * @param hostname
		 *            name of host where data are stored. It is always used in
		 *            conjunction with a path attribute. Input (output) data can
		 *            be accessed locally on host with that name. This attribute
		 *            may consist of special variable <code>#{deployer}</code>
		 *            meaning that the path is local to the deployer.
		 * @param spaceType
		 *            input or output data space type
		 * @param name
		 *            Unique name of input (output) data space. Default value is
		 *            just name "default", which implies being default input
		 *            (output). e.g. id="instances"
		 * @throws ConfigurationException
		 *             when one of above's contract condition fails
		 */
		private InputOutputSpaceConfiguration(String url, String path, String hostname, SpaceType spaceType,
				String name) throws ConfigurationException {

			super(url, path, spaceType);
			this.hostname = hostname;
			this.name = name;
			boolean localDefined;

			if (spaceType != SpaceType.INPUT && spaceType != SpaceType.OUTPUT)
				throw new ConfigurationException("Use input or output data space type");

			if (path != null && hostname != null)
				localDefined = true;
			else if (path == null && hostname == null)
				localDefined = false;
			else
				throw new ConfigurationException("Path and hostname must be specified together");

			if (!localDefined && url == null)
				throw new ConfigurationException("Provide local or remote access definition");

			if (name == null)
				throw new ConfigurationException("Name cannot be null");
		}

		public String getName() {
			return name;
		}

		@Override
		public String getHostname() {
			return hostname;
		}
	}

	public static class ScratchSpaceConfiguration extends SpaceConfiguration {

		private static final String HOSTNAME_VARIABLE_KEYWORD = "#{hostname}";

		private static final String FILE_URI_SCHEME = "file://";

		private boolean isResolved = false;

		private String hostname;

		private String resolvedUrl;

		/**
		 * Note: When local path is not specified and URL is specified, protocol
		 * specified in URL is used to access data locally. If remote access
		 * (URL) is not specified and only local path is specified, default
		 * ProActive provider is started, hence remote access is always
		 * possible. At least one access (remote or local) must be defined.
		 *
		 * @param url
		 *            Access URL to scratch, used for accessing from remote
		 *            nodes. URL defines which protocol is used to access the
		 *            data from remote node, and some additional information for
		 *            protocol like path, sometimes user name and password. This
		 *            URL may contain special variable #{hostname} that is
		 *            filled with actual host name on deployment, so scratch
		 *            configuration definition may be more generic — sufficient
		 *            to use in context of generic host configuration.
		 * @param path
		 *            Points where to store scratch data space files. This path
		 *            is local to host that is referencing this scratch
		 *            configuration.
		 * @throws ConfigurationException
		 *             when one of above's contract condition fails
		 */
		private ScratchSpaceConfiguration(String url, String path) throws ConfigurationException {
			super(url, path, SpaceType.SCRATCH);

			if (url == null && path == null)
				throw new ConfigurationException("Provide local or remote access definition");
		}

		// TODO here
		public String getSpaceURL() {
			if (path != null)
				return FILE_URI_SCHEME + this.path;
			else
				return this.url;
		}

		/**
		 * Use hostname of a local machine to resolve any #{hostname} variable
		 * in URL. Cannot be undone or repeated.
		 *
		 * @throws ConfigurationException
		 *             when hostname has been already resolved
		 */
		public synchronized void resolveHostname() throws IllegalStateException, ConfigurationException {
			if (isResolved)
				throw new ConfigurationException("Hostname has been already resolved");

			hostname = Utils.getHostname();
			final StringBuffer sb = new StringBuffer(url);
			final int start = sb.indexOf(HOSTNAME_VARIABLE_KEYWORD);

			// not found
			if (start == -1) {
				resolvedUrl = url;
				isResolved = true;
				return;
			}

			final int end = start + HOSTNAME_VARIABLE_KEYWORD.length();
			sb.replace(start, end, hostname);
			resolvedUrl = sb.toString();
			isResolved = true;
		}

		public synchronized boolean isHostnameResolved() {
			return isResolved;
		}

		@Override
		public synchronized String getHostname() throws ConfigurationException {
			if (!isResolved)
				throw new ConfigurationException("Hostname has not been resolved yet");
			return hostname;
		}

		@Override
		public String getUrl() {
			return isResolved ? resolvedUrl : url;
		}
	}
}
