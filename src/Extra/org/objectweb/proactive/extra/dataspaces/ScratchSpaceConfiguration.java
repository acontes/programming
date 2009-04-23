package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

// TODO javadocs; the only contract - returning SCRATCH as type ;) rest as in superclass
// TODO fix javadocs of using classes (after change)
public class ScratchSpaceConfiguration extends SpaceConfiguration {
	public ScratchSpaceConfiguration(final String url, final String path, final String hostname)
			throws ConfigurationException {
		super(url, path, hostname, SpaceType.SCRATCH);
	}
}
