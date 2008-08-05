package functionalTests.annotations.activeobject.inputs.accept;

import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.annotation.migration.MigrationSignal;
import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;

@ActiveObject
public class AcceptSimple {
	// OK
	@MigrationSignal
	public void migrateTo1() throws MigrationException {
		PAMobileAgent.migrateTo(new Object());
	}
	
	@MigrationSignal
	public void migrateTo2() throws MigrationException {
		org.objectweb.proactive.api.PAMobileAgent.migrateTo(new Object());
	}
	
	@MigrationSignal
	public int migrateTo3() throws MigrationException {
		PAMobileAgent.migrateTo(new Object());
		return 0;
	}
	
}
