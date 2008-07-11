package org.objectweb.proactive.core.jmx.mbean;

import javax.management.ObjectName;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.body.ComponentBody;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.component.identity.ProActiveComponentImpl;

public class ComponentWrapper extends BodyWrapper implements ComponentWrapperMBean {

	ComponentBody cbody;
	boolean iscomponent;

	/**
	 * Empty constructor required by JMX
	 */
	public ComponentWrapper() {
		/* Empty Constructor required by JMX */
		super();
	}

	/**
	 * Creates a new BodyWrapper MBean, representing an active object.
	 * 
	 * @param oname
	 *            The JMX name of this wrapper
	 * @param body
	 *            The wrapped active object's body
	 */
	public ComponentWrapper(ObjectName oname, AbstractBody body) {
		super(oname, body);
		iscomponent = false;
		if (body instanceof ComponentBody) {
			this.cbody = (ComponentBody) body;
			if (this.cbody.isComponent())
				this.iscomponent = true;
		}
	}

	public String getComponentName() {
		return super.getName();
	}

	public boolean isComponent() {
		return this.iscomponent;
	}

	public ProActiveComponent[] getSubComponents() {
		if (!isComponent())
			return null;

		ProActiveComponentImpl componentImpl = this.cbody.getProActiveComponentImpl();
		try
		{
			ContentController CC = (ContentController) componentImpl.getFcInterface(Constants.CONTENT_CONTROLLER);
			Component[] components = CC.getFcSubComponents();
			if(components == null)
				return null;
			ProActiveComponent[] subs = new ProActiveComponent[components.length];
			int index = 0;
			for(Component c:components)
			{
				ProActiveComponent pc = (ProActiveComponent)c;
				
				subs[index++] = pc;
			}
			return subs;
		}
		catch(NoSuchInterfaceException e)
		{
			return null;
		}
		
	}

	public UniqueID getID() {
		return super.getID();
	}
	
	public ObjectName getObjectName()
	{
		return super.getObjectName();
	}
	
	

}
