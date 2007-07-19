package org.objectweb.proactive.extra.scheduler.scripting;

import java.io.Reader;
import java.io.Serializable;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;


/**
 * A simple script to evaluate using java 6 scripting API.
 *
 * @author ProActive Team
 * @version 1.0, Jun 4, 2007
 * @since ProActive 3.2
 */
public abstract class Script<E> implements Serializable {

    /**
     * Execute the script and return the ScriptResult corresponding.
     *
     * @return
     */
    public ScriptResult<E> execute() {
        ScriptEngine engine = getEngine();
        if (engine == null) {
            return new ScriptResult<E>(new Exception("No Script Engine Found"));
        }
        try {
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            prepareBindings(bindings);
            engine.eval(getReader());
            return getResult(bindings);
        } catch (Throwable e) {
            return new ScriptResult<E>(new Exception(
                    "An exception occured while executing the script ", e));
        }
    }
    
    /** String identifying the script **/
    public abstract String getId();

    /** The reader used to read the script. */
    protected abstract Reader getReader();

    /** The Script Engine used to evaluate the script. */
    protected abstract ScriptEngine getEngine();

    protected abstract void prepareBindings(Bindings bindings);

    protected abstract ScriptResult<E> getResult(Bindings bindings);
}
