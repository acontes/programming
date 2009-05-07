package org.objectweb.proactive.examples.dataspaces.hello;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.NamingService;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;


public class NamingServiceListing {

    private static final int APPLICATION_ID = 0;
    private final String namingServiceURL;

    public Set<SpaceInstanceInfo> execute(long applicationId) {
        DataSpacesURI query;
        NamingService ns = null;

        try {
            ns = NamingService.createNamingServiceStub(namingServiceURL);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        query = getLSQuery(applicationId);
        return ns.lookupMany(query);
    }

    private DataSpacesURI getLSQuery(long applicationId) {
        return DataSpacesURI.createURI(applicationId);
    }

    public NamingServiceListing(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java " + this.getClass().getName() + " <naming service URL>");
            System.exit(0);
        }
        namingServiceURL = args[0];
    }

    public static void main(String[] args) {
        final NamingServiceListing ls = new NamingServiceListing(args);
        prettyPrint(ls.execute(APPLICATION_ID));
        PALifeCycle.exitSuccess();
    }

    private static void prettyPrint(Set<SpaceInstanceInfo> listing) {
        List<String> sorted = new ArrayList<String>(listing.size());

        for (SpaceInstanceInfo sii : listing) {
            final StringBuffer sb = new StringBuffer();

            sb.append(sii.getMountingPoint());
            sb.append("\t-> ");
            sb.append(sii.getUrl());
            sorted.add(sb.toString());
        }
        java.util.Collections.sort(sorted);

        for (String string : sorted)
            System.out.println(string);
    }
}
