package functionalTests.annotations.migrationsignal.inputs;

import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.extra.annotation.migration.signal.MigrationSignal;


public class AcceptInterClassCall {

    @MigrationSignal
    public void migrateTo1() throws MigrationException {
        // calling another method from another class, that migrates
        AnotherMigrateTo amt = new AnotherMigrateTo();
        amt.migrateTo();
    }

    @MigrationSignal
    public void migrateTo1() throws MigrationException {
        // a more sophisticated form of call
        new AnotherMigrateTo().migrateTo();
    }

}

//@ActiveObject
//should not be an active object!!
class AnotherMigrateTo {

    @MigrationSignal
    public void migrateTo() {
        PAMobileAgent.migrateTo(new Object());
    }
}
