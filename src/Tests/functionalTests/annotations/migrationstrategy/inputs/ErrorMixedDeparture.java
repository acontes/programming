package functionalTests.annotations.migrationstrategy.inputs;

import org.objectweb.proactive.extensions.annotation.migration.strategy.OnDeparture;


public class ErrorMixedDeparture {

    // 2xERR :
    // 1) has parameters
    // 2) returns something
    @OnDeparture
    String leaving(String where) {
        return where + " for Christmas";
    }

}
