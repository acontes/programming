package org.objectweb.proactive.extra.dataspaces.console;

import java.net.URISyntaxException;

import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.dataspaces.InputOutputSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.NamingService;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;


public class NamingServiceOperation {

    /**
     * @param args
     */
    public static void main(String[] args) throws ProActiveException, URISyntaxException {
        final String name = NamingServiceOperation.class.getName();

        if (args.length != 3) {
            System.out.println("Usage: java " + name + " <naming service URL> <input name> <input URL>");
            System.out.println("Registers input with specified name and URL.");
            System.out.println("\t--help\tprints this screen");
            return;
        }

        final String url = args[0];
        final String inputName = args[1];
        final String inputURL = args[2];

        try {
            final InputOutputSpaceConfiguration conf = InputOutputSpaceConfiguration
                    .createInputSpaceConfiguration(inputURL, null, null, inputName);
            final SpaceInstanceInfo spaceInstanceInfo = new SpaceInstanceInfo(0, conf);
            NamingService stub = NamingService.createNamingServiceStub(url);

            try {
                stub.register(spaceInstanceInfo);
            } catch (WrongApplicationIdException e) {
                stub.registerApplication(0, null);
                stub.register(spaceInstanceInfo);
            }
        } finally {
            PALifeCycle.exitSuccess();
        }
    }
}
