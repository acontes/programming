/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;

// TODO what about IO exceptions?
/**
 * Implements PADataSpaces API within a node (and an application).
 */
public class DataSpacesImpl {

	private final SpacesMountManager spacesMountManager;

	private final SpacesDirectory spacesDirectory;

	private final ApplicationScratchSpace appScratchSpace;

	private final long appId;

	public DataSpacesImpl(SpacesMountManager smm, SpacesDirectory sd, ApplicationScratchSpace ass, long appId) {
		appScratchSpace = ass;
		spacesDirectory = sd;
		spacesMountManager = smm;
		this.appId = appId;
	}

	/**
	 * Implementation (more generic) method for resolveDefaultInput and
	 * resolveDefaultOutput.
	 * 
	 * @see {@link PADataSpaces#resolveDefaultInput()}
	 * @see {@link PADataSpaces#resolveDefaultOutput()}
	 * @return FileObject received from SpacesMountManager instance
	 * @throws IllegalArgumentException
	 *             when specified space type is neither input nor output
	 * @throws FileSystemException
	 */
	public FileObject resolveDefaultInputOutput(SpaceType type) throws IllegalArgumentException,
			FileSystemException {
		assertIsInputOrOutput(type);

		final DataSpacesURI defaultInputURI = DataSpacesURI.createInOutSpaceURI(appId, type, type
				.getDefaultName());

		final FileObject fo = spacesMountManager.resolveFile(defaultInputURI);
		return fo;
	}

	/**
	 * @see {@link PADataSpaces#resolveScratchForAO()}
	 * @return FileObject received from SpacesMountManager instance
	 * @throws FileSystemException
	 */
	public FileObject resolveScratchForAO() throws FileSystemException {
		final String aoid = Utils.extractAOId();
		final DataSpacesURI scratchURI = appScratchSpace.getScratchForAO(aoid);
		final FileObject fo = spacesMountManager.resolveFile(scratchURI);

		return fo;
	}

	/**
	 * Implementation (more generic) method for getAllKnownInputNames and
	 * getAllKnownInputNames.
	 * 
	 * @see {@link PADataSpaces#getAllKnownInputNames()}
	 * @see {@link PADataSpaces#getAllKnownOutputNames()}
	 * @see {@link SpacesDirectory#lookupAll(DataSpacesURI)}
	 * 
	 * @param type
	 *            of data spaces (input or output)
	 * @return set of known names
	 * @throws IllegalArgumentException
	 *             when specified space type is neither input nor output
	 */
	public Set<String> getAllKnownInputOutputNames(SpaceType type) throws IllegalArgumentException {
		assertIsInputOrOutput(type);

		final DataSpacesURI aURI = DataSpacesURI.createURI(appId, type);
		final Set<SpaceInstanceInfo> infos = spacesDirectory.lookupAll(aURI);
		final Set<String> names = new HashSet<String>();

		for (SpaceInstanceInfo sii : infos) {
			names.add(sii.getName());
		}
		return names;
	}

	/**
	 * Implementation (more generic) method for resolveAllKnownInputs and
	 * resolveAllKnownOutputs.
	 * 
	 * @see {@link PADataSpaces#resolveAllKnownInputs()}
	 * @see {@link PADataSpaces#resolveAllKnownOutputs()}
	 * @param type
	 * @return
	 * @throws FileSystemException
	 * @throws IllegalArgumentException
	 *             when specified space type is neither input nor output
	 */
	public Map<String, FileObject> resolveAllKnownInputsOutputs(SpaceType type) throws FileSystemException,
			IllegalArgumentException {

		assertIsInputOrOutput(type);
		final DataSpacesURI uri = DataSpacesURI.createURI(appId, type);
		final Map<SpaceInstanceInfo, FileObject> spaces = spacesMountManager.resolveSpaces(uri);
		final Map<String, FileObject> ret = new HashMap<String, FileObject>(spaces.size());

		for (Entry<SpaceInstanceInfo, FileObject> entry : spaces.entrySet()) {
			final String name = entry.getKey().getName();
			ret.put(name, entry.getValue());
		}
		return ret;
	}

