/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

/**
 * Stores information needed to configure an instance of a data space.
 */
// TODO javadocs
public abstract class SpaceConfiguration {

	protected final String path;

	protected final SpaceType spaceType;

	protected final String hostname;

	protected final String url;

	protected SpaceConfiguration(String url, String path, String hostname, SpaceType spaceType)
			throws ConfigurationException {
		this.url = url;
		this.path = path;
		this.hostname = hostname;
		this.spaceType = spaceType;

		final boolean localDefined;
		if (path != null && hostname != null)
			localDefined = true;
		else if (path == null && hostname == null)
			localDefined = false;
		else
			throw new ConfigurationException("Local path provided without hostname specified");

		if (!localDefined && url == null)
			throw new ConfigurationException("Provide local or remote access definition");
	}

	public final String getUrl() {
		return url;
	}

	public final String getPath() {
		return path;
	}

	public final String getHostname() {
		return hostname;
	}

	public final SpaceType getType() {
		return spaceType;
	}

	public final boolean isComplete() {
		// remaining contract is guaranteed by constructor
		return url != null;
	}
}
