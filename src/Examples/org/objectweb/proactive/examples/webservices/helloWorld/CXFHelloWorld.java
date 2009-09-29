package org.objectweb.proactive.examples.webservices.helloWorld;

import java.io.Serializable;


public class CXFHelloWorld implements Serializable {

    private String text;

    public void setHello() {
        this.text = "Hello World !!!";
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public String setTextAndReturn(String text) {
        this.text = text;
        return "The \"text\" field has been set to " + this.text;
    }

    public String setTextAndReturn() {
        this.text = "Empty";
        return "The \"text\" field has been set to " + this.text;
    }
}
