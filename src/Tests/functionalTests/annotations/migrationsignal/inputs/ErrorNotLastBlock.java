package functionalTests.annotations.migrationsignal.inputs;

import static org.objectweb.proactive.api.PAMobileAgent.*;
import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.migration.signal.MigrationSignal;

@ActiveObject
public class ErrorNotLastBlock {
	
	// error - not last statement
	@MigrationSignal
	public String migrateTo1(boolean onCondition) {
		if(onCondition) {
			org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
			return ""; // the sweet C-style hakz
		} else {
			System.out.println("I refuze to migrate!");
			return "";
		}
	}
	
}
