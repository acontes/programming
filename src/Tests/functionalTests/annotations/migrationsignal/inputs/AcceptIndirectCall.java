package functionalTests.annotations.migrationsignal.inputs;

import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.migration.MigrationSignal;

@ActiveObject
public class AcceptIndirectCall {

	@MigrationSignal
	public void migrateTo1() throws MigrationException {
		PAMobileAgent.migrateTo(new Object());
	}
	
	@MigrationSignal
	public void migrateTo2() throws MigrationException {
		// calling another method that migrates
		migrateTo1();
	}
}
