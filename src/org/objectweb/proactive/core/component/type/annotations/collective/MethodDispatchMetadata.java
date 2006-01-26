package org.objectweb.proactive.core.component.type.annotations.collective;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodDispatchMetadata {
    
    ParamDispatchMetadata mode();
        

}
