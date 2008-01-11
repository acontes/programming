package org.objectweb.proactive.compi2.control.controller;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.compi2.MPIResult;
import org.objectweb.proactive.compi2.MPISpmd;
import org.objectweb.proactive.compi2.control.DGConstants;
import org.objectweb.proactive.compi2.control.DGNode;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;
import org.objectweb.proactive.ext.util.FutureList;
import org.objectweb.fractal.util.Fractal;

public class DGControllerImpl extends AbstractProActiveController 
											implements DGController {
	private MPISpmd mpiSpmd;
	private List<DGController> clusterControllers;
	private DGNode[] nodes;
	
	public DGControllerImpl(Component owner) {
		super(owner);
		clusterControllers = new ArrayList<DGController>();
	}

	@Override
	protected void setControllerItfType() {
		try {
			setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(DGConstants.DG_CONTROLLER, 
					DGController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE));
		} catch (InstantiationException e) {
			throw new ProActiveRuntimeException("cannot create controller " +
					this.getClass().getName());
		}
		
	}
	
	public void addDGController (int JobID, DGController dgController){
		clusterControllers.add(JobID, dgController);
	}
	
	public MPIResult startMPI(){	
		return this.startMPIProcess();	
	}
	
	public boolean PAMPIHandShake () {
		FutureList readyToStart = new FutureList();
		try {
			nodes = (DGNode[]) Fractal.getContentController(owner).getFcSubComponents();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		for (DGNode node : nodes){
			readyToStart.add(node.blockUntilReady());
		}
		
		readyToStart.waitAll();
		
		for (DGNode node : nodes){
			node.wakeUpThread();
		}
	
		return true;
	}
		
	
	private MPIResult startMPIProcess(){
		return this.mpiSpmd.startMPI();
	}

}
