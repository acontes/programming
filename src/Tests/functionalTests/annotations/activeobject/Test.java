package functionalTests.annotations.activeobject;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import junit.framework.Assert;

import org.objectweb.proactive.annotation.jsr269.ProActiveProcessor;
import org.objectweb.proactive.core.config.PAProperties;

import functionalTests.FunctionalTest;

/*
 * Tests for the MigrationSignal and ActiveObject annotations
 * proactive_home could be set; if not set, the code tries to guess
 */
public class Test extends FunctionalTest{
	
	public static final String INPUT_FILES_PATH;
	public static final String PROC_PATH;
	public static final String TEST_FILES_RELPATH = "/src/Tests/functionalTests/annotations/activeobject/inputs/";
	public static final String TEST_FILES_PACKAGE = "functionalTests.annotations.activeobject.inputs.";
	public static final String TEST_TO_PASS = "accept";
	public static final String TEST_TO_FAIL = "reject";
	
	static {
		String proactive_home;
		if(PAProperties.PA_HOME.isSet()){
			proactive_home = PAProperties.PA_HOME.getValue();
		}
		else {
			// guess the value
			String location = Test.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			proactive_home = getPAHomeFromClassPath(location); 
		}
		
		System.out.println("proactive_home is:" + proactive_home);
		INPUT_FILES_PATH = proactive_home + TEST_FILES_RELPATH;
		PROC_PATH = buildAnnotationProcessorPath(proactive_home);
		
	}
	
	private static final String getPAHomeFromClassPath(String location) {
		int pos = location.lastIndexOf(File.separator);
		String sb = location.substring(0, pos);
		pos = sb.lastIndexOf(File.separator);
		sb = sb.substring(0, pos);
		pos = sb.lastIndexOf(File.separator);
		sb = sb.substring(0, pos);
		
		return sb;
	}
	
	private static final String buildAnnotationProcessorPath(String proactive_home) {

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

	private JavaCompiler _compiler;
	private StandardJavaFileManager _fileManager;
	private DiagnosticCollector<JavaFileObject> _nonFatalErrors;
	
	@org.junit.Before
	public void initTest() {
		// get the compiler
		_compiler = ToolProvider.getSystemJavaCompiler();
		_nonFatalErrors = new DiagnosticCollector<JavaFileObject>();
		_fileManager = _compiler.
			getStandardFileManager(_nonFatalErrors, null, null); // go for the defaults
		
	}
	@org.junit.Test
	public void action() throws Exception {
		
		// checking conditions that should be seen as errors
		Assert.assertFalse(checkFile("ErrorNotLast", TEST_TO_FAIL));
		Assert.assertFalse(checkFile("ErrorNotLastBlock",TEST_TO_FAIL));
		Assert.assertFalse(checkFile("ErrorPrivate", TEST_TO_FAIL));
		Assert.assertFalse(checkFile("ErrorNotInActiveObject", TEST_TO_FAIL));
		
		// checking conditions that should be ok
		Assert.assertTrue(checkFile("AcceptSimple", TEST_TO_PASS));
		
	}
	
	// compile a single file
	// return true if no compilation errors, false else
	private boolean checkFile(String fileName , String expectedPrefix) {
		
		final String[] fileNames = new String[] {
			INPUT_FILES_PATH + expectedPrefix + "/" + fileName + ".java"
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
			ProActiveProcessor.class.getName()
		};
		
		String[] annotationsClassNames = {
				TEST_FILES_PACKAGE + expectedPrefix + "." + fileName
		};
		
		// create the compilation task
		CompilationTask compilationTask = _compiler.getTask( null, // where to write error messages 
				_fileManager, // the file manager 
				diagnosticListener, // where to receive the errors from compilation 
				Arrays.asList(compilerOptions),  // the compiler options 
				Arrays.asList(annotationsClassNames), // related to annotations but I don't know what is it, yet...
				compilationUnits);
		
		// call the compilation task
		System.out.println("Calling the compilation tasks on file:" + fileNames[0]);
		boolean compilationSuccesful = compilationTask.call();
		/*if( !compilationSuccesful  ) {
			// get the fatal errors
			for (Diagnostic<? extends JavaFileObject> diagnostic : 
				diagnosticListener.getDiagnostics()) {
					System.out.println("Error message is:" + diagnostic.getMessage(null)); 

			}
		}
		else {
			if(_nonFatalErrors.getDiagnostics().isEmpty()) {
				System.out.println("Compilation completed succesfully!");
			}
			else {
				for (Diagnostic<? extends JavaFileObject> diagnostic : 
					_nonFatalErrors.getDiagnostics()) {
						System.out.println("Non-fatal message message is:" + diagnostic.getMessage(null)); 
				}
			}
		}*/
		
		return compilationSuccesful && 
			diagnosticListener.getDiagnostics().isEmpty() &&
			_nonFatalErrors.getDiagnostics().isEmpty();
	}
	
	@org.junit.After
	public void endTest() throws Exception {

		// close the file manager
		_fileManager.close();
		
	}
	
}
