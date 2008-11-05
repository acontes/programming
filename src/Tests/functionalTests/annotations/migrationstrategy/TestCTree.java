/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package functionalTests.annotations.migrationstrategy;

import junit.framework.Assert;
import functionalTests.annotations.CTreeTest;
import functionalTests.annotations.AnnotationTest.Result;

/**
 * Test for the @OnDeparture/@OnArrival annotations; ctree version
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class TestCTree extends CTreeTest {

	@org.junit.Before
	public void init() throws Exception {
		envInit();
		inputFilesPathInit(this.getClass());
		testInit();
	}
	
	@org.junit.Test
	public void action() throws Exception {
		
		// misplaced annotation
		Assert.assertEquals( ERROR , checkFile("MisplacedAnnotation"));
		
		// "simple" tests
		Assert.assertEquals( OK , checkFile("SimpleAccept") );
		Assert.assertEquals( ERROR , checkFile("ErrorReturnType") );
		Assert.assertEquals( new Result(2,0) , checkFile("ErrorParameters") );
		
		// "complex" tests
		Assert.assertEquals( new Result(2,0) , checkFile("ErrorMixed") );
	}
	
	@org.junit.After
	public void endTest() throws Exception {
		testCleanup();
	}
	
}