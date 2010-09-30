package org.objectweb.proactive.core.component.adl.luc.demo;

public class BasicExampleComponent implements ExampleComponent
{

	@Override
	public int printOk()
	{
		System.out.println("salut ca marche");
		System.err.println("salut ca marche");
		
		return 0;
	}

}
