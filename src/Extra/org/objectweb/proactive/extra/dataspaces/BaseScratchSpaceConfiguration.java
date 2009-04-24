/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileName;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

/**
 * Stores information for a base of concrete scratch space configuration -
 * {@link ScratchSpaceConfiguration}, which can derived from instance of this
 * class.
 * <p>
 * Usually, scratch configuration is specified by providing access ways for
 * temporary storage, often with abstract hostname definition that need to be
 * filled on target host. Also, access path/URL is later appended with concrete
 * subdirectory created for each runtime, node and application identifier. That
 * makes static configuration for host not complete until actual deployment
 * takes place. This class is intended to provide a way to store this static
 * configuration and derive actual scratch space configuration on target host
 * from that instance.
 * 
 * @see ScratchSpaceConfiguration
 */
public class BaseScratchSpaceConfiguration {
	public static final String HOSTNAME_VARIABLE_KEYWORD = "#{hostname}";

	/**
	 * Appends subdirectories to provided base location (path or URL), managing
	 * file separators (slashes) in appropriate way.
	 * 
	 * @param baseLocation
	 *            Base location (path or URL) which is the root for appended
	 *            subdirectories. Can be <code>null</code>.
	 * @param subDirs
	 *            Any number of subdirectories to be appended to provided
	 *            location. Order of subdirectories responds to directories
	 *            hierarchy and result path. None of it can be <code>null</code>
	 *            .
	 * @return location with appended subdirectories with appropriate slashes
	 *         (separators). <code>null</code> if <code>basePath</code> is
	 *         <code>null</code>.
	 */
	public static String appendSubDirs(final String baseLocation, final String... subDirs) {
		if (baseLocation == null)
			return null;

		final StringBuilder sb = new StringBuilder(baseLocation);
		boolean skipFirst = baseLocation.charAt(baseLocation.length() - 1) == FileName.SEPARATOR_CHAR;
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
	 * Creates base for a scratch space configuration.
	 * <p>
	 * At least one access way should be specified at this stage - local path or
	 * remote access URL. Hostname is later filled at
	 * {@link #createScratchSpaceConfiguration(String...)}.
	 * <p>
	 * Remote access URL may contain special metavariable
	 * {@value #HOSTNAME_VARIABLE_KEYWORD} that is later filled by localhost
	 * hostname.
	 * 
	 * @param url
	 *            Base access URL to scratch space, where subdirectories will be
	 *            created. Used for accessing from remote nodes. URL defines
	 *            which protocol is used to access the data from remote node,
	 *            and some additional information for protocol like path,
	 *            sometimes user name and password. This URL may contain special
	 *            variable {@value #HOSTNAME_VARIABLE_KEYWORD} that is later
	 *            filled with actual host name for caller, so scratch
	 *            configuration definition may be more generic — sufficient to
	 *            use in context of generic host configuration. May be
	 *            <code>null</code> if remote access URL is not yet specified.
	 * @param path
	 *            Base of local path for scratch data space. This path is local
	 *            to host where this base scratch configuration will be used.
	 *            May be <code>null</code> if there is no local access
	 *            specified.
	 * @throws ConfigurationException
	 *             When no access was specified.
	 */
	public BaseScratchSpaceConfiguration(final String url, final String path) throws ConfigurationException {
		this.url = url;
		this.path = path;

		if (url == null && path == null)
			throw new ConfigurationException("No access specified (neither local, nor remote)");
	}

	/**
	 * @return remote access URL with hostname metavariable filled with actual
	 *         localhost hostname. May be <code>null</code>
	 */
	public String getUrl() {
		if (url == null)
			return null;
		return url.replace(HOSTNAME_VARIABLE_KEYWORD, Utils.getHostname());
	}

	/**
	 * @return local access path. May be <code>null</code>
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Creates concrete scratch space configuration, derived from this base
	 * scratch space configuration.
	 * <p>
	 * Hostname metavariable is replaced by localhost hostname in target URL,
	 * and provided subdirectories are appended to local access path and remote
	 * access URL.
	 * 
	 * @param subDirs
	 *            Any number of subdirectories to be appended to target URL and
	 *            path. Order of subdirectories responds to directories
	 *            hierarchy and result path. None of it can be <code>null</code>
	 * @return scratch space configuration derived from this base configuration.
	 * @throws ConfigurationException
	 *             when derived configuration is not correct (shouldn't happen)
	 */
	public ScratchSpaceConfiguration createScratchSpaceConfiguration(final String... subDirs)
			throws ConfigurationException {
		final String newUrl = appendSubDirs(getUrl(), subDirs);
		final String newPath = appendSubDirs(getPath(), subDirs);
		return new ScratchSpaceConfiguration(newUrl, newPath, Utils.getHostname());
	}
}
