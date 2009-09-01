package org.objectweb.proactive.core.mop;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;


public class Method {

    private CtMethod method;
    private List<Annotation> methodAnnotation;
    private List<MethodParameter> listMethodParameters;

    public List<MethodParameter> getListMethodParameters() {
        return listMethodParameters;
    }

    public void setListMethodParameters(List<MethodParameter> lmp) {
        this.listMethodParameters = lmp;
    }

    public Method(CtMethod method) {
        this.method = method;
        methodAnnotation = new ArrayList<Annotation>();
        listMethodParameters = new ArrayList<MethodParameter>();

        CodeAttribute codeAttribute = (CodeAttribute) method.getMethodInfo().getAttribute(CodeAttribute.tag);
        if (codeAttribute != null) {
            LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);
            if (localVariableAttribute != null) {
                // localVariableAttribute returns the number of method parameters
                // with the first being a reference on the object itself
                // thus the nb of effective parameters is  localVariableAttribute.tableLength() - 1;
                int nbOfParam = localVariableAttribute.tableLength() - 1;

                for (int i = 0; i < nbOfParam; i++) {
                    // initialize the list of parameters
                    listMethodParameters.add(new MethodParameter(null));
                }
            }
        }
        grabMethodandParameterAnnotation(method);
    }

    public CtMethod getCtMethod() {
        return method;
    }

    public void setCtMethod(CtMethod method) {
        this.method = method;
    }

    public List<Annotation> getMethodAnnotation() {
        return methodAnnotation;
    }

    public void setMethodAnnotation(List<Annotation> methodAnnotation) {
        this.methodAnnotation = methodAnnotation;
    }

    //    public void grabMethodandParameterAnnotation(CtMethod ctMethod) {
    //        Object[] o  = ctMethod.getAvailableAnnotations();
    //        for (int i =0; i< o.length; i++) {
    //            methodAnnotation.add((Annotation) o[i]);
    //        }
    //
    //        CodeAttribute codeAttribute = (CodeAttribute) ctMethod.getMethodInfo().getAttribute(
    //                CodeAttribute.tag);
    //        LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute) codeAttribute
    //                .getAttribute(LocalVariableAttribute.tag);
    //        for (int j = 0; j < localVariableAttribute.tableLength(); j++) {
    //
    //        }
    //
    //
    //
    //
    //    }

    private static ParameterAnnotationsAttribute toParameterAnnotationsAttribute(CtBehavior ctBehavior) {
        MethodInfo minfo = ctBehavior.getMethodInfo();
        ParameterAnnotationsAttribute attr = (ParameterAnnotationsAttribute) minfo
                .getAttribute(ParameterAnnotationsAttribute.visibleTag);
        return attr;
    }

    public void grabMethodandParameterAnnotation(CtBehavior ctBehavior) {

        //        Object[][] annotations = ctBehavior.getAvailableParameterAnnotations();

        ParameterAnnotationsAttribute attr = toParameterAnnotationsAttribute(ctBehavior);
        if (attr == null) {
            return;
        }

        javassist.bytecode.annotation.Annotation[][] parametersAnnotations = attr.getAnnotations();

        if (listMethodParameters.size() > 0) {

            for (int paramIndex = 0; paramIndex < parametersAnnotations.length; paramIndex++) {

                javassist.bytecode.annotation.Annotation[] paramAnnotations = parametersAnnotations[paramIndex];
                for (javassist.bytecode.annotation.Annotation parameterAnnotation : paramAnnotations) {

                    MethodParameter mp = listMethodParameters.get(paramIndex);
                    if (mp == null) {
                        mp = new MethodParameter(null);
                        listMethodParameters.set(paramIndex, mp);
                    }
                    mp.getAnnotations().add(parameterAnnotation);
                }
            }
        }
    }

}
