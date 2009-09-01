package org.objectweb.proactive.core.mop;

import java.util.ArrayList;
import java.util.List;

import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.annotation.Annotation;


public class MethodParameter {

    private LocalVariableAttribute lva;
    private List<Annotation> annotations;

    public MethodParameter(LocalVariableAttribute lva) {
        this.lva = lva;
        annotations = new ArrayList<Annotation>();
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public LocalVariableAttribute getLva() {
        return lva;
    }

}
