/*
 * Created on Sep 15, 2003
 */
package nonregressiontest.group.result;

import java.util.Iterator;

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
    private A resultTypedGroup = null;
    private A resultResultTypedGroup = null;

    public Test() {
        super("operations on a result group",
            "do a oneway call and an (a)synchronous call on a result group");
    }

    public void action() throws Exception {
        this.resultTypedGroup.onewayCall();
        this.resultResultTypedGroup = this.resultTypedGroup.asynchronousCall();
    }

    public void endTest() throws Exception {
        // nothing to do
    }

    public void initTest() throws Exception {
        // nothing to do
    }

    public boolean postConditions() throws Exception {
        Group group = ProActiveGroup.getGroup(this.resultTypedGroup);
        Group groupResult = ProActiveGroup.getGroup(this.resultResultTypedGroup);

        // was the oneway call on the result group ok ?
        boolean allOnewayCallDone = true;
        Iterator it = group.iterator();
        while (it.hasNext()) {
            allOnewayCallDone &= ((A) it.next()).isOnewayCallReceived();
        }
        if (!allOnewayCallDone) {
            return false;
        }

        // has the result-result group the same size as the caller group (result group) ?
        if (groupResult.size() != group.size()) {
            return false;
        }

        // is the result of the n-th group member at the n-th position in the result-result group ?
        boolean rightRankingOfResults = true;
        for (int i = 0; i < group.size(); i++) {
            rightRankingOfResults &= ((A) groupResult.get(i)).getName().equals((((A) group.get(
                    i)).asynchronousCall()).getName());
        }
        return rightRankingOfResults;
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
        A typedGroup = (A) ProActiveGroup.newGroup(A.class.getName(), params,
                nodes);
        this.resultTypedGroup = typedGroup.asynchronousCall();

        boolean NoOnewayCallDone = true;
        Group group = ProActiveGroup.getGroup(this.resultTypedGroup);
        Iterator it = group.iterator();
        while (it.hasNext()) {
            NoOnewayCallDone &= !((A) it.next()).isOnewayCallReceived();
        }
        return (NoOnewayCallDone && (this.resultResultTypedGroup == null));
    }
}
