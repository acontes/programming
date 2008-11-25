package functionalTests.annotations.activeobject.inputs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.naming.directory.Attribute;

import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.migration.signal.MigrationSignal;


class SerialParam implements Serializable {
}

class Beta extends SerialParam {
}

@ActiveObject
public class ErrorMethodArgsNotSerializable {

    // Integer extends Number implements Serializable
    public void doSomething(Integer in) {
    }

    // String implements Serializable
    void doSomething2(String str) {
    }

    // interface javax.naming.directory.Attribute extends Serializable
    // SerialParam implements Serializable
    private void doSomething3(Attribute attr, SerialParam sp) {
    }

    // Beta extends SerialParam implements Serializable
    public void doSomethingg(Beta b) {
    }

    // FileInputStream NOT serializable
    public void readInputFromFile(FileInputStream in) throws IOException {
    }

    // should work - primitive types can be active object method arguments
    void setCounter(int counter, boolean onlyIfConditionMet) {
    }

    @MigrationSignal
    public void migrateTo(Node node) {
        PAMobileAgent.migrateTo(node);
    }

}
