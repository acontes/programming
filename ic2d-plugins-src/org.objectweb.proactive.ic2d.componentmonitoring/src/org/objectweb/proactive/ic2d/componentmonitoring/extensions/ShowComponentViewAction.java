package org.objectweb.proactive.ic2d.componentmonitoring.extensions;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.HostObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IActionExtPoint;

public class ShowComponentViewAction extends Action implements IActionExtPoint{

	 public static final String SHOW_COMP_VIEW_ACTION = "Show components view";
	 public static final String SHOW_COMP_TOOL_TIP = "Show the component view of the monitored applications";
	 
	 private AbstractData target;
	 
	public ShowComponentViewAction()
	{
		 super.setId(SHOW_COMP_VIEW_ACTION);
		 //TODO: add image 
//		 super.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(
//	                org.objectweb.proactive.ic2d.chartit.Activator.getDefault().getBundle(), new Path(
//	                    "icons/graph.gif"), null)));
//		
	        super.setToolTipText(SHOW_COMP_TOOL_TIP);
	        super.setEnabled(false);
	}

	
	
	 public void setAbstractDataObject(final AbstractData object) {
	      //  if (object.getClass() != HostObject.class) 
	      //  	return;
	        	
	        //TODO: could use this to show the comp model only for one host
	         	//|| object.getClass() == HostObject.class)
	            //return;
	        
	        this.target = object;
	        super.setText("Show component view blah");
	        super.setEnabled(true);
	    }
	
	  public void setActiveSelect(final AbstractData ref) {
	        this.handleData(ref);
	    }
	  
	  private void handleData(final AbstractData abstractData) {
	        if (abstractData == null)
	                return;
	   
	        WorldObject wo = target.getWorldObject();
	        
	        System.out.println("ShowComponentViewAction.handleData() -> will show comp view for " + target.toString());
	  }
	

	  
	  
	
}
