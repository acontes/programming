package org.objectweb.proactive.compi2.control.controller;


import java.util.Hashtable;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.compi2.MPISpmd;
import org.objectweb.proactive.compi2.control.DGConstants;
import org.objectweb.proactive.compi2.control.ProActiveMPI;
import org.objectweb.proactive.compi2.control.DGNodeImpl;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.adl.FactoryFactory;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;
import org.objectweb.proactive.core.component.controller.ProActiveContentController;
import org.objectweb.proactive.core.component.factory.ProActiveGenericFactory;
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
	
	
	//deploy components on each node
	// if owner is "running", stop it
	//include them as subcomponents of the owner
	//do all the binding between primitives and owner
	public boolean createInnerComponents(MPISpmd mpiSpmd, Map<String, Object> context){
		this.mpiSpmd = mpiSpmd;
		boolean wasStopped = false; 
		
		try {
			Node allNodes[] = mpiSpmd.getVn().getNodes();
			Component nodes[] = new Component[allNodes.length];

	    	try {
	    		Component boot = Fractal.getBootstrapComponent();
	    		ProActiveGenericFactory cf = Fractive.getGenericFactory(boot);
	    		Factory f = FactoryFactory.getFactory();
	    		ComponentType nodeType = (ComponentType) f.newComponentType(DGConstants.DG_NODE_ADL, context);
	    		ControllerDescription nodeController = new ControllerDescription("node", Constants.PRIMITIVE, 
	    				ProActiveMPI.class.getClass().getResource(DGConstants.DG_NODE_CONTROLLER_CONFIG).getPath());
	    		
	    		//get ID from the attributeController
	    		ContentDescription nodeContent = new ContentDescription(DGNodeImpl.class.getName(), new Object[]{DGConstants.DEFAULT_LIBRARY_NAME, new Integer(1)});
	    	
	    		if(Fractal.getLifeCycleController(owner).getFcState().equals(LifeCycleController.STARTED)){
	    			Fractal.getLifeCycleController(owner).stopFc();
	    			wasStopped = true;
	    		} 
	    			
	    		for (int i = 0; i< nodes.length; i++){
	    			nodes[i] = cf.newFcInstance(nodeType, nodeController, nodeContent, allNodes[i]);
	    		}
	    		
	    		//TODO: must wait and get rank to add subcomponents ordered by rank
	    		for (int i = 0; i< nodes.length; i++){
	    			ownerContentController.addFcSubComponent(nodes[i]);
	    			Fractal.getBindingController(owner).bindFc("outMxNServerItf", nodes[i].getFcInterface("serverItf"));
	    			Fractal.getBindingController(nodes[i]).bindFc("clientItf", owner.getFcInterface("inMxNClientItf"));
	    		}
	    		
	    		if(wasStopped){
	    			Fractal.getLifeCycleController(owner).startFc();
	    		}

	    	} catch (InstantiationException e) {
	    		e.printStackTrace();
	    	} catch (NoSuchInterfaceException e) {
	    		e.printStackTrace();
	    	} catch (ADLException e) {
	    		e.printStackTrace();
	    	} catch (IllegalLifeCycleException e) {
				e.printStackTrace();
			} catch (IllegalContentException e) {
				e.printStackTrace();
			} catch (IllegalBindingException e) {
				e.printStackTrace();
			} 
		} catch (NodeException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
