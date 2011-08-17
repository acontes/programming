package functionalTests.component.sca.conformADL;

import java.io.IOException;
import java.net.URL;
import org.objectweb.fractal.adl.util.ClassLoaderHelper;
import org.objectweb.proactive.extensions.sca.adl.xml.SCAXMLConverter;

public class Test {

    public void test() throws IOException {
        ClassLoader cl = ClassLoaderHelper.getClassLoader(this);
        String name = "functionalTests.component.sca.conformADL.components.availability-test";
        //String name = "functionalTests.component.sca.conformADL.helloworld-property";
        final String file = name.replace('.', '/') + ".composite";
        System.err.println("DEBUGGG============" + file);
        final URL url = cl.getResource(file);
        //System.ou
        //SCAXMLConverter tmp = new SCAXMLConverter(url.openStream());
        SCAXMLConverter tmp = new SCAXMLConverter(url.openStream());
        System.err.println(tmp.ParseSCAXML());
    }

    public static void main(String[] args) throws Exception {

        new Test().test();
        
    }
}
