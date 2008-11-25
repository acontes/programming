package functionalTests.annotations.migrationsignal.inputs;

import org.objectweb.proactive.api.PAMobileAgent;
import static org.objectweb.proactive.api.PAMobileAgent.migrateTo;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.migration.signal.MigrationSignal;


@ActiveObject
public class AcceptSimple {
    // OK
    @MigrationSignal
    public void migrateTo1() throws MigrationException {
        PAMobileAgent.migrateTo(new Object());
    }

    @MigrationSignal
    public void migrateTo2() throws MigrationException {
        org.objectweb.proactive.api.PAMobileAgent.migrateTo(new Object());
    }

    @MigrationSignal
    public int migrateTo3() throws MigrationException {
        PAMobileAgent.migrateTo(new Object());
        return 0;
    }

    @MigrationSignal
    public void migrateTo4(String place) throws MigrationException {
        migrateTo(place);
    }

}
