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
package functionalTests.annotations.ctree;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

import org.objectweb.proactive.extra.annotation.ProActiveProcessorCTree;

import functionalTests.annotations.AnnotationTest;

/**
 * Root class for tests for annotation implemented using JDK 1.6 - the Compiler Tree API
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public abstract class CTreeTest extends AnnotationTest {
	
	private JavaCompiler _compiler;
	private NoClassOutputFileManager _fileManager;
	private DiagnosticCollector<JavaFileObject> _nonFatalErrors;
	
	/* (non-Javadoc)
	 * @see functionalTests.annotations.AnnotationTest#testInit()
	 */
	@Override
	protected void testInit() throws NoCompilerDetectedException {
		// get the compiler
		_compiler = ToolProvider.getSystemJavaCompiler();
		if(_compiler==null) {
			logger.error("Cannot detect the system Java compiler. Check for your JDK settings(btw, you DO have a JDK installed, right?)");
			// this test can no longer continue...
			throw new NoCompilerDetectedException("The annotations test will not be run, because a Java compiler was not detected.");
		}
		_nonFatalErrors = new DiagnosticCollector<JavaFileObject>();
		// get the file manager
		StandardJavaFileManager stdFileManager = _compiler.
			getStandardFileManager(_nonFatalErrors, null, null); // go for the defaults
		_fileManager = new NoClassOutputFileManager(stdFileManager);

	}
	
	@Override
	protected void testCleanup() {
		// close the file manager
		try {
			_fileManager.close();
		} catch (IOException e) {
			// 
		}
	}
	
	/* (non-Javadoc)
	 * @see functionalTests.annotations.AnnotationTest#checkFile(java.lang.String)
	 */
	@Override
	protected Result checkFile(String fileName) throws CompilationExecutionException {
		final String[] fileNames = new String[] {
				INPUT_FILES_PATH + File.separator +  fileName + ".java",
		};

		// get the compilation unit
		Iterable<? extends JavaFileObject> compilationUnits =		
			_fileManager.getJavaFileObjects(fileNames);

		// setup diagnostic collector 
		DiagnosticCollector<JavaFileObject> diagnosticListener = 
			new DiagnosticCollector<JavaFileObject>();

		// compiler options
		// the arguments of the options come after the option
		String[] compilerOptions = {
				"-proc:only",
				"-processorpath",
				PROC_PATH,
				"-processor",
				ProActiveProcessorCTree.class.getName()
		};

		String[] annotationsClassNames = {
				TEST_FILES_PACKAGE + fileName
		};

		// create the compilation task
		CompilationTask compilationTask = _compiler.getTask( null, // where to write error messages 
				_fileManager, // the file manager 
				diagnosticListener, // where to receive the errors from compilation 
				Arrays.asList(compilerOptions),  // the compiler options 
				Arrays.asList(annotationsClassNames), // classes on which to perform annotation processing
				compilationUnits);

		// call the compilation task
		boolean compilationSuccesful = compilationTask.call();

		if(compilationSuccesful) {
			return OK;
		}
		else { 
			// TODO problem with reporting warnings for the Compiler Tree API
			//				for( Diagnostic<? extends JavaFileObject> diag : diagnosticListener.getDiagnostics()) {
			//					System.out.println("Here is an error:" + diag.toString());
			//				}
			return new Result(diagnosticListener.getDiagnostics().size(),0);
		}
	}

	// a JavaFileManager used in order to suppress any .class file generation in the compilation phase
	final class NoClassOutputFileManager extends ForwardingJavaFileManager<JavaFileManager> {
		
		private final BlackHoleFileObject _blackHoleFileObject;
		private final JavaFileManager _underlyingFileManager;

		protected NoClassOutputFileManager(JavaFileManager fileManager) {
			super(fileManager);
			_underlyingFileManager = fileManager;
			_blackHoleFileObject = new BlackHoleFileObject();
		}
		
		public Iterable<? extends JavaFileObject> getJavaFileObjects(
				String... fileNames) {
			if ( !(_underlyingFileManager instanceof StandardJavaFileManager)) 
				return null;
			return ((StandardJavaFileManager)_underlyingFileManager).getJavaFileObjects(fileNames);
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location,
				String className, Kind kind, FileObject sibling)
				throws IOException {
			
			if ( kind == JavaFileObject.Kind.CLASS && isClassLocation(location) ) {
				return _blackHoleFileObject;
			}
			
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}

		private boolean isClassLocation(Location location) {
			
			if(!location.isOutputLocation())
				return false;
			
			if( location instanceof StandardLocation && ((StandardLocation)location) == StandardLocation.CLASS_OUTPUT )
				return true;
			
			return false;
		}
		
		// a FileObject that discards all output it receives
		final class BlackHoleFileObject extends SimpleJavaFileObject {
			
			protected BlackHoleFileObject() {
				this(URI.create("blabla"), JavaFileObject.Kind.CLASS);
			}

			protected BlackHoleFileObject(URI uri, Kind kind) {
				super(uri, kind);
			}
			
			@Override
			public OutputStream openOutputStream() throws IOException {
				return new BlackHoleOutputStream();
			}
			
			// an OutputStream that discards all output it receives
			final class BlackHoleOutputStream extends OutputStream {

				@Override
				public void write(int b) throws IOException {
					// black hole - do nothing!
					return;
				}
				
			}
			
		}
		
	}

}