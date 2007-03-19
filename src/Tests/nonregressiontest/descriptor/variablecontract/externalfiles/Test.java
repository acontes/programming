/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package nonregressiontest.descriptor.variablecontract.externalfiles;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.xml.VariableContract;

import testsuite.test.FunctionalTest;

public class Test extends FunctionalTest {
	static final long serialVersionUID = 1;

	private static String XML_LOCATION = Test.class.getResource(
			"/nonregressiontest/descriptor/variablecontract/externalfiles/Test.xml").getPath();
	ProActiveDescriptor pad;
	
	boolean bogusFromDescriptor, bogusFromProgram;
	
	public Test() {
		super("Variable Contract: External Files",
				"Tests conditions for external files");
	}

	@Override
	public boolean postConditions() throws Exception {

		VariableContract variableContract=pad.getVariableContract();
		
		//System.out.println(variableContract);
		
		return 
				variableContract.getValue("test_var0").equals("value0") &&
				variableContract.getValue("test_var1").equals("value1") &&
				variableContract.getValue("test_var2").equals("value2") &&
				variableContract.getValue("test_var3").equals("value3") &&
				variableContract.getValue("test_var4").equals("value4") &&
				variableContract.getValue("test_var5").equals("value5") &&
				variableContract.getValue("test_var6").equals("value6") &&
				variableContract.isClosed() &&
				variableContract.checkContract();
	}

	@Override
	public void initTest() throws Exception {
		bogusFromDescriptor=true;
		bogusFromProgram=true;
	}
	
	@Override
	public void endTest() throws Exception {
		
		if (pad != null) {
			pad.killall(false);
		}
	}

	@Override
	public void action() throws Exception {
		
		VariableContract variableContract= new VariableContract();
		
		/*
		//Setting from Program
		HashMap map = new HashMap();
		map.put("test_var1", "value1");
		variableContract.setVariableFromProgram(map, XMLPropertiesType.getType("ProgramDefaultVariable"));
				
		//Setting bogus from Program (this should fail)
		try{
			variableContract.setVariableFromProgram("test_empty", "", XMLPropertiesType.getType("ProgramDefaultVariable"));
		}catch (Exception e){
			bogusFromProgram=false;
		}
		
		//Setting from Program
		variableContract.setDescriptorVariable("test_var2", "value2a", XMLPropertiesType.getType("ProgramDefaultVariable"));
		//The following value should not be set, because Program is default and therefore has lower priority
		variableContract.setVariableFromProgram("test_var2", "value2b", XMLPropertiesType.getType("ProgramDefaultVariable"));
				
		//Setting bogus variable from Descriptor (this should fail)
		try{
			variableContract.setDescriptorVariable("bogus_from_descriptor", "", XMLPropertiesType.getType("ProgramDefaultVariable"));
		}catch (Exception e){
			bogusFromDescriptor=false;
		}
		
		//test_var3=value3
		*/
		
		pad = ProActive.getProactiveDescriptor(XML_LOCATION, variableContract);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Test test = new Test();
		try {
			System.out.println("InitTest");
			test.initTest();
			System.out.println("Action");
			test.action();
			System.out.println("postConditions");
			System.out.println("Result="+test.postConditions());
			System.out.println("endTest");
			test.endTest();
			System.out.println("The end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
