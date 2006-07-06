package org.objectweb.proactive.ic2d.monitoring.data;

import org.objectweb.proactive.core.UniqueID;

public class AOObject extends AbstractDataObject{
	
	/**
	 * State of the object (ex: WAITING_BY_NECESSITY)
	 */
	private int state;
	
	private static int counter = 0;
	
	private UniqueID id;
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
	public AOObject(NodeObject parent, String className, UniqueID id){
		super(parent, className + "#" + counter());
		System.out.println("AOObject : constructor id="+id.toString());
		//this.parent.putChild(this);
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
		return abstractDataObjectName;
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
