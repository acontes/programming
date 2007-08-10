package org.objectweb.proactive.extra.infrastructuremanager.imnode;

import java.util.Comparator;

import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;

/**
 * Comparator for imnodes :
 * compare two nodes by their chances to verify a script.
 * @author ProActive Team
 * @version 1.0, Jul 12, 2007
 * @since ProActive 3.2
 */
public class IMNodeComparator implements Comparator<IMNode> {

    private VerifyingScript script;

    public IMNodeComparator(VerifyingScript script) {
        this.script = script;
    }

    public int compare(IMNode o1, IMNode o2) {
        int status1 = IMNode.NEVER_TESTED;
        if (o1.getScriptStatus().containsKey(script)) {
            status1 = o1.getScriptStatus().get(script);
        }
        int status2 = IMNode.NEVER_TESTED;
        if (o2.getScriptStatus().containsKey(script)) {
            status2 = o2.getScriptStatus().get(script);
        }
        return status2 - status1;
    }
}
