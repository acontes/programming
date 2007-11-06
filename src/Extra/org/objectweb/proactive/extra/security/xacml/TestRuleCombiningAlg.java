package org.objectweb.proactive.extra.security.xacml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.Rule;
import com.sun.xacml.combine.RuleCombiningAlgorithm;
import com.sun.xacml.ctx.Result;


public class TestRuleCombiningAlg extends RuleCombiningAlgorithm {
    public TestRuleCombiningAlg() throws URISyntaxException {
        super(new URI("rule-combining-alg:most-specific"));
    }

    @Override
    public Result combine(EvaluationCtx context, List rules) {
        Iterator<Rule> it = rules.iterator();

        while (it.hasNext()) {
            // get the next Rule, and evaluate it
            Rule rule = (Rule) (it.next());
            Result result = rule.evaluate(context);

            // if it returns Permit, then the alg returns Permit
            if (result.getDecision() == Result.DECISION_PERMIT) {
                return result;
            }
        }

        // if nothing returned Permit, then the alg returns Deny
        return new Result(Result.DECISION_DENY);
    }
}
