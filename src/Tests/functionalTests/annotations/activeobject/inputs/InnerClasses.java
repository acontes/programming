package functionalTests.annotations.activeobject.inputs;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

@ActiveObject
public class InnerClasses {
	
	protected javax.swing.JSplitPane verticalSplitPane;

	public InnerClasses() {
	}
	
	// inner class
	class Dada{}
	
	// inner class
	// ERROR
	@ActiveObject
	class AnnotatedDada{}
	
	public void localInnerClass() {
		// all this stuff cannot be seen by apt
		// local inner class
		class InnerClass{}
		
		// local inner class
		// ERROR
		@ActiveObject
		class InnerClass{}
	}
	
	// anonymous inner class
	public InnerClasses(String name, Integer width, Integer height) {
	        verticalSplitPane.addMouseListener(new java.awt.event.MouseAdapter() {
	            @Override
	            public void mouseEntered(java.awt.event.MouseEvent e) {
	            }
	        });
	 }
}
