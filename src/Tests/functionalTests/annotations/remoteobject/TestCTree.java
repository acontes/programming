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
package functionalTests.annotations.remoteobject;

import junit.framework.Assert;
import functionalTests.annotations.CTreeTest;

public class TestCTree extends CTreeTest {

	@org.junit.Before
	public void init() throws Exception {
		envInit();
		inputFilesPathInit(this.getClass());
		testInit();
	}

	@org.junit.Test
	public void action() throws Exception {

		// TODO how to refactor? - checkFile() has different implementation for apt and ctree tests
		// APT
		// misplaced annotation
		Assert.assertEquals( ERROR , checkFile("MisplacedAnnotation") );

		// basic checks
		Assert.assertEquals( new Result(0,6) , checkFile("WarningGettersSetters"));
		Assert.assertEquals( ERROR , checkFile("ErrorFinalClass"));
		Assert.assertEquals( new Result(2,0), checkFile("ErrorFinalMethods"));
		Assert.assertEquals( new Result(2,0) , checkFile("ErrorFinalFields"));
		Assert.assertEquals( ERROR , checkFile("ErrorNoArgConstructor"));
		Assert.assertEquals( ERROR , checkFile("ErrorClassNotPublic"));
		Assert.assertEquals( ERROR , checkFile("PrivateEmptyConstructor"));
		Assert.assertEquals( OK , checkFile("EmptyConstructor"));
		Assert.assertEquals( new Result(2,0) , checkFile("ErrorConstructorArgsNotSerializable"));
		Assert.assertEquals( ERROR , checkFile("ErrorMethodArgsNotSerializable"));
		Assert.assertEquals( new Result(3,0), checkFile("ErrorMissingTypes"));

		// more complicated scenarios
		Assert.assertEquals( WARNING , checkFile("ErrorReturnTypes")); // because of getter/setter
		Assert.assertEquals( new Result(1,1) , checkFile("Reject"));
		Assert.assertEquals( OK , checkFile("CorrectedReject"));

		// CTREE - specific
		Assert.assertEquals( OK , checkFile("ErrorReturnsNull"));
		Assert.assertEquals( new Result(1,0) , checkFile("ErrorNonEmptyConstructor"));
		Assert.assertEquals( OK , checkFile("NoConstructor"));
	}

	@org.junit.After
	public void endTest() throws Exception {
		testCleanup();
	}

}
