package functionalTests.annotations.migrationsignal.inputs;

import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.migration.signal.MigrationSignal;


@ActiveObject
public class AcceptInterClassCall {

    private AnotherMigrateTo ao = new AnotherMigrateTo();

    // call against a local variable
    @MigrationSignal
    public void migrateTo1() throws MigrationException {
        // calling another method from another class, that migrates
        AnotherMigrateTo amt = new AnotherMigrateTo();
        amt.migrateTo();
    }

    //    @MigrationSignal
    //    public void migrateTo2() throws MigrationException {
    //        // a more sophisticated form of call
    //        new AnotherMigrateTo().migrateTo();
    //    }

    // call against a field member
    @MigrationSignal
    public void migrateTo3() throws MigrationException {
        // a more sophisticated form of call
        ao.migrateTo();
    }

}

class AnotherMigrateTo {

    @MigrationSignal
    public void migrateTo() throws MigrationException {
        PAMobileAgent.migrateTo(new Object());
    }
}
