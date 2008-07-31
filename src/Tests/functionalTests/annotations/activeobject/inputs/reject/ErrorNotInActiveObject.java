package functionalTests.annotations.activeobject.inputs.reject;

import org.objectweb.proactive.annotation.migration.MigrationSignal;
import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;

public class ErrorNotInActiveObject {
	// OK
	@MigrationSignal
	public void migrateTo2() throws MigrationException {
		PAMobileAgent.migrateTo(new Object());
	}
}