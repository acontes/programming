/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileName;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

// TODO provide setters or make it immutable with <code>withXXX</code> methods creating new instance
// (Use case: scratch space configuration; it is needed for path and url)
// TODO not sure if inner classes are best choice/ give any benefit here, especially if we specify
// some additional methods in these subclasses
// TODO (related) if we have factory methods, documentation should be directly there perhaps, not
// referencing to private constructor  
/**
 * Stores information needed to configure an instance of a data space.
 */
public abstract class SpaceConfiguration {

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
		// FIXME: the same thing is checked in constructor... with different
		// exception
		if (type != SpaceType.INPUT && type != SpaceType.OUTPUT)
			throw new IllegalArgumentException("Use input or output data space type");

		return new InputOutputSpaceConfiguration(url, path, hostname, type, name);
	}

	protected SpaceConfiguration(String path, SpaceType spaceType) {
		this.path = path;
		this.spaceType = spaceType;
	}

	public abstract String getUrl();

	public String getPath() {
		return path;
	}

	public abstract String getHostname();

	public SpaceType getType() {
		return spaceType;
	}

	public static class InputOutputSpaceConfiguration extends SpaceConfiguration {

		protected final String hostname;

		protected final String name;

		protected final String url;

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
		// FIXME IMO we shouldn't handle #{deployer} at this level; #{deployer}
		// is very context-specific, it should be rather handled in
		// deployment code on deployer side perhaps
		// TODO: now, somebody may provide us input-output space configuration
		// with no URL (meaning -
		// start provider), specify valid local path and wrong hostname; may be
		// we should have some factories for that
		private InputOutputSpaceConfiguration(String url, String path, String hostname, SpaceType spaceType,
				String name) throws ConfigurationException {

			super(path, spaceType);
			this.url = url;
			this.hostname = hostname;
			this.name = name;
			boolean localDefined;

			if (spaceType != SpaceType.INPUT && spaceType != SpaceType.OUTPUT)
				throw new ConfigurationException("Invalid space type for InputOutputSpaceConfiguration");

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

		@Override
		public String getUrl() {
			return url;
		}
	}

	public static class ScratchSpaceConfiguration extends SpaceConfiguration {

		public static final String HOSTNAME_VARIABLE_KEYWORD = "#{hostname}";

		private static final String FILE_URI_SCHEME = "file://";

		private String hostname;

		private String unresolvedUrl;

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
			super(path, SpaceType.SCRATCH);
			this.unresolvedUrl = url;

			if (url == null && path == null)
				throw new ConfigurationException("Provide local or remote access definition");
		}

		// TODO merge it with getLocalAccessUrl in SpaceMountManager
		// = make Utils.getAccessURL(url, path, hostname) ?
		public String getLocalAccessUrl() {
			if (path != null)
				return FILE_URI_SCHEME + this.path;
			else
				return getUrl();
		}

		@Override
		public String getHostname() {
			return Utils.getHostname();
		}

		@Override
		public String getUrl() {
			return unresolvedUrl.replace(HOSTNAME_VARIABLE_KEYWORD, getHostname());
		}

		public String appendBasePath(String basePath, String runtimeId, String nodeId, Long appid) {
			final StringBuffer sb = new StringBuffer(basePath);

			if (!basePath.endsWith(FileName.SEPARATOR))
				sb.append(FileName.SEPARATOR_CHAR);

			if (runtimeId != null)
				sb.append(runtimeId).append(FileName.SEPARATOR_CHAR);

			if (nodeId != null)
				sb.append(nodeId).append(FileName.SEPARATOR_CHAR);

			if (appid != null)
				sb.append(appid);

			return sb.toString();
		}
	}
}
