package functionalTests.annotations.migrationsignal.inputs;

import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.migration.signal.MigrationSignal;


@ActiveObject
public class Fi {

    // OK - both branches have migrateTo last 
    @MigrationSignal
    public String migrateToRight(boolean onCondition) throws MigrationException {
        if (onCondition) {
            org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
            return ""; // the sweet C-style hakz
        } else {
            System.out.println("I refuze to migrate!");
            org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
            return "";
        }
    }

    // error - else branch fucked up
    @MigrationSignal
    public String migrateToWrong(boolean onCondition) throws MigrationException {
        if (onCondition) {
            org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
            return ""; // the sweet C-style hakz
        } else {
            org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
            System.out.println("I refuze to migrate!");
            return "";
        }
    }

    @MigrationSignal
    public String migrateToStSt(boolean onCondition) throws MigrationException {
        if (onCondition)
            migrateToRight(onCondition);
        else
            migrateToWrong(onCondition);
    }

    @MigrationSignal
    public String migrateToBlSt(boolean onCondition) throws MigrationException {
        if (onCondition) {
            System.out.println("Ich will migrate");
            migrateToRight(onCondition);
        } else
            migrateToWrong(onCondition);
        return "okay";
    }

    // wrong on one of the branches
    @MigrationSignal
    public String migrateToStBl(boolean onCondition) throws MigrationException {
        if (onCondition)
            migrateToRight(onCondition);
        else {
            System.out.println("Ich will migrate");
            migrateToWrong(onCondition);
            System.out.println("I'll fuck everything up!");
        }
    }

}
