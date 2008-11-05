package functionalTests.annotations.migrationstrategy.inputs;

import org.objectweb.proactive.extra.annotation.migration.strategy.OnArrival;
import org.objectweb.proactive.extra.annotation.migration.strategy.OnDeparture;

public class SimpleAccept {

	@OnDeparture
	public void leaving() {
		System.out.println("Leaving Node...");
	}
	
	@OnArrival
	public void arriving() {
		System.out.println("Finally home...");
	}
}
