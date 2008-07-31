package functionalTests.annotations.activeobject.inputs.reject;

import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.annotation.migration.MigrationSignal;
import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;

@ActiveObject
public class ErrorPrivate {
	// error - private method
	@MigrationSignal
	private void migrateTo3() throws MigrationException {
		PAMobileAgent.migrateTo(new Object());
	}
}
