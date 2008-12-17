package functionalTests.annotations.migrationsignal.inputs;

import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extensions.annotation.MigrationSignal;


@ActiveObject
public class AcceptInterClassCall {

    private AnotherMigrateTo ao = new AnotherMigrateTo();

    // call against a local variable
    //    @MigrationSignal
    //    public void migrateTo1() throws MigrationException {
    //        // calling another method from another class, that migrates
    //        AnotherMigrateTo amt = new AnotherMigrateTo();
    //        amt.migrateTo();
    //    }

    // local variable; type fully named
    // ERR not a migration signal; only for illustration purposes
    //    @MigrationSignal
    //    public void migrateTo2() throws ProActiveException {
    //        org.objectweb.proactive.core.node.Node node = null;
    //        node.getActiveObjects();
    //    }

    // local variable; type imported
    // ERR not a migration signal; only for illustration purposes
    //    @MigrationSignal
    //    public void migrateTo3() throws ProActiveException {
    //        Node node = null;
    //        node.getActiveObjects();
    //    }

    //    @MigrationSignal
    //    public void migrateTo2() throws MigrationException {
    //        // a more sophisticated form of call
    //        new AnotherMigrateTo().migrateTo();
    //    }

    // call against a field member
    @MigrationSignal
    public void migrateTo4() throws MigrationException {
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
