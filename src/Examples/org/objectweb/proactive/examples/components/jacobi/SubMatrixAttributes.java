package org.objectweb.proactive.examples.components.jacobi;

import org.objectweb.fractal.api.control.AttributeController;

public interface SubMatrixAttributes extends AttributeController {
	
	public void setDimensions(String dimensions);
	
	public String getDimensions();
	
	public void setCoordinates(String coordinates);
	
	public String getCoordinates();
	
	public void setGlobalDimensions(String dimensions);
	
	public String getGlobalDimensions();
	
	public void setNbIterations(String nbIterations);
	
	public String getNbIterations();
	
	

}
