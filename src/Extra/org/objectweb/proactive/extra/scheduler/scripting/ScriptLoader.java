package org.objectweb.proactive.extra.scheduler.scripting;

import java.io.FileReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;


/**
 * The script Loader
 *
 *
 * @author ProActive Team
 * @version 1.0, Jun 6, 2007
 * @since ProActive 3.2
 */
public class ScriptLoader {
    public static ScriptHandler createHandler(Node node)
        throws ActiveObjectCreationException, NodeException {
        return (ScriptHandler) ProActive.newActive(ScriptHandler.class.getCanonicalName(),
            null, node);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage : scriptloader script");
            System.exit(1);
        }
        String filename = args[0];
        String[] split = filename.split("\\.");
        if (split.length < 2) {
            System.err.println("Script must have an extension");
            System.exit(-2);
        }
        Reader reader = new FileReader(args[0]);
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByExtension(split[split.length - 1]);
        engine.eval(reader);
    }
}
