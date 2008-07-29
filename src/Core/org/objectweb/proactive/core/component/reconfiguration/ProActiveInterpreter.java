package org.objectweb.proactive.core.component.reconfiguration;

import java.io.Reader;
import java.util.Map;

import org.objectweb.fractal.api.Component;

public interface ProActiveInterpreter {

	public void createVariable(String name, Component comp);
	
	public void executeScript(String statement);
	
	public void addVariables(Map<String, Object> varibles);
	
	public void applyProcedure(String procName, Object[] argsProc);
	
	public void loadScript(Reader input);

//    void setGlobalVariable(String name, Object value);

}
