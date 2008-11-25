package functionalTests.annotations.remoteobject.inputs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;

import javax.naming.directory.Attribute;
import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObject;


interface SerTest extends Serializable {
}

class Beta implements SerTest {
}

@RemoteObject
public class ErrorConstructorArgsNotSerializable {

    public ErrorConstructorArgsNotSerializable() {
        // TODO Auto-generated constructor stub
    }

    // String implements Serializable
    public ErrorConstructorArgsNotSerializable(String str) {

    }

    // Integer extends Number implements Serializable
    public ErrorConstructorArgsNotSerializable(Integer str) {

    }

    // ERR Matcher NOT Serializable
    public ErrorConstructorArgsNotSerializable(Matcher str, StringBuilder blah) {

    }

    // interface javax.naming.directory.Attribute extends Serializable
    public ErrorConstructorArgsNotSerializable(Attribute attr) {
    }

    // SerTest extends Serializable
    public ErrorConstructorArgsNotSerializable(SerTest test) {
    }

    // Beta implements SerTest extends Serializable
    public ErrorConstructorArgsNotSerializable(Beta test) {
    }

    // FileInputStream NOT serializable
    public ErrorConstructorArgsNotSerializable(FileInputStream in) throws IOException {
    }

}
