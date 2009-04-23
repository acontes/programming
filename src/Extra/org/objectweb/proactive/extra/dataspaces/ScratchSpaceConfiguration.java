/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileName;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

//TODO provide setters or make it immutable with <code>withXXX</code> methods creating new instance
//(Use case: scratch space configuration; it is needed for path and url)
public class ScratchSpaceConfiguration extends SpaceConfiguration {

	public static final String HOSTNAME_VARIABLE_KEYWORD = "#{hostname}";

	/**
	 * Note: When local path is not specified and URL is specified, protocol
	 * specified in URL is used to access data locally. If remote access (URL)
	 * is not specified and only local path is specified, default ProActive
	 * provider is started, hence remote access is always possible. At least one
	 * access (remote or local) must be defined.
	 * 
	 * @param url
	 *            Access URL to scratch, used for accessing from remote nodes.
	 *            URL defines which protocol is used to access the data from
	 *            remote node, and some additional information for protocol like
	 *            path, sometimes user name and password. This URL may contain
	 *            special variable #{hostname} that is filled with actual host
	 *            name on deployment, so scratch configuration definition may be
	 *            more generic — sufficient to use in context of generic host
	 *            configuration.
	 * @param path
	 *            Points where to store scratch data space files. This path is
	 *            local to host that is referencing this scratch configuration.
	 * @throws ConfigurationException
	 *             when one of above's contract condition fails
	 */
	public static ScratchSpaceConfiguration createConfiguration(String url, String path)
			throws ConfigurationException {

		return new ScratchSpaceConfiguration(url, path);
	}

	private String unresolvedUrl;

	private ScratchSpaceConfiguration(String url, String path) throws ConfigurationException {
		super(path, SpaceType.SCRATCH);
		this.unresolvedUrl = url;

		if (url == null && path == null)
			throw new ConfigurationException("Provide local or remote access definition");
	}

	@Override
	public String getHostname() {
		return Utils.getHostname();
	}

	@Override
	public String getUrl() {
		if (unresolvedUrl == null)
			return null;
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