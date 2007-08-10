/**
 *
 */
package org.objectweb.proactive.extra.infrastructuremanager.frontend;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.core.IMCore;
import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMState;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;


/**
 * @author Ellendir
 *
 */
public class IMMonitoringImpl implements IMMonitoring {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.IM_MONITORING);

    // Attributes
    private IMCore imcore;

    //----------------------------------------------------------------------//
    // CONSTRUTORS

    /** ProActive compulsory no-args constructor */
    public IMMonitoringImpl() {
    }

    public IMMonitoringImpl(IMCore imcore) {
        if (logger.isInfoEnabled()) {
            logger.info("IMMonitoring constructor");
        }
        this.imcore = imcore;
    }

    //=======================================================//
    public StringWrapper echo() {
        return new StringWrapper("Je suis le IMMonitoring");
    }

    //=======================================================//
    public HashMap<String, ProActiveDescriptor> getListDescriptor() {
        if (logger.isInfoEnabled()) {
            logger.info("getListDescriptor");
        }
        return imcore.getListPAD();
    }

    public HashMap<String, ArrayList<VirtualNode>> getDeployedVirtualNodeByPad() {
        if (logger.isInfoEnabled()) {
            logger.info("getDeployedVirtualNodeByPad");
        }
        return imcore.getDeployedVirtualNodeByPad();
    }

    public ArrayList<IMNode> getListAllIMNodes() {
        if (logger.isInfoEnabled()) {
            logger.info("getListAllIMNodes");
        }
        return imcore.getListAllNodes();
    }

    public ArrayList<IMNode> getListFreeIMNode() {
        if (logger.isInfoEnabled()) {
            logger.info("getListFreeIMNode");
        }
        return imcore.getListFreeIMNode();
    }

    public ArrayList<IMNode> getListBusyIMNode() {
        if (logger.isInfoEnabled()) {
            logger.info("getListBusyIMNode");
        }
        return imcore.getListBusyIMNode();
    }

    public IntWrapper getNumberOfFreeResource() {
        if (logger.isInfoEnabled()) {
            logger.info("getNumberOfFreeResource");
        }
        return imcore.getSizeListFreeIMNode();
    }

    public IntWrapper getNumberOfBusyResource() {
        if (logger.isInfoEnabled()) {
            logger.info("getNumberOfBusyResource");
        }
        return imcore.getSizeListBusyIMNode();
    }

    public IntWrapper getNumberOfDownResource() {
        return this.imcore.getSizeListDownIMNode();
    }

    public IntWrapper getNumberOfAllResources() {
        return this.imcore.getNbAllIMNode();
    }
    
    public IMState addIMEventListener(IMEventListener listener) {
    	return imcore.addIMEventListener(listener);
    }
}
