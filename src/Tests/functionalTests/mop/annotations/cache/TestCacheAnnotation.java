package functionalTests.mop.annotations.cache;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;

import functionalTests.FunctionalTest;


public class TestCacheAnnotation extends FunctionalTest {

    /**
     * test the @Cache annotation, returned objects must always be the same
     * but differ from the initial one due to serialization.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test
    public void test() throws ActiveObjectCreationException, NodeException {

        Object initialObject = new SerializableClass();
        int initialI = 25;

        CacheAnnotationClass cac = (CacheAnnotationClass) PAActiveObject.newActive(CacheAnnotationClass.class
                .getName(), new Object[] { initialI, initialObject });

        Object cachedObject = cac.getO();
        Object secondObject = cac.getO();

        Assert.assertNotSame(cachedObject, initialObject);
        Assert.assertSame(cachedObject, secondObject);

    }

}
