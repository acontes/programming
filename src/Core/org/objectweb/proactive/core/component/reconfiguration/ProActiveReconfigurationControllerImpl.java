package org.objectweb.proactive.core.component.reconfiguration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;

import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;


public class ProActiveReconfigurationControllerImpl extends AbstractProActiveController 
implements ProActiveReconfigurationController{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProActiveInterpreter interpreter;

	public ProActiveReconfigurationControllerImpl(Component owner) {
		super(owner);
	}


	@Override
	protected void setControllerItfType() {
		// TODO Auto-generated method stub
		try {
			setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(
					ProActiveReconfigurationController.RECONFIGURATION_CONTROLLER_NAME,
					ProActiveReconfigurationController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
					TypeFactory.SINGLE));
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new ProActiveRuntimeException("cannot create controller type for controller " +
					this.getClass().getName());
		}
	}


	public ProActiveInterpreter getInterpreter() {
		// TODO Auto-generated method stub
		return interpreter;
	}


	public void setInterpreter(ProActiveInterpreter interpreter) {
		// TODO Auto-generated method stub
		this.interpreter = interpreter;
	}


	public void setInterpreter(String interpreterClassName) {

		// TODO Auto-generated method stub
		Class<?> interpreterClass;			
		try {
			interpreterClass = Class.forName(interpreterClassName);
			try {
				this.interpreter = (ProActiveInterpreter) interpreterClass.newInstance();
				//this.interpreter.createVariable("owner", owner);
				try {
					String name = Fractal.getNameController(owner).getFcName();
					System.out.println("[debug] interpreter global variable name $"+name);
					this.interpreter.createVariable(name, owner);	
				} catch (NoSuchInterfaceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (java.lang.InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AssertionError("Class not found: "+interpreterClassName);
		}


	}

	public void applyProcedure(String procName, Object[] argsProc){
		this.interpreter.applyProcedure(procName, argsProc);
	}


	public void addVariables(Map<String, Object> varibles) {
		// TODO Auto-generated method stub
		this.interpreter.addVariables(varibles);
	}


	public void createVariable(String name, Component comp) {
		// TODO Auto-generated method stub
		this.interpreter.createVariable(name, comp);
	}


	public void executeScript(String statement) {
		// TODO Auto-generated method stub
		this.interpreter.executeScript(statement);
	}


	public void loadScript(String input) {
		// TODO Auto-generated method stub
		try {
			this.interpreter.loadScript(new FileReader(input));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
