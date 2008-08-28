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
package org.objectweb.proactive.extra.annotation.transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObjectKernel;

import recoder.CrossReferenceServiceConfiguration;
import recoder.ParserException;
import recoder.convenience.Format;
import recoder.convenience.Naming;
import recoder.io.DataFileLocation;
import recoder.io.DataLocation;
import recoder.io.SourceFileRepository;
import recoder.java.CompilationUnit;
import recoder.kit.Problem;
import recoder.kit.ProblemReport;
import recoder.kit.Transformation;

/**
 * Execute annotation transformations
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public class TransformationExecutor {
	
	private static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS);
	
	public final CrossReferenceServiceConfiguration _sourceConfig;
	public final SourceFileRepository _sourceFileRepo;
	
	// this is to be used if Recoder is configured "outside"
	public TransformationExecutor() throws ParserException {
		_sourceConfig = new CrossReferenceServiceConfiguration();
		_sourceFileRepo = _sourceConfig.getSourceFileRepository();
		_sourceFileRepo.getAllCompilationUnitsFromPath();
	}
	
	private EnvironmentConfiguration _recoderCfg;
	private String _outputPath;
	// this is to be used if Recoder also has to be configured
	// arguments of the constructor:
	//	-> inputFilesPath - the path to the directory that contains the definition of the input class files
	//  -> outputPath     - the path to the directory where the files resulted will be written to
	public TransformationExecutor(String inputFilesPath, String outputPath) 
		throws ParserException, MissingConfigurationParameterException {
		
		_recoderCfg = new EnvironmentConfiguration();
		_recoderCfg.addToInputPath(inputFilesPath);
		String inputPath = _recoderCfg.getInputPath();
		_outputPath = outputPath;
		// set the system properties
		System.setProperty("input.path" , inputPath);
		System.setProperty("output.path" , outputPath);
		
		_sourceConfig = new CrossReferenceServiceConfiguration();
		_sourceFileRepo = _sourceConfig.getSourceFileRepository();
		_sourceFileRepo.getAllCompilationUnitsFromPath();
	}
	
	public void execute(Transformation transform) 
		throws CodeGenerationException, IOException {

		_logger.debug( "***execute transformation " + transform.getClass().getSimpleName() +  "***" );
		ProblemReport report = transform.execute();

		if (report instanceof Problem) {
			_logger.error( "Errors in the transformation.");
			throw new CodeGenerationException(report.toString());
		} else {		
			_logger.debug("Transformation succeeded - writing results");

			List<CompilationUnit> units = _sourceFileRepo.getCompilationUnits();

			for (int i = 0; i < units.size(); i += 1) {
				CompilationUnit cu = units.get(i);
				// only if the compilation unit is modified...
				if (!_sourceFileRepo.isUpToDate(cu)) {
					_logger.debug(Format.toString("%u [%f]", cu));
					// ...write the results to the output file
					_sourceConfig.getSourceFileRepository().print(cu);	
				}
				else {
					DataLocation dl = cu.getDataLocation();
					// if we are dealing with source code file...
					if( dl.getType().equals(DataFileLocation.LOCATION_TYPE_FILE)) {
						// ... then copy the file as-is to the output dir
						DataFileLocation dfl = (DataFileLocation)dl;
						File oldLocation = dfl.getFile();

						String filename = Naming.toCanonicalFilename(cu);
						File newLocation = new File( _outputPath , filename);
						copyFile( oldLocation , newLocation );
					}
				}
			}
		}

	}
	
	private void copyFile(File oldLocation, File newLocation) {
		
		try {
			FileChannel inputChannel = new FileInputStream(oldLocation).getChannel();
			FileChannel outputChannel = new FileOutputStream(newLocation).getChannel();
			// Copy file contents from source to destination
	        outputChannel.transferFrom( inputChannel, 0, inputChannel.size());
	    
	        // Close the channels
	        inputChannel.close();
	        outputChannel.close();
		} catch (FileNotFoundException e) {
			_logger.error( "Error while copying file " 
					+ oldLocation.getAbsolutePath() + " to the new location " 
					+ newLocation.getAbsolutePath(), e);
		} catch (IOException e) {
			_logger.error( "Error while copying file " 
				+ oldLocation.getAbsolutePath() + " to the new location " 
				+ newLocation.getAbsolutePath(), e);
		}
	}

	/*
	 * This is to ease the configuration of Recoder, especially its input.path 
	 */
	final class EnvironmentConfiguration {
		
		private final String _javaHome;  // path to the root of the JDK
		private static final String JAVA_RT_RELPATH = "/jre/lib/rt.jar";
		
		private final String _proActiveHome; // path to the ProActive distribution
		private static final String PROACTIVE_LIBS_RELPATH = "/dist/lib/";
		
		private final String _recoderHome; // path to where the Recoder libraries are 
		private static final String RECODER_LIBS_RELPATH = "/lib/recoder/";
		
		private String _inputPath; // the contents that will be put in the input.path configuration of Recoder 
		
		public String getInputPath() { 
			return _inputPath; 
		}
		
		public void addToInputPath(String pathComponent) {
			_inputPath = pathComponent + File.pathSeparator + _inputPath;
		}
		
		public EnvironmentConfiguration() throws MissingConfigurationParameterException {

			// "guess" the environment properties
			_javaHome = System.getenv("JAVA_HOME");
			if(_javaHome == null)
				throw new MissingConfigurationParameterException("JAVA_HOME" , " system property ");
			
			if(PAProperties.PA_HOME.isSet()){
				_proActiveHome = PAProperties.PA_HOME.getValue();
			}
			else{
				// try with the environment variable
				_proActiveHome = System.getenv("PROACTIVE_HOME");
				if( _proActiveHome == null )
				throw new MissingConfigurationParameterException( "proactive.home" , " Java property " );
			}
			
			_recoderHome = _proActiveHome + RECODER_LIBS_RELPATH;
			
			// initialize all the rest of the paths
			_inputPath = initInputPath();
			
		}
		
		public EnvironmentConfiguration(String javaHome, String proActiveHome, String recoderHome) {
			
			_javaHome = javaHome;
			_proActiveHome = proActiveHome;
			_recoderHome = recoderHome;
			
			_inputPath = initInputPath();
		}
		
		// the common part pf the constructors. We are guaranteed to have 
		// proactive_home, java_home and recoder_home initialized
		private String initInputPath() {
			
			// the JRE lib
			String jreLib = _javaHome + JAVA_RT_RELPATH;
			
			// the ProActive libs
			String proActiveLibDir = _proActiveHome + PROACTIVE_LIBS_RELPATH;
			String proActiveLibs = getJarsFromDir(proActiveLibDir);
			
			// the Recoder libs
			String recoderLibDir = _recoderHome;
			String recoderLibs = getJarsFromDir(recoderLibDir);
			
			return jreLib + File.pathSeparator + recoderLibs + proActiveLibs;
			
		}

		private String getJarsFromDir(String libDir) {
			
			File dir = new File(libDir);
			if(!dir.exists())
				throw new IllegalArgumentException("The directory " + libDir + " does not exist.");
			if(!dir.isDirectory())
				throw new IllegalArgumentException(libDir + " is not a directory.");
			
			File[] dirContents = dir.listFiles();
			if( dirContents == null )
				throw new IllegalArgumentException( "Could not list the contents of the directory " + libDir);
			
			StringBuilder jarsPath = new StringBuilder(dirContents.length * 42);
			for (File file : dirContents) {
				if( file.isFile() && Pattern.matches(".+\\.jar", file.getName()) ){
					jarsPath.append(file.getAbsolutePath() + File.pathSeparator);
				}
			}
			
			return jarsPath.toString();
		}
	}
	
	public static final int ARGS_NO = 2;
	public static void printUsage() {
		System.out.println("Usage: java " + TransformationExecutor.class.getSimpleName() + " inputFilesPath outputPath" );
		System.out.println("\tinputFilesPath is the path to the files that need to be processed.");
		System.out.println("\toutputPath is the path where the output will be written to.");
		System.out.println(" Before running, the following configurations must be done:");
		System.out.println("\tthe JAVA_HOME environment variable must be set, pointing to the root of the JDK distribution");
		System.out.println("\tthe proactive.home Java property, or PROACTIVE_HOME system property, must be set, pointing to the root of the ProActive distribution.");
	}
	
	public static void main(String[] args) {

		if( args.length != 2){
			System.out.println("Invalid number of args:" + args.length);
			printUsage();
			System.exit(1);
		}
		
		try {
			String inputFilesPath = args[0];
			String outputFiles = args[1];
			
			// test the provided input
			testDirName(inputFilesPath);
			testDirName(outputFiles);
			
			TransformationExecutor firestarter = new TransformationExecutor(inputFilesPath , outputFiles);
			Transformation transform = new AllAnnotationsTransformation( firestarter._sourceConfig);
			firestarter.execute(transform);
			System.exit(0);
		} catch (ParserException e) {
			_logger.error("Cannot get the compilation units from the path: " + System.getProperty("input.path"), e );
			printUsage();
			System.exit(1);
		} catch (IOException ioe) {
			_logger.error("Error while trying to write the output of the preprocessor. Error details: " , ioe);
			System.exit(1);
		} catch (CodeGenerationException e) {
			_logger.error("Code was not generated. Reason:", e);
			System.exit(1);
		} catch (MissingConfigurationParameterException e) {
			_logger.error("Recoder was not initialized due to missing configuration parameter.Details:", e);
			printUsage();
			System.exit(1);
		} catch(IllegalArgumentException e){
			_logger.error("Error on the input provided:" , e);
			printUsage();
			System.exit(1);
		}

	}

	private static void testDirName(String path) {
		File inputTest = new File(path);
		if(!inputTest.exists())
			throw new IllegalArgumentException("The directory " + path + " does not exist.");
		if(!inputTest.isDirectory())
			throw new IllegalArgumentException(path + " is not a directory.");
	}
		
}
