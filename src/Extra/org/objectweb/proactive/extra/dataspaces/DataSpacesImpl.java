/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;

/**
 * resp: - implements PADataSpaces API within a node (and for app) col: -
 * SpacesMountManager - CachingSpacesDirectory (or SpaceDirectory interface)
 * 
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

	public FileObject resolveDefaultInput() {
		return null;
	}

	public FileObject resolveDefaultOutput() {
		return null;
	}

	public FileObject resolveScratchForAO() {
		return null;
	}

	public Set<String> getAllKnownInputNames() {
		return null;
	}

	public Set<String> getAllKnownOutputNames() {
		return null;
	}

	public Map<String, FileObject> resolveAllKnownInputs() {
		return null;
	}

	public Map<String, FileObject> resolveAllKnownOutputs() {
		return null;
	}

	public FileObject resolveDefaultInputBlocking(long timeoutMillis) {
		return null;
	}

	public FileObject resolveDefaultOutputBlocking(long timeoutMillis) {
		return null;
	}

	public FileObject resolveFile(String uri) {
		return null;
	}

	public String getURI(FileObject fileObject) {
		return null;
	}

	public FileObject resolveInput(String name) {
		return null;
	}

	public FileObject resolveOutput(String name) {
		return null;
	}

	public FileObject resolveInputBlocking(String name, long timeoutMillis) {
		return null;
	}

	public FileObject resolveOutputBlocking(String name, long timeoutMillis) {
		return null;
	}

	public String addInput(String name, String path, URL url) {
		return null;
	}

	public String addOutput(String name, String path, URL url) {
		return null;
	}
}