	/**
	 * Implementation (more generic) method for resolveDefaultInputBlocking and
	 * resolveDefaultOutputBlocking.
	 * 
	 * @see {@link PADataSpaces#resolveDefaultInputBlocking(long))}
	 * @see {@link PADataSpaces#resolveDefaultOutputBlocking(long))}
	 * @param timeoutMillis
	 * @param type
	 * @return
	 * @throws FileSystemException
	 * @throws IllegalArgumentException
	 */
	public FileObject resolveDefaultInputOutputBlocking(long timeoutMillis, SpaceType type)
			throws IllegalArgumentException, FileSystemException {

		assertIsInputOrOutput(type);
		// TODO FIXME it is not blocking ;-)
		return resolveDefaultInputOutput(type);
	}

	/**
	 * @see {@link PADataSpaces#resolveFile(String)}
	 * @param uri
	 * @return
	 * @throws MalformedURIException
	 * @throws FileSystemException
	 */
	public FileObject resolveFile(String uri) throws MalformedURIException, FileSystemException {
		final DataSpacesURI spaceURI = DataSpacesURI.parseURI(uri);
		return spacesMountManager.resolveFile(spaceURI);
	}

	/**
	 * @see {@link PADataSpaces#getURI(FileObject)}
	 * @param fileObject
	 * @return
	 */
	public String getURI(FileObject fileObject) {
		return fileObject.getName().getFriendlyURI();
	}

	/**
	 * Implementation (more generic) method for resolveInput and resolveOutput.
	 * 
	 * @see {@link PADataSpaces#resolveInput(String)}
	 * @see {@link PADataSpaces#resolveOutput(String)}
	 * 
	 * @param name
	 * @param type
	 * @return
	 * @throws FileSystemException
	 * @throws IllegalArgumentException
	 *             when specified space type is neither input nor output
	 */
	public FileObject resolveInputOutput(String name, SpaceType type) throws FileSystemException,
			IllegalArgumentException {
		assertIsInputOrOutput(type);
		final DataSpacesURI uri = DataSpacesURI.createInOutSpaceURI(appId, type, name);

		return spacesMountManager.resolveFile(uri);
	}

	/**
	 * Implementation (more generic) method for resolveInputBlocking and
	 * resolveOutputBlocking.
	 * 
	 * @see {@link PADataSpaces#resolveInputBlocking(String, long)}
	 * @see {@link PADataSpaces#resolveOutputBlocking(String, long)}
	 * @param name
	 * @param timeoutMillis
	 * @param type
	 * @return
	 * @throws FileSystemException
	 * @throws IllegalArgumentException
	 *             when specified space type is neither input nor output
	 */
	public FileObject resolveInputOutputBlocking(String name, long timeoutMillis, SpaceType type)
			throws FileSystemException, IllegalArgumentException {

		assertIsInputOrOutput(type);
		// TODO FIXME not blocking yet
		return resolveInputOutput(name, type);
	}

	/**
	 * Implementation (more generic) method for addInput and addOutput.
	 * 
	 * @see {@link PADataSpaces#addInput(String, String, String)}
	 * @see {@link PADataSpaces#addOutput(String, String, String)}
	 * @param name
	 * @param path
	 * @param url
	 * @param type
	 * @return
	 * @throws IllegalArgumentException
	 *             when specified space type is neither input nor output
	 */
	public String addInputOutput(String name, String path, String url, SpaceType type)
			throws IllegalArgumentException {

		assertIsInputOrOutput(type);

		final String hostname = Utils.getHostnameForThis();
		final SpaceConfiguration config = new SpaceConfiguration(url, path, hostname, type, name);
		final SpaceInstanceInfo spaceInstanceInfo = new SpaceInstanceInfo(appId, config);

		spacesDirectory.register(spaceInstanceInfo);
		return DataSpacesURI.createInOutSpaceURI(appId, type, name).toString();
	}

	private void assertIsInputOrOutput(SpaceType type) {
		if (type == SpaceType.SCRATCH)
			throw new IllegalArgumentException("This method can be only used with input or output data space");
	}
}
