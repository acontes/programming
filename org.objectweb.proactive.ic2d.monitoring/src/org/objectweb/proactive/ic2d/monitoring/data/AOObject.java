package org.objectweb.proactive.ic2d.monitoring.data;

import org.objectweb.proactive.core.UniqueID;

public class AOObject extends AbstractDataObject{
	
	/**
	 * State of the object (ex: WAITING_BY_NECESSITY)
	 */
	private int state;
	
	/** the object's name */
	private String fullName;
	
	private static int counter = 0;
	
	private UniqueID id;
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
	public AOObject(NodeObject parent, String name, UniqueID id){
		super(parent);
		
		if ( name == null) 
			name = this.getClass().getName() ;
		this.fullName = name + "#" + counter();
		
		this.id = id;
		
		
		/*
		if(((NodeObject)this.parent).spy == null)
			System.err.println("AOObject : constructor => WARNING spy is null");
		((NodeObject)this.parent).spy.addMessageEventListener(this.id);
		
		System.out.println("Constructor AOObject : className = "+className+", id = "+counter);
		*/
	}
	
    //
    // -- PUBLIC METHODS ---------------------------------------------
    //
	
	public String getKey() {
		return this.id.toString();
	}

	public String getFullName() {
		return fullName;
	}
	
	/**
	 * Change the current state
	 */
	public void setState(int newState) {
		this.state = newState;
		setChanged();
		notifyObservers(new Integer(this.state));
	}
	
	public int getState(){
		return this.state;
	}
	
	public String toString() {
		return this.getFullName();
	}
	
	public String getType() {
		return "ao";
	}
	
	//
    // -- PROTECTED METHODS ---------------------------------------------
    //
	
	protected static synchronized void cancelCreation() {
		counter--;
	}
	
    //
    // -- PRIVATE METHODS ---------------------------------------------
    //
	
    private static synchronized int counter() {
    	return ++counter;
    }

	public void explore() {/* Do nothing */}
	
}
