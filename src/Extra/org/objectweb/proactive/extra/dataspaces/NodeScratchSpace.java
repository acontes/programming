/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.dataspaces.SpaceConfiguration.ScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.exceptions.AlreadyConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;

/**
 * 
 */

// FIXME leave data or remove all directories?
public class NodeScratchSpace {

	private final ScratchSpaceConfiguration scratchConfiguration;

	private final DefaultFileSystemManager fileSystemManager;

	private final Node node;

	private boolean configured = false;

	private FileObject fPartialSpace;

	private String runtimeId;

	private String nodeId;

	private class AppScratchSpaceImpl implements ApplicationScratchSpace {
		private final FileObject fSpace;

		private final Map<String, DataSpacesURI> scratches = new HashMap<String, DataSpacesURI>();

		private final long appId;

		private final SpaceInstanceInfo spaceInstanceInfo;

		private AppScratchSpaceImpl(long appid) throws FileSystemException, ConfigurationException {
			final String appIdString = new Long(appid).toString();
			this.fSpace = createEmptyDirectoryRelative(fPartialSpace, appIdString);
			// or change it and use absolute configuration-created path
			this.appId = appid;
			this.spaceInstanceInfo = new SpaceInstanceInfo(appId, runtimeId, nodeId, scratchConfiguration);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.objectweb.proactive.extra.dataspaces.ApplicationScratchSpace#
		 * close()
		 */
		public void close() throws FileSystemException {
			fSpace.delete(Selectors.SELECT_ALL);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.objectweb.proactive.extra.dataspaces.ApplicationScratchSpace#
		 * getScratchForAO(java.lang.String)
		 */
		public synchronized DataSpacesURI getScratchForAO(String aoid) throws FileSystemException {
			DataSpacesURI uri;

			if (!scratches.containsKey(aoid)) {
				createEmptyDirectoryRelative(fSpace, aoid);
				uri = DataSpacesURI.createScratchSpaceURI(appId, runtimeId, nodeId, aoid);
				scratches.put(aoid, uri);
			} else
				uri = scratches.get(aoid);

			return uri;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.objectweb.proactive.extra.dataspaces.ApplicationScratchSpace#
		 * getSpaceInstanceInfo()
		 */
		public SpaceInstanceInfo getSpaceInstanceInfo() {
			return spaceInstanceInfo;
		}
	}

	// TODO: here we use Node to get nodeId and runtimeId, and in
	// AppScratchSpace we use just aoId; let's make it consistent;
	public NodeScratchSpace(ScratchSpaceConfiguration conf, DefaultFileSystemManager manager, Node node) {
		this.scratchConfiguration = conf;
		this.fileSystemManager = manager;
		this.node = node;
	}

	// TODO check "other stuff" like os permitions in more explicit way? Cleanup
	// after failing back?
	/**
	 * @throws ConfigurationException
	 * @throws FileSystemException
	 * @throws AlreadyConfiguredException
	 * @throws ConfigurationException
	 *             when checking FS capabilities
	 */
	// TODO: hmm maybe we should be consistent with NodeConfigurator about
	// passing arguments in init() ?
	public synchronized void init() throws FileSystemException, AlreadyConfiguredException,
			ConfigurationException {

		if (configured)
			throw new AlreadyConfiguredException();

		nodeId = Utils.getNodeId(node);
		runtimeId = Utils.getRuntimeId(node);

		final String localAccessUrl = Utils.getLocalAccessURL(scratchConfiguration.getUrl(),
				scratchConfiguration.getPath(), scratchConfiguration.getHostname());
		final String partialSpacePath = scratchConfiguration.appendBasePath(localAccessUrl, runtimeId,
				nodeId, null);

		fPartialSpace = fileSystemManager.resolveFile(partialSpacePath);
		checkCapabilities(fPartialSpace.getFileSystem());
		fPartialSpace.delete(Selectors.EXCLUDE_SELF);
		fPartialSpace.createFolder();
		configured = true;
	}

	/**
	 * @param appid
	 * @return
	 * @throws FileSystemException
	 * @throws NotConfiguredException
	 * @throws ConfigurationException
	 *             when provided information is not enough to build a complete
	 *             space definition
	 */
	public synchronized ApplicationScratchSpace initForApplication(long appid) throws FileSystemException,
			NotConfiguredException, ConfigurationException {

		checkIfConfigured();
		return new AppScratchSpaceImpl(appid);
	}

	/**
	 * Removes initialized directory on finalization.
	 * 
	 * @throws FileSystemException
	 * @throws NotConfiguredException
	 */
	public synchronized void close() throws FileSystemException, NotConfiguredException {
		checkIfConfigured();

		final FileObject fRuntime = fPartialSpace.getParent();

		// rm -r node
		fPartialSpace.delete(Selectors.SELECT_ALL);

		// try to remove runtime file
		fRuntime.delete();
	}

	private FileObject createEmptyDirectoryRelative(final FileObject parent, final String path)
			throws FileSystemException {

		FileObject f = parent.resolveFile(path);
		f.delete(Selectors.EXCLUDE_SELF);
		f.createFolder();
		return f;
	}

	private void checkCapabilities(FileSystem fs) throws ConfigurationException {
		final Capability[] expected = new Capability[] { Capability.CREATE, Capability.DELETE,
				Capability.GET_TYPE, Capability.LIST_CHILDREN, Capability.READ_CONTENT,
				Capability.WRITE_CONTENT };

		for (int i = 0; i < expected.length; i++) {
			final Capability capability = expected[i];
			if (fs.hasCapability(capability))
				throw new ConfigurationException("Specified file system provider does not support "
						+ capability);
		}
	}

	private void checkIfConfigured() throws NotConfiguredException {
		if (!configured)
			throw new NotConfiguredException();
	}
}