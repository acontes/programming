package org.objectweb.proactive.compi2.control.controller;


import java.util.Hashtable;
import java.util.Map;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.compi2.MPIResult;
import org.objectweb.proactive.compi2.MPISpmd;
import org.objectweb.proactive.compi2.control.DGConstants;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;
import org.objectweb.proactive.core.component.controller.ProActiveContentController;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;


public class DGFractiveControllerImpl extends AbstractProActiveController implements DGFractiveController{
	//protected Map <Integer, Component> fcSubComponents;
	private ProActiveContentController ownerContentController;
	private MPISpmd mpiSpmd;
		

	public DGFractiveControllerImpl(Component owner) {
		super(owner);
		
		try {
			ownerContentController = Fractive.getProActiveContentController(owner);
			//Component[] subcomponents =  ownerContentController.getFcSubComponents();
		} catch (NoSuchInterfaceException e) {
			System.err.println("error: trying to get a content controller on a non-composite component");
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void setControllerItfType() {
		try {
			setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(DGConstants.DG_FRACTIVE_CONTROLLER, 
					DGFractiveController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE));
		} catch (InstantiationException e) {
			throw new ProActiveRuntimeException("cannot create controller " +
					this.getClass().getName());
		}
		
	}
	
	
	public boolean createInnerComponents(MPISpmd mpiSpmd){
		this.mpiSpmd = mpiSpmd;
		try {
			Node allNodes[] = mpiSpmd.getVn().getNodes();
			//deploy components on each node
			// if owner is "running", stop it
			//include them as subcomponents of the owner
			//do all the binding between primitives and owner 
		} catch (NodeException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
