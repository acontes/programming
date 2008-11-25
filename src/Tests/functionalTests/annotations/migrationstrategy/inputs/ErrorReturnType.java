package functionalTests.annotations.migrationstrategy.inputs;

import org.objectweb.proactive.extra.annotation.migration.strategy.OnArrival;
import org.objectweb.proactive.extra.annotation.migration.strategy.OnDeparture;


public class ErrorReturnType {

    // ERR - return type must be void
    @OnDeparture
    public String leaving() {
        return "";
    }

    @OnArrival
    public int arriving() {
        return 42;
    }
}
