package org.objectweb.proactive.ic2d.componentmonitoring.data;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class ComponentHolderModel extends AbstractData
{

	private static String ObjectNameString = "org.objectweb.proactive.ic2d.componentmonitoring:type=ComponentHolder";

	// -------------------------------------------
	// --- Constructor ---------------------------
	// -------------------------------------------

	private String name = "";

	public ComponentHolderModel() throws MalformedObjectNameException, NullPointerException
	{
		super(new ObjectName(ObjectNameString));
		name = ComponentHolderModel.class.getName();
	}

	@Override
	public void explore()
	{
		// TODO Auto-generated method stub
		findSubComponents();
	}

	@Override
	public String getKey()
	{
		// TODO Auto-generated method stub
		return getName();
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public <T extends AbstractData> T getParent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType()
	{
		// TODO Auto-generated method stub
		return "Components-holder";
	}

	private void findSubComponents()
	{

	}

}
