package functionalTests.annotations.migrationsignal.inputs;

import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.migration.signal.MigrationSignal;

@ActiveObject
public class Fi {
	
	// OK - both branches have migrateTo last 
	@MigrationSignal
	public String migrateToRight(boolean onCondition) throws MigrationException{
		if(onCondition) {
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
	public String migrateToWrong(boolean onCondition) throws MigrationException{
		if(onCondition) {
			org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
			return ""; // the sweet C-style hakz
		} else {
			org.objectweb.proactive.api.PAMobileAgent.migrateTo("");
			System.out.println("I refuze to migrate!");
			return "";
		}
	}
}
