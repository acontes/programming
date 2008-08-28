package org.objectweb.proactive.extra.annotation.transformation;

public class MissingConfigurationParameterException extends Exception {
	public MissingConfigurationParameterException(String prop, String type) {
		super("The " +  prop + " " +  type + " " +
				"needs to be configured with the path to the ProActive distribution.");
	}
}
