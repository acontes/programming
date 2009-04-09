/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileSystemManager;

/**
 * name: NodeScratchSpace resp: - initialization of rt/node config (with
 * testing) - produces inner class with AppScratchSpace interface - removes
 * initialized directory on finalization
 * 
 */
public class NodeScratchSpace {

	private class AppScratchSpaceImpl implements ApplicationScratchSpace {

		public AppScratchSpaceImpl(long appid) {
		}

		public void close() {
			// TODO Auto-generated method stub

		}

		public SpaceFileURI getScratchForAO(String aoid) {
			// TODO Auto-generated method stub
			return null;
		}

		public SpaceInstanceInfo getSpaceInstanceInfo() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public NodeScratchSpace(SpaceConfiguration conf, FileSystemManager manager) {
	}

	public void init() {

	}

	public ApplicationScratchSpace initForApplication(long appid) {
		return new AppScratchSpaceImpl(appid);
	}

	public void close() {
	}
}
