package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ModelRecorder;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;

public class TestView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		String firstModel = ModelRecorder.getInstance().getNames().iterator().next();
		WorldObject wo= ModelRecorder.getInstance().getModel(firstModel);
	
		if (wo!=null)
		{
			System.out.println("ok: "+wo.toString());
		}
		else
			System.out.println("tzeapa... ");
		
	}

	@Override
	public void setFocus() {
		
		
	}

}
