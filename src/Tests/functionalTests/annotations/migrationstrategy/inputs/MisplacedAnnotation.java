package functionalTests.annotations.migrationstrategy.inputs;

import org.objectweb.proactive.extensions.annotation.migration.strategy.OnArrival;
import org.objectweb.proactive.extensions.annotation.migration.strategy.OnDeparture;


@OnDeparture
class MisplacedOnDeparture {
}

@OnArrival
class MisplacedOnArrival {

}

public class MisplacedAnnotation {

}
