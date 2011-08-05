package functionalTests.component.sca.conformADL;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import org.objectweb.fractal.adl.util.ClassLoaderHelper;
import org.objectweb.proactive.extensions.sca.adl.xml.SCAXMLConverter;


public class Test {

    public void test() throws IOException {
        ClassLoader cl = ClassLoaderHelper.getClassLoader(this);
        String name = "functionalTests.component.sca.ConformADL.helloworld-property";
        final String file = name.replace('.', '/') + ".composite";
        System.err.println("DEBUGGG============" + file);
        final URL url = cl.getResource(file);

        SCAXMLConverter tmp = new SCAXMLConverter(url.openStream());
        System.err.println(tmp.ConvertSCAXMLToFractal());
    }

    public static void main(String[] args) throws Exception {

        new Test().test();

    }
}
