package org.eclipse.proactive.debug.proactivefilter.core;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * 
 * @author lvanni
 */
public class ProactiveFilter extends ViewerFilter {

	private String threadRegex;
	private String stackRegex;
	private boolean isAuto;

	public ProactiveFilter(){
		this.isAuto = true;
	}

	public void addRegex(String threadRegex, String stackRegex){
		this.threadRegex = threadRegex;
		this.stackRegex = stackRegex;
		this.isAuto = false;
	}

	public void removeAllRegex(){
		this.isAuto = true;
	}

	public String removePackage(String className){
		Pattern p = Pattern.compile("[.]");
		String[] classSplit = p.split(className);
		if(classSplit.length != 0){
			return classSplit[classSplit.length - 1];
		} else {
			return className;
		}
	}

	/**
	 * This filter Hide the proactive stack trace in the debug view
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		try {
			if(element instanceof JDIThread){
				String threadName = ((JDIThread) element).getName();
				Pattern threadPattern;
				Matcher threadMatcher;
				if(isAuto){ // Automatic mode
					threadPattern = Pattern.compile("rmi://");
					threadMatcher = threadPattern.matcher(threadName);
					if(threadMatcher.find()){
						threadPattern = Pattern.compile("ProActive");
						threadMatcher = threadPattern.matcher(threadName);
						if(!threadMatcher.find())
							return true;
					}
				} else { // User mode
					threadPattern = Pattern.compile(threadRegex);
					threadMatcher = threadPattern.matcher(threadName);
					if(threadMatcher.find())
						return true;
				}
				return false;
			}
			if(element instanceof JDIStackFrame){
				if(isAuto){ // Automatic mode
					String stackName = ((JDIStackFrame) element).getDeclaringTypeName();
					Pattern stackPattern = Pattern.compile("org.objectweb.proactive");
					Matcher stackMatcher = stackPattern.matcher(stackName);
					if(!stackMatcher.find())
						return true;
				} else { // User mode
					String exp = "";
					String className = ((JDIStackFrame) element).getDeclaringTypeName();
					exp += removePackage(className);
					String methodeName = ((JDIStackFrame) element).getMethodName();
					exp += "." + methodeName + "("; 
					List<String> args = ((JDIStackFrame) element).getArgumentTypeNames();
					int line = ((JDIStackFrame) element).getLineNumber();
					if(line != -1){
						if(args.size() != 0){
							exp += removePackage(args.get(0));
							if(args.size() > 1){
								for(String arg : args){
									exp += ", " + removePackage(arg);
								}
							}

						}
						exp += ") line: " + line;
					} else {
						exp += ") line: not available";
					}
					Pattern stackPattern = Pattern.compile(stackRegex);
					Matcher stackMatcher = stackPattern.matcher(exp);
					if(stackMatcher.find())
						return true;
				}
				return false;
			}
		} catch (DebugException e) {
			e.printStackTrace();
		}
		return true;
	}
}

