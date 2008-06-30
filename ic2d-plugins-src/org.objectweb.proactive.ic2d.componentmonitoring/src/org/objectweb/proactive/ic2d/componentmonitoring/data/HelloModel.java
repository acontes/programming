package org.objectweb.proactive.ic2d.componentmonitoring.data;

import java.util.ArrayList;
import java.util.List;



public class HelloModel {

    private String text = "Hello world usring GEF";
    protected HelloModel parent;
    protected List<HelloModel> children;
    
    public HelloModel(HelloModel parent)
    {
    	children = new ArrayList();
    	this.parent = parent;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    
    public List<HelloModel> getChildren()
    {
    	return children;
    }
    
    public void addChild(Object child)
    {
    	this.children.add((HelloModel)child);
    }
    
    public HelloModel getParent()
    {
    	return this.parent;
    }

}