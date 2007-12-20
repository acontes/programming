package org.objectweb.proactive.compi2.control.controller;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.compi2.MPIResult;
import org.objectweb.proactive.compi2.MPISpmd;
import org.objectweb.proactive.compi2.control.DGConstants;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;

public class DGControllerImpl extends AbstractProActiveController 
											implements DGController {
	
	private MPISpmd mpiSpmd;
	private List<DGController> clusterControllers;
	
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
	
	private MPIResult startMPI(){
		return this.mpiSpmd.startMPI();
	}

}
