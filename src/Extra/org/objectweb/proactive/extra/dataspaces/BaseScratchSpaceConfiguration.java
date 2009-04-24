/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileName;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

// TODO javadocs
public class BaseScratchSpaceConfiguration {
	public static final String HOSTNAME_VARIABLE_KEYWORD = "#{hostname}";

	public static String appendSubDir(final String basePath, final String... subDirs) {
		if (basePath == null)
			return null;

		final StringBuilder sb = new StringBuilder(basePath);
		boolean skipFirst = basePath.charAt(basePath.length() - 1) == FileName.SEPARATOR_CHAR;
		for (final String subDir : subDirs) {
			if (skipFirst)
				skipFirst = false;
			else
				sb.append(FileName.SEPARATOR_CHAR);
			sb.append(subDir);
		}
		return sb.toString();
	}

	private String url;

	private String path;

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
	public BaseScratchSpaceConfiguration(final String url, final String path) throws ConfigurationException {
		this.url = url;
		this.path = path;

		if (url == null && path == null)
			throw new ConfigurationException("Provide local or remote access definition");
	}

	public String getUrl() {
		if (url == null)
			return null;
		return url.replace(HOSTNAME_VARIABLE_KEYWORD, Utils.getHostname());
	}

	public String getPath() {
		return path;
	}

	public ScratchSpaceConfiguration createScratchSpaceConfiguration(final String... subDirs)
			throws ConfigurationException {
		final String newUrl = appendSubDir(getUrl(), subDirs);
		final String newPath = appendSubDir(getPath(), subDirs);
		return new ScratchSpaceConfiguration(newUrl, newPath, Utils.getHostname());
	}
}
