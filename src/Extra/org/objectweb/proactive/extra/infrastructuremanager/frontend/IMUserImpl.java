package org.objectweb.proactive.extra.infrastructuremanager.frontend;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.core.IMCore;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


public class IMUserImpl implements IMUser {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.IM_USER);

    // Attributes
    private IMCore imcore;

    //----------------------------------------------------------------------//
    // CONSTRUCTORS

    /** ProActive compulsory no-args constructor */
    public IMUserImpl() {
    }

    public IMUserImpl(IMCore imcore) {
        if (logger.isInfoEnabled()) {
            logger.info("IMUser constructor");
        }
        this.imcore = imcore;
    }

    //=======================================================//
    public StringWrapper echo() {
        return new StringWrapper("Je suis le IMUser");
    }

    //=======================================================//

    //----------------------------------------------------------------------//
    // METHODS

    public NodeSet getAtMostNodes(IntWrapper nb, VerifyingScript verifyingScript) {
        if (logger.isInfoEnabled()) {
            logger.info("getAtMostNodes, nb nodes : " + nb);
        }
        return imcore.getAtMostNodes(nb, verifyingScript);
    }

    public NodeSet getExactlyNodes(IntWrapper nb,
        VerifyingScript verifyingScript) {
        if (logger.isInfoEnabled()) {
            logger.info("getExactlyNodes, nb nodes : " + nb);
        }
        return imcore.getExactlyNodes(nb, verifyingScript);
    }

    public void freeNode(Node node) {
        if (logger.isInfoEnabled()) {
            logger.info("freeNode : " + node.getNodeInformation().getName());
        }
        imcore.freeNode(node);
    }

    public void freeNodes(NodeSet nodes) {
        if (logger.isInfoEnabled()) {
            String freeNodes = "";
            for (Node node : nodes) {
                freeNodes += (node.getNodeInformation().getName() + " ");
            }
            logger.info("freeNode : " + freeNodes);
        }
        imcore.freeNodes(nodes);
    }
}
