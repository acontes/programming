/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs.FileObject;

/**
 * resp: - contains all API methods - finds out node-specific objects -
 * delegates method calls col: - DataSpacesImpl
 */
public class PADataSpaces {

	private PADataSpaces() {
	}

	public static FileObject resolveDefaultInput() {
		return null;
	}

	public static FileObject resolveDefaultOutput() {
		return null;
	}

	public static FileObject resolveScratchForAO() {
		return null;
	}

	public static Set<String> getAllKnownInputNames() {
		return null;
	}

	public static Set<String> getAllKnownOutputNames() {
		return null;
	}

	public static Map<String, FileObject> resolveAllKnownInputs() {
		return null;
	}

	public static Map<String, FileObject> resolveAllKnownOutputs() {
		return null;
	}

	public static FileObject resolveDefaultInputBlocking(long timeoutMilis) {
		return null;
	}

	public static FileObject resolveDefaultOutputBlocking(long timeoutMilis) {
		return null;
	}

	public static FileObject resolveFile(String uri) {
		return null;
	}

	public static String getURI(FileObject fileObject) {
		return null;
	}

	public static FileObject resolveInput(String name) {
		return null;
	}

	public static FileObject resolveOutput(String name) {
		return null;
	}

	public static FileObject resolveInputBlocking(String name, long timeoutMilis) {
		return null;
	}

	public static FileObject resolveOutputBlocking(String name, long timeoutMilis) {
		return null;
	}

	public static String addInput(String name, String path) {
		return null;
	}

	public static String addInput(URL url) {
		return null;
	}

	public static String addOutput(String name, String path) {
		return null;
	}

	public static String addOutput(URL url) {
		return null;
	}

	/*
	 * public static FileObject resolveScratchForAO(String node: ) {
	 * 
	 * }
	 */

}
