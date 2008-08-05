package functionalTests.annotations.activeobject.inputs.reject;

import java.rmi.AlreadyBoundException;

import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.annotation.migration.MigrationSignal;
import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;

@ActiveObject
public class ErrorNotLast {
	
	// error - not last statement
	@MigrationSignal
	public void migrateTo1() throws MigrationException, NodeException, AlreadyBoundException {
		int i=0;
		PAMobileAgent.migrateTo(NodeFactory.createNode(""));
		i++; // muhahaw
	}
	
	// a more subtle error - the return statement actually contains 
	// another method call - the call to the Integer constructor
	@MigrationSignal
	public Integer migrateTo2() throws MigrationException, NodeException, AlreadyBoundException {
		int i=0;
		org.objectweb.proactive.api.PAMobileAgent.migrateTo(NodeFactory.createNode(""));
		return new Integer(i);
	}
	
}
