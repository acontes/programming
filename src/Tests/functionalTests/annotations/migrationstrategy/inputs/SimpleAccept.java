package functionalTests.annotations.migrationstrategy.inputs;

import org.objectweb.proactive.extra.annotation.migration.strategy.OnDeparture;

public class SimpleAccept {

	@OnDeparture
	public void leaving() {
		
	}
}
