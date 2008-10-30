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
package functionalTests.annotations;

import java.io.File;

import org.objectweb.proactive.core.config.PAProperties;

import functionalTests.FunctionalTest;

/**
 * Root class grouping common functionality for all annotation tests
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public abstract class AnnotationTest extends FunctionalTest {
	
	// automatic environment configuration stuff
	protected String PROACTIVE_HOME;
	protected String PROC_PATH;

	protected void envInit() {
		if(PAProperties.PA_HOME.isSet()){
			PROACTIVE_HOME = PAProperties.PA_HOME.getValue();
		}
		else {
			// guess the value
			String location = AnnotationTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			PROACTIVE_HOME = getPAHomeFromClassPath(location); 
		}
		
		PROC_PATH = buildAnnotationProcessorPath(PROACTIVE_HOME);
		
	}
	
	private final String getPAHomeFromClassPath(String location) {
		int pos = location.lastIndexOf(File.separator);
		String sb = location.substring(0, pos);
		pos = sb.lastIndexOf(File.separator);
		sb = sb.substring(0, pos);
		pos = sb.lastIndexOf(File.separator);
		sb = sb.substring(0, pos);
		
		return sb;
	}
	
	private final String buildAnnotationProcessorPath(String proactive_home) {

		String proactive_classes = proactive_home + "/classes/";
		StringBuilder buildProcPath = new StringBuilder();
		String[] pathDirs = new String[] {
			"Core",
			"Extra",
			"Utils",
			"Extensions"
		};
		for (String pathDir : pathDirs) {
			buildProcPath.append( proactive_classes + pathDir + ":" );
		}
		
		return buildProcPath.toString();
	}
	
	// to be initialized by the subclasses
	protected String INPUT_FILES_PATH;
	protected String TEST_FILES_PACKAGE;
	
	// "guesses" the path to the test files. this method assumes(does not check!) that 
	// the structure of the tests is the same as described 
	// <a href="http://confluence.activeeon.com/display/PROG/Feature+Compile+time+annotations">here</a>
	protected void inputFilesPathInit(Class<? extends Object> testClass) {
		
		TEST_FILES_PACKAGE = testClass.getPackage().getName() + ".inputs.";
		String testFilesRelpath =  File.separator + "src" + File.separator + "Tests" + File.separator 
			+ TEST_FILES_PACKAGE.replace('.', File.separatorChar);
		
		INPUT_FILES_PATH = PROACTIVE_HOME + testFilesRelpath;
	}
	
	// initialization needed in order to perform the tests
	protected abstract void testInit() throws NoCompilerDetectedException;

	// how to execute a compilation process on a compilation unit
	protected abstract Result checkFile(String fileName) throws CompilationExecutionException;
	
	// test-specific cleanup code
	protected abstract void testCleanup();
	
	// the results of compilation execution
	public final class Result{
		public int errors;
		public int warnings;
		public Result() {
			errors = warnings = 0;
		}
		
		public Result(int e,int w) {
			errors = e; warnings = w;
		}
		
		@Override
		public boolean equals(Object obj) {
			Result rhs = (Result)obj;
			return errors == rhs.errors && warnings == rhs.warnings;
		}
		
		@Override
		public String toString() {
			return "errors:" + errors + ";warnings:" + warnings;
		}
	}
	
	protected final Result OK = new Result(0,0);
	protected final Result WARNING = new Result(0,1);
	protected final Result ERROR = new Result(1,0);
	
	// the errors of compilation execution
	public final class CompilationExecutionException extends Exception{
		
		public CompilationExecutionException(String str){ super(str); }
		public CompilationExecutionException(String str,Throwable e) { super(str,e); }
		
	}
	
	// if I don't find a compiler...
	public class NoCompilerDetectedException extends Exception {
		
		public NoCompilerDetectedException(String message) { super(message); }
		public NoCompilerDetectedException(String message,Throwable e) { super(message,e); }
		
	}
	
}
