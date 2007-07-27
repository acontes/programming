package org.objectweb.proactive.extra.gcmdeployment.process.bridge;


/**
 * A dummy bridge to be used in unit tests
 */
public class BridgeDummy extends AbstractBridge {
    String command;

    public BridgeDummy(String command) {
        this.command = command;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String internalBuildCommand() {
        return command;
    }
}
