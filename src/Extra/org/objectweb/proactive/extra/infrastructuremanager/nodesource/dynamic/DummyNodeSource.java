package org.objectweb.proactive.extra.infrastructuremanager.nodesource.dynamic;

import java.io.File;
import java.util.ArrayList;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.IMNodeSource;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.pad.PADNodeSource;


/**
 * Simple implementation of what can be a {@link DynamicNodeSource}.
 * This Dummy class, create a {@link PADNodeSource}, deployes nodes,
 * and acts as if it was a dynamic source...
 * @author proactive team
 *
 */
public class DummyNodeSource extends DynamicNodeSource {

    /**
     *
     */
    private static final long serialVersionUID = 8213062492772541033L;
    private PADNodeSource manager;
    private IMNodeSource stubOnThis;

    public DummyNodeSource(String id, int nbMaxNodes, int nice, int ttr) {
        super("Dummy:" + id, nbMaxNodes, nice, ttr);
    }

    public DummyNodeSource() {
    }

    @Override
    public void initActivity(Body body) {
        try {
            stubOnThis = (IMNodeSource) ProActive.getStubOnThis();
            manager = (PADNodeSource) ProActive.newActive(PADNodeSource.class.getCanonicalName(),
                    new Object[] { "DummyPADNS" });
            manager.deployAllVirtualNodes(new File(getClass()
                                                       .getResource("/org/objectweb/proactive/examples/scheduler/test.xml")
                                                       .getPath()), null);
        } catch (ActiveObjectCreationException e) {
            logger.error("Error while creating Dummy PADNodeSource", e);
            e.printStackTrace();
        } catch (NodeException e) {
            logger.error("Error while creating Dummy PADNodeSource", e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Error while creating Dummy PADNodeSource", e);
            e.printStackTrace();
        }
        super.initActivity(body);
    }

    @Override
    protected IMNode getNode() {
        ArrayList<IMNode> nodes = manager.getNodesByScript(null, false);
        if (!nodes.isEmpty()) {
            IMNode node = nodes.get(0);
            manager.setBusy(node);
            node.setNodeSource(stubOnThis);
            return node;
        }
        return null;
    }

    @Override
    protected void releaseNode(IMNode node) {
        node.setNodeSource(manager);
        manager.setFree(node);
    }

    @Override
    public BooleanWrapper shutdown() {
        manager.shutdown();
        return super.shutdown();
    }
}
