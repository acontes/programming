package org.objectweb.proactive.extensions.jmx.jboss.tests;

import org.jboss.logging.*;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

public class DeployerTest<TestClass> {

	private Logger _jbossLogger;
	
	private String _virtualNodeName;
	private String _xmlDescriptorName;
	private String _testClassName;
	
	private VirtualNode _virtualNode;
	private TestClass[] _testActiveObjects;

	public DeployerTest(String nodeName, String descriptorName , String testClassName, 
			Logger jbossLogger) {
		_virtualNodeName = nodeName;
		_xmlDescriptorName = descriptorName;
		_jbossLogger = jbossLogger;
		_testClassName = testClassName;
	}
	
	// to be called in startService
	public VirtualNode deploy() throws ProActiveException {
		
		_jbossLogger.debug("Creating the virtual node " + _virtualNodeName + "...");

		ProActiveDescriptor descriptor =
			PADeployment.getProactiveDescriptor(_xmlDescriptorName);
		descriptor.activateMapping(_virtualNodeName); 
		_virtualNode =	descriptor.getVirtualNode(_virtualNodeName);

		_jbossLogger.debug("Virtual node created!");
		
		return _virtualNode;
		
	}
	
	/**
	 * starts active objects of the type TestClass on all the nodes 
	 * in the virtual node _virtualNode
	 * <p>to be called in startService</p>
	 * <p>testClassName needed because of type erasure</p>
	 */
	public void startActiveObjects() {
		
		_jbossLogger.debug("Creating active object on the nodes " +
				"of the virtual node " + _virtualNodeName + "...");

		try { 
			_testActiveObjects= (TestClass[])PAActiveObject.newActiveInParallel(
				_testClassName, new Object[][] {}, _virtualNode.getNodes());

		_jbossLogger.debug("Active objects created!");
		
		} 
		catch(ClassNotFoundException e) { 
			_jbossLogger.error(e.getMessage(), e); 
		} catch (NodeException e) {
			_jbossLogger.error( e.getMessage() , e );
		}
						
	}
	

	/**
	 * starts an active object of the type TestClass on the node given as a parameter
	 * <p>to be called in startService</p>
	 * @param node - the node onto which to start the active object
	 * 			  <p>if null , will start on a default node, in the local JVM</p>
	 * @return a reference to the created active object
	 */
	public TestClass startActiveObject(Node node) {
		_jbossLogger.debug("Create AO...");

		TestClass test;
		try {
			if(node == null)
				test = (TestClass) PAActiveObject.newActive(_testClassName, null);
			else 
				test = (TestClass)PAActiveObject.newActive(_testClassName, null, node);
		}
		catch (ActiveObjectCreationException e) {
			_jbossLogger.error( e.getMessage() , e );
			return null;
		} catch (NodeException e) {
			_jbossLogger.error( e.getMessage() , e );
			return null;
		}

		_jbossLogger.debug("Done!");
		
		return test;
	}
	
	/**
	 * stops an already created active object
	 * @param testActiveObject the reference to the created active object
	 */
	public void stopActiveObject(TestClass testActiveObject){
		PAActiveObject.terminateActiveObject(testActiveObject, true);
	}
	
	// to be called in stopService
	public void stopActiveObjects() {
		_jbossLogger.debug("Terminating the created active objects..."); 
		for (TestClass test : _testActiveObjects) { 
			PAActiveObject.terminateActiveObject( test,
						true); 
		} 
		_jbossLogger.debug("Done this shit!"); 
	}
	
	// to be called in stopService
	public void undeploy() {
		_jbossLogger.debug("Stopping the virtual node " + _virtualNodeName);
		
		_virtualNode.killAll(true);
		
		_jbossLogger.debug("Virtual node(s) stopped!");
	}
	
}
