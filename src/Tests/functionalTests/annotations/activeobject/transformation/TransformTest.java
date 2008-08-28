package functionalTests.annotations.activeobject.transformation;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObjectKernel;
import org.objectweb.proactive.extra.annotation.transformation.AllAnnotationsTransformation;
import org.objectweb.proactive.extra.annotation.transformation.AnnotationTransformation;
import org.objectweb.proactive.extra.annotation.transformation.CodeGenerationException;
import org.objectweb.proactive.extra.annotation.transformation.MissingConfigurationParameterException;
import org.objectweb.proactive.extra.annotation.transformation.TransformationExecutor;
import org.objectweb.proactive.extra.annotation.transformation.TransformationKernel;

import com.sun.source.tree.AssertTree;

import recoder.ParserException;
import recoder.kit.Transformation;

import functionalTests.FunctionalTest;
import functionalTests.annotations.activeobject.Test;

public class TransformTest extends FunctionalTest {
	
	// log
	static final protected Logger _logger = Logger.getLogger("testsuite");
	
	public static final String INPUT_FILES_PATH;
	public static final String OUTPUT_FILES_PATH;
	public static final String TEST_FILES_RELPATH = "/src/Tests/functionalTests/annotations/activeobject/transformation/inputs/";
	public static final String TEST_FILES_PACKAGE = "functionalTests.annotations.activeobject.transformation.inputs.";
	public static final String OUTPUT_FILES_RELPATH = "/classes/";
	
	private TransformationExecutor _executor = null;
	
	static {
		INPUT_FILES_PATH = Test.PROACTIVE_HOME + TEST_FILES_RELPATH;
		OUTPUT_FILES_PATH = Test.PROACTIVE_HOME + OUTPUT_FILES_RELPATH;
	}

	@org.junit.Before
	public void init() {
		try {
			_executor = new TransformationExecutor( INPUT_FILES_PATH , OUTPUT_FILES_PATH);
		} catch (ParserException e) {
			_logger.error("Cannot get the compilation units from the path: " + System.getProperty("input.path"), e );
		} catch (MissingConfigurationParameterException e) {
			_logger.error( "Recoder was not initialized. Reason:" , e);
		}
	}


	@org.junit.Test
	public void pass(){
		
		if(_executor == null) {
			_logger.error("Transformation executor not created, nothing to execute.");
			return;
		}
		
		try {
			executeOnClasses(new String[] {
					TEST_FILES_PACKAGE + "ActiveObjectAccept",
					TEST_FILES_PACKAGE + "VirtualNodeAccept",
					TEST_FILES_PACKAGE + "VirtualActiveAccept",
				}
			);
			// must not throw exception
			Assert.assertTrue(true);
		} catch (CodeGenerationException e) {
			// must not throw exception
			_logger.debug(e.getMessage());
			Assert.assertTrue(false);
		}
	}
	
	@org.junit.Test( expected = CodeGenerationException.class )
	public void fail() throws CodeGenerationException {
		
		if(_executor == null) {
			_logger.error("Transformation executor not created, nothing to execute.");
			return;
		}
		
		executeOnClasses(new String[] {
				TEST_FILES_PACKAGE + "ActiveObjectReject",
				TEST_FILES_PACKAGE + "VirtualNodeReject",
				TEST_FILES_PACKAGE + "VirtualActiveReject",
			}
		);
	}
	
	private void executeOnClasses(String[] classNames) 
			throws CodeGenerationException {
		
		try {
			
			Transformation transform = new AllAnnotationsTransformation(
					_executor._sourceConfig ,
					classNames 
					);

			_executor.execute(transform);
		}
		catch (IOException ioe) {
			_logger.error("Error while trying to write the output of the preprocessor. Error details: " , ioe);
		}
	}
	
}
