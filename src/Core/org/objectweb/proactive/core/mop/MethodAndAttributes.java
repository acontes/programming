package org.objectweb.proactive.core.mop;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javassist.CtMethod;


class MethodAndAttributes {

    CtMethod method;
    List<Annotation> annotations;

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public MethodAndAttributes(CtMethod method) {
        this.method = method;
        annotations = new ArrayList<Annotation>();
    }

}
