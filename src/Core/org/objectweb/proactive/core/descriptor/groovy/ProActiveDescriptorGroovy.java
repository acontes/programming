package org.objectweb.proactive.core.descriptor.groovy;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.objectweb.proactive.core.descriptor.xml.ProActiveDescriptorConstants;
import groovy.lang.GroovyClassLoader;

public class ProActiveDescriptorGroovy implements ProActiveDescriptorConstants {

	public void test() throws Exception {
		String fileName = "Tester.groovy";
		GroovyClassLoader gcl = new GroovyClassLoader();
		Class clazz = gcl.parseClass(new File(fileName));
		Object aScript = clazz.newInstance();
	}
	
}
