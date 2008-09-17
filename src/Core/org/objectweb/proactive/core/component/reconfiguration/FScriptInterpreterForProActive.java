package org.objectweb.proactive.core.component.reconfiguration;

import java.io.Reader;
import java.util.Map;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fscript.FScriptException;
import org.objectweb.fractal.fscript.FScriptInterpreter;
import org.objectweb.fractal.fscript.nodes.Node;
import org.objectweb.fractal.fscript.statements.Statement;



public class FScriptInterpreterForProActive extends FScriptInterpreter 
										implements ProActiveInterpreter{

	public FScriptInterpreterForProActive() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void createVariable(String name, Component comp){		
		 Node node = this.createComponentNode(comp);
		 this.getEnvironment().setVariable(name, node);
	}
	
	public void executeScript(String statement){		
		try {
//			System.out.println("debug: exist var ? "+this.getEnvironment().getVariable("owner"));
			Statement stmt = this.parseStatement(statement);
			this.execute(stmt, null);
		} catch (FScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public void loadScript(Reader input) {
		// TODO Auto-generated method stub
		try {
			this.loadDefinitions(input);
		} catch (FScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	public void addVariables(Map<String, Object> varibles) {
		// TODO Auto-generated method stub
		//this.getEnvironment().getVariables().putAll(varibles);
	}

	public void applyProcedure(String procName, Object[] argsProc) {
		// TODO Auto-generated method stub
		try {
			this.apply(procName, argsProc);
		} catch (FScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
