package org.objectweb.proactive.compi2.control.controller;

import java.util.Map;

import org.objectweb.proactive.compi2.MPISpmd;

public interface DGFractiveController {
	public boolean createInnerComponents(MPISpmd mpiSpmd, Map<String, Object> context);
	public boolean orderInnerComponentsAndBindThem();
}
