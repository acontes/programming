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
package org.objectweb.proactive.annotation.virtualnode;

import java.io.File;

import org.objectweb.proactive.annotation.transformation.AnnotationElements;
import org.objectweb.proactive.annotation.transformation.CodeGenerationException;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;

import recoder.java.declaration.AnnotationElementValuePair;
import recoder.java.declaration.AnnotationUseSpecification;

/**
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public class VirtualNodeAnnotationElements extends AnnotationElements {
	
	// elements
	// required
	String _name;
	String _descriptorFile;
	// optional
	String _descriptorType;
	String _logger;
	// generated
	String _padVarName;
	String _vnVarName;
	
	public static final String NAME_ELEMENT = "name";
	public static final String DESCR_TYPE_ELEMENT = "descriptorType";
	public static final String DESCR_FILE_ELEMENT = "descriptorFile";
	public static final String LOGGER_ELEMENT = "logger";
	
	public VirtualNodeAnnotationElements() {
		_name = null;
		_descriptorType = "gcm";
		_descriptorFile = null;
		_logger = null;
	}
	
	public String getVirtualNodeName() { return _name;	}
	
	public VirtualNodeAnnotationElements(AnnotationUseSpecification annotation) 
		throws CodeGenerationException 
	{
		// load default values
		this();
		if( annotation.getElementValuePairs() == null || annotation.getElementValuePairs().isEmpty() )
			throw new CodeGenerationException("The " + NAME_ELEMENT + " and " + DESCR_FILE_ELEMENT + " elements are mandatory for annotation " + VirtualNode.class.getSimpleName());
		// overwrite user-specified values
		for( AnnotationElementValuePair pair : annotation.getElementValuePairs()) {
			if(pair.getElementName().equals(NAME_ELEMENT)){
				_name = getStringValue(pair.getValue());
			}
			if(pair.getElementName().equals(DESCR_TYPE_ELEMENT)) {
				_descriptorType = getStringValue(pair.getValue());
				if(!supportedDescriptorType())
					throw new CodeGenerationException("The value " + _descriptorType + " is not supported for the element " + DESCR_TYPE_ELEMENT);
			}
			if(pair.getElementName().equals(DESCR_FILE_ELEMENT)) {
				_descriptorFile = getStringValue(pair.getValue());
			}
			if(pair.getElementName().equals(LOGGER_ELEMENT)){
				_logger = getStringValue(pair.getValue());
			}
		}
		
		if(_name == null)
			throw new CodeGenerationException("The " + NAME_ELEMENT + " element is mandatory for annotation " + VirtualNode.class.getSimpleName());
		if(_descriptorFile == null)
			throw new CodeGenerationException("The " + DESCR_FILE_ELEMENT + " element is mandatory for annotation " + VirtualNode.class.getSimpleName());
		
		// get only the file name without the extension
		generateVarNames();
		
	}
	
	private void generateVarNames() {
		File descr = new File(_descriptorFile);
		String fileName = descr.getName();
		fileName = fileName.substring(0, fileName.indexOf('.'));
		
		_padVarName = "pad" + camel(fileName) + camel(_descriptorType);
		_vnVarName = "vn" + camel(_name) + camel(fileName) + camel(_descriptorType) ;
	}
	
	public String getVnVarName() { return _vnVarName; }

	private String camel(String name) {
		if (name.length() == 0) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
	}

	private boolean supportedDescriptorType() {
		return _descriptorType.equals("old") || _descriptorType.equals("gcm");
	}

}