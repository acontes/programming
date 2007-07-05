package org.objectweb.proactive.extra.gcmdeployment;

import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.FileTransferBlock;


public interface VirtualNodeInternal extends VirtualNode {

    /**
     * Added a File Transfer Block to be executed before a node
     * is returned to the appplication
     *
     * @param ftb A File Transfer Block
     */
    public void addFileTransfertBlock(FileTransferBlock ftb);
}
