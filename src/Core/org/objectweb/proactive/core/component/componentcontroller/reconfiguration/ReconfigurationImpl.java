package org.objectweb.proactive.core.component.componentcontroller.reconfiguration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.fscript.FScript;
import org.objectweb.fractal.fscript.FScriptEngine;
import org.objectweb.fractal.fscript.FScriptException;
import org.objectweb.fractal.fscript.InvalidScriptException;
import org.objectweb.fractal.fscript.ScriptLoader;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.extra.component.fscript.PAGCMScript;
import org.objectweb.proactive.extra.component.fscript.control.PAReconfigurationController;
import org.objectweb.proactive.extra.component.fscript.exceptions.ReconfigurationException;
import org.objectweb.proactive.extra.component.fscript.model.GCMComponentNode;
import org.objectweb.proactive.extra.component.fscript.model.GCMNodeFactory;

/**
 * Reconfiguration component embedding a PAGCMScript engine.
 * It's an adaptation of the the PAReconfigurationController added by Bastien.
 * 
 * @author cruz
 *
 */

public class ReconfigurationImpl extends AbstractPAComponentController implements PAReconfigurationController {

    /** The {@link ScriptLoader} used by the controller. */
    private transient ScriptLoader loader;
    /** The {@link FScriptEngine} used the controller. */
    private transient FScriptEngine engine;
	
    public ReconfigurationImpl() {
        super();
    }
    
    /**
     * Instantiates a new ProActive/GCM Script engine from the default ProActive/GCM Script ADL file and set it as
     * default engine for the controller.
     *
     * @throws ReconfigurationException If an error occurred during the instantiation.
     */
	public void setNewEngineFromADL() throws ReconfigurationException {
		System.out.println("Initializing with "+ PAGCMScript.PAGCM_SCRIPT_ADL);
		setNewEngineFromADL(PAGCMScript.PAGCM_SCRIPT_ADL);		
	}

	/**
     * Instantiates a new ProActive/GCM Script engine from an ADL file and set it as default engine for the
     * controller.
     *
     * @param adlFile The ADL file name containing the ProActive/GCM Script architecture to instantiate and to set
     * as default engine for the controller.
     * @throws ReconfigurationException If an error occurred during the instantiation.
     */
	public void setNewEngineFromADL(String adlFile)
	throws ReconfigurationException {
		PAComponent owner = hostComponent;
		try {
			String defaultFcProvider = null;
			try {
				defaultFcProvider = System.getProperty("fractal.provider");
			} catch (NullPointerException npe) {
				// No fractal.provider system property defined
			}
			System.setProperty("fractal.provider", "org.objectweb.fractal.julia.Julia");
			Component fscript = PAGCMScript.newEngineFromAdl(adlFile);
			loader = FScript.getScriptLoader(fscript);
			engine = FScript.getFScriptEngine(fscript);
			engine.setGlobalVariable("this", ((GCMNodeFactory) FScript.getNodeFactory(fscript))
					.createGCMComponentNode(owner));
			System.setProperty("fractal.provider", defaultFcProvider);
		} catch (Exception e) {
			throw new ReconfigurationException("Unable to set new engine for reconfiguration controller", e);
		}
	}
	
    /**
     * Checks if the {@link ScriptLoader} and the {@link FScriptEngine} have been initialized. If not, the
     * instantiation is done by using {@link #setNewEngineFromADL()}.
     *
     * @throws ReconfigurationException If an error occurred during the instantiation.
     */
    private void checkInitialized() throws ReconfigurationException {
        if ((loader == null) || (engine == null)) {
            setNewEngineFromADL();
        }
    }
    
    /**
     * Loads procedure definitions from a file containing source code, and make them available for later invocation
     * by name.
     *
     * @param fileName The name of the file containing the source code of the procedure definitions.
     * @return The names of all the procedures successfully loaded.
     * @throws ReconfigurationException If errors were detected in the procedure definitions.
     */
    public Set<String> load(String fileName) throws ReconfigurationException {
        checkInitialized();
        try {
            return loader.load(new FileReader(fileName));
        } catch (FileNotFoundException fnfe) {
            throw new ReconfigurationException("Unable to load procedure definitions", fnfe);
        } catch (InvalidScriptException ise) {
            throw new ReconfigurationException("Unable to load procedure definitions\n" + ise.getMessage());
        }
    }
    
    /**
     * Returns the names of all the currently defined global variables.
     *
     * @return The names of all the currently defined global variables.
     * @throws ReconfigurationException If an error occurred while getting global variable names.
     */
    public Set<String> getGlobals() throws ReconfigurationException {
        checkInitialized();
        return engine.getGlobals();
    }
    
    /**
     * Execute a code fragment: either an FPath expression or a single FScript statement.
     *
     * @param source The code fragment to execute.
     * @return The value of the code fragment, if successfully executed.
     * @throws ReconfigurationException If an error occurred during the execution of the code fragment.
     */
    public Object execute(String source) throws ReconfigurationException {
        checkInitialized();
        try {
        	System.out.println("Executing source: "+ source);
        	NameController nc = null;
        	try {
				nc = GCM.getNameController(hostComponent);
			} catch (NoSuchInterfaceException e) {
				e.printStackTrace();
			}
			System.out.println("Name controller found!");
			String name = nc.getFcName();
			System.out.println("Name is "+ name);
        	
        	Object result = engine.execute(source);
        	System.out.println("Result is of type: "+ result.getClass().getName());
        	HashSet hs = new HashSet();
        	System.out.println("Size: "+ hs.size());
        	for(Object i : hs) {
        		System.out.println("   ---> ("+ i.getClass().getName() + ") " + i);
        	}
        	Set<String> globals = getGlobals();
        	System.out.println("Size: "+ globals.size());
        	for(String s : globals) {
        		System.out.println("   ---> "+ s );
        	}
        	//Object result2 = engine.execute("name($this);");
        	//System.out.println("Result is of type: "+ result2.getClass().getName() + " and name is "+ result2 );
        	result = engine.execute("$this/interface;");
        	System.out.println("Result is of type: "+ result.getClass().getName());
        	
        	return new String("PAGCMScript executed!");
        } catch (FScriptException fse) {
            throw new ReconfigurationException("Unable to execute the procedure", fse);
        }
    }


}
