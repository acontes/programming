package org.objectweb.proactive.core.component.adl.types;

import org.objectweb.fractal.adl.types.FractalTypeBuilder;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Matthieu Morel
 */
public class ProActiveTypeBuilder extends FractalTypeBuilder {
    public Object createInterfaceType(final String name, final String signature, final String role,
                                      final String contingency, final String cardinality,
                                      final Object context) throws Exception {

    // TODO : cache already created types ?

    boolean client = "client".equals(role);
    boolean optional = "optional".equals(contingency);

        // TODO_M should use bootstrap type factory with extended createFcItfType method

        return ProActiveTypeFactoryImpl.instance().createFcItfType(name, signature, client, optional, cardinality);
    }
}
