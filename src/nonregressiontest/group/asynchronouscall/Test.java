/*
 * Created on Sep 12, 2003
 */
package nonregressiontest.group.asynchronouscall;

import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProActiveGroup;
import org.objectweb.proactive.core.node.Node;

import nonregressiontest.descriptor.defaultnodes.TestNodes;
import nonregressiontest.group.A;

import testsuite.test.FunctionalTest;


/**
 * @author Laurent Baduel
 */
public class Test extends FunctionalTest {
    private A typedGroup = null;
    private A resultTypedGroup = null;

    /**
     *
     */
    public Test() {
        super("asynchronous (and synchronous) call on group",
            "do an (a)synchronous call on a previously created group");
    }

    public void action() throws Exception {
        this.resultTypedGroup = this.typedGroup.asynchronousCall();
    }

    public void endTest() throws Exception {
        // nothing to do
    }

    public boolean preConditions() throws Exception {
        Object[][] params = {
                { "Agent0" },
                { "Agent1" },
                { "Agent2" }
            };
        Node[] nodes = {
                TestNodes.getSameVMNode(), TestNodes.getLocalVMNode(),
                TestNodes.getRemoteVMNode()
            };
        this.typedGroup = (A) ProActiveGroup.newGroup(A.class.getName(),
                params, nodes);

        ProActiveGroup.getGroup(this.typedGroup).setRatioMemberToThread(1);

        return (this.typedGroup != null);
    }

    public boolean postConditions() throws Exception {
        // was the result group created ?
        if (this.resultTypedGroup == null) {
            return false;
        }

        Group group = ProActiveGroup.getGroup(this.typedGroup);
        Group groupOfResult = ProActiveGroup.getGroup(this.resultTypedGroup);

        // has the result group the same size as the caller group ?
        if (groupOfResult.size() != group.size()) {
            return false;
        }

        boolean rightRankingOfResults = true;
        for (int i = 0; i < group.size(); i++) {
            rightRankingOfResults &= ((A) groupOfResult.get(i)).getName()
                                      .equals((((A) group.get(i)).asynchronousCall()).getName());
        }

        // is the result of the n-th group member at the n-th position in the result group ?
        return rightRankingOfResults;
    }

    public void initTest() throws Exception {
        // nothing to do : ProActive methods can not be used here
    }
}
