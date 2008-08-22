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
	
	public static final String RECODER_HOME;
	public static final String RECODER_LIBS_RELPATH = "/lib/recoder/";
	public static final String RECODER_LIBS;
	public static final String JAVA_HOME;
	public static final String JAVA_RT_RELPATH = "/jre/lib/rt.jar";
	public static final String JAVA_RT;
	
	public static final String PROACTIVE_LIBS_RELPATH = "/lib/";
	// sed rulz
	public static final String[] PROACTIVE_LIBS = {
		"activation.jar",
		"ajo.jar",
		"asm-2.2.1.jar",
		"axis.jar",
		"batik-awt-util.jar",
		"batik-dom.jar",
		"batik-svggen.jar",
		"batik-util.jar",
		"batik-xml.jar",
		"bcel-5.1-fixes-1.0.jar",
		"bcel-5.1.jar",
		"bouncycastle.jar",
		"bundlerepository.jar",
		"classad.jar",
		"cog-jglobus-1.2.jar",
		"cog-jglobus.jar",
		"cog-ogce.jar",
		"colobus-0.1.jar",
		"colt.jar",
		"commons-cli-1.0.jar",
		"commons-collections-3.2.jar",
		"commons-discovery.jar",
		"commons-logging.jar",
		"concurrent.jar",
		"cryptix32.jar",
		"cryptix-asn1.jar",
		"cryptix.jar",
		"dtdparser.jar",
		"fractal-adl.jar",
		"fractal.jar",
		"glite-wms-jdlj.jar",
		"http.jar",
		"ibis-1.4.jar",
		"ibis-connect-1.0.jar",
		"ibis-util-1.0.jar",
		"javasci.jar",
		"javassist.jar",
		"jaxrpc.jar",
		"jcommon-1.0.6.jar",
		"jdom.jar",
		"jfreechart-1.0.2.jar",
		"jh.jar",
		"julia-asm.jar",
		"julia-mixins.jar",
		"julia-runtime.jar",
		"jung-1.7.4.jar",
		"log4j.jar",
		"mail.jar",
		"moduleloader.jar",
		"njs_client.jar",
		"oscar.jar",
		"osgi.jar",
		"ow_deployment_scheduling.jar",
		"ptolemy.jar",
		"puretls.jar",
		"saaj-api.jar",
		"saxon8-dom.jar",
		"saxon8.jar",
		"scriptPlugin.jar",
		"servicebinder.jar",
		"servlet-api.jar",
		"servlet.jar",
		"shellgui.jar",
		"shell.jar",
		"shellplugin.jar",
		"shelltui.jar",
		"simple.jar",
		"soap.jar",
		"ssj.jar",
		"sunxacml.jar",
		"tablelayout.jar",
		"trilead-ssh2.jar",
		"winp-1.5.jar",
		"wsdl4j.jar",
		"xercesImpl.jar",
		"xml-apis.jar",
	};
	
	static {
		INPUT_FILES_PATH = Test.PROACTIVE_HOME + TEST_FILES_RELPATH;
		OUTPUT_FILES_PATH = Test.PROACTIVE_HOME + OUTPUT_FILES_RELPATH;
		
		RECODER_HOME = Test.PROACTIVE_HOME + RECODER_LIBS_RELPATH;
		RECODER_LIBS = RECODER_HOME + "recoder.jar" + File.pathSeparator + 
			RECODER_HOME + "bsh-1.2b2.jar";
		
		JAVA_HOME = System.getenv("JAVA_HOME");
		JAVA_RT = JAVA_HOME + JAVA_RT_RELPATH;
	}

	@org.junit.Before
	public void init() {
		try {
			recoderConfig();
			transformationInit();
		} catch (ParserException e) {
			_logger.error("Cannot get the compilation units from the path: " + System.getProperty("input.path"), e );
		}
	}

	private void recoderConfig() {
		// recoder config 
		// output.path is where the generated files will be written
		System.setProperty("output.path", OUTPUT_FILES_PATH);
		StringBuilder proactiveLibs = new StringBuilder(PROACTIVE_LIBS.length*20);
		for(String lib : PROACTIVE_LIBS){
			proactiveLibs.append(Test.PROACTIVE_HOME + PROACTIVE_LIBS_RELPATH + lib + File.pathSeparator);
		}
		// input.path is where recoder reads info about the processed compilation units
		String inputPath = INPUT_FILES_PATH + File.pathSeparator + JAVA_RT + File.pathSeparator + 
			RECODER_LIBS + File.pathSeparator + Test.PROC_PATH + proactiveLibs;
		System.setProperty("input.path" , inputPath);
	}
	
	private TransformationExecutor _executor = null;
	
	private void transformationInit() throws ParserException {
		_executor = new TransformationExecutor();
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
