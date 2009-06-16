package org.objectweb.proactive.extra.dataspaces.console;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extra.dataspaces.api.FileType;
import org.objectweb.proactive.extra.dataspaces.core.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.core.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.core.naming.NamingService;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;
import org.objectweb.proactive.extra.dataspaces.vfs.SpacesMountManager;


/**
 * Tool for printing listing of a NamingService content.
 */
public class NamingServiceListing {

    private long applicationID;

    private String namingServiceURL;

    private boolean recursively;
    private boolean listApplications;

    private DataSpacesURI query;
    private Set<SpaceInstanceInfo> listing;
    private Map<DataSpacesURI, List<String>> recurseTree = new HashMap<DataSpacesURI, List<String>>();
    private NamingService namingService;
    private SpacesMountManager mountManager;

    private Set<Long> registeredApplications;

    private void buildLSQuery(long applicationId) {
        query = DataSpacesURI.createURI(applicationId);
    }

    private String prettyPrint(DataSpacesFileObject fo) throws FileSystemException {
        final String uri = fo.getURI();
        final long time = fo.getContent().getLastModifiedTime();
        final Calendar lastModified = Calendar.getInstance();

        lastModified.setTimeInMillis(time);

        if (fo.getType() == FileType.FILE) {
            final long size = fo.getContent().getSize();
            return String.format("%s\t%d\t%s", uri, size, lastModified.getTime().toString());
        } else {
            return String.format("%s\t<DIR>\t%s", uri, lastModified.getTime().toString());
        }
    }

    private void processFileTree(List<String> ret, DataSpacesFileObject fo) throws FileSystemException {
        ret.add(prettyPrint(fo));

        if (fo.getType().hasChildren()) {
            final List<DataSpacesFileObject> ch = fo.getChildren();

            for (DataSpacesFileObject file : ch) {
                processFileTree(ret, file);
            }
        }
    }

    private void processRecursively() throws FileSystemException {

        // we need to mount spaces for that..
        mountManager = new SpacesMountManager(namingService);

        // get FileObject for each space
        final Map<DataSpacesURI, DataSpacesFileObject> files = mountManager.resolveSpaces(query, null);

        for (Entry<DataSpacesURI, DataSpacesFileObject> space : files.entrySet()) {
            try {
                final List<String> list = new LinkedList<String>();

                processFileTree(list, space.getValue());
                recurseTree.put(space.getKey(), list);
            } catch (FileSystemException e) {
                e.printStackTrace();
            }
        }
    }

    public NamingServiceListing(String[] args) throws FileSystemException {
        String appIdString = null;
        boolean printHelp = false;

        if (args.length == 3 && "-R".equals(args[0])) {
            recursively = true;
            namingServiceURL = args[1];
            appIdString = args[2];
        } else if (args.length == 2) {
            namingServiceURL = args[0];
            appIdString = args[1];
        } else if (args.length == 1 && "--help".equals(args[0])) {
            printHelp = true;
        } else if (args.length == 1) {
            namingServiceURL = args[0];
            listApplications = true;
        } else
            printHelp = true;

        if (printHelp)
            throw new IllegalArgumentException();

        if (appIdString != null) {
            try {
                applicationID = Long.parseLong(appIdString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    public void prettyPrint() {

        if (listApplications) {
            System.out.println(registeredApplications);
            return;
        }

        if (listing == null) {
            System.out.println("<EMPTY>");
            return;
        }
        List<String> sorted = new ArrayList<String>(listing.size());

        for (SpaceInstanceInfo sii : listing) {
            final StringBuffer sb = new StringBuffer();

            sb.append(sii.getMountingPoint());
            sb.append("\t-> ");
            sb.append(sii.getUrl());
            sorted.add(sb.toString());

            if (recursively)
                sorted.addAll(recurseTree.get(sii.getMountingPoint()));
        }
        java.util.Collections.sort(sorted);

        for (String string : sorted)
            System.out.println(string);
    }

    public Set<SpaceInstanceInfo> execute() throws ProActiveException, URISyntaxException {
        namingService = NamingService.createNamingServiceStub(namingServiceURL);

        if (listApplications) {
            registeredApplications = namingService.getRegisteredApplications();
            return null;
        }

        buildLSQuery(applicationID);
        System.out.println("looking for: " + query);
        listing = namingService.lookupMany(query);

        if (recursively)
            try {
                processRecursively();
            } catch (FileSystemException e) {
                e.printStackTrace();
                recursively = false;
            }

        return listing == null ? null : Collections.unmodifiableSet(listing);
    }

    public static void main(String[] args) throws FileSystemException, ProActiveException, URISyntaxException {
        NamingServiceListing ls = null;

        try {
            ls = new NamingServiceListing(args);
        } catch (IllegalArgumentException e) {
            final String msg = e.getMessage();
            final String name = NamingServiceListing.class.getName();

            if (msg != null)
                System.out.println("Error: " + msg);

            System.out.println("Usage: java " + name + " [-R] <naming service URL> <application ID>");
            System.out.println("       java " + name + " <naming service URL>");
            System.out.println("       java " + name + " --help");
            System.out
                    .println("Prints listing of all data spaces mounted in <naming service URL> with provided");
            System.out
                    .println("<application ID>. When no <application ID> provided list all registered application IDs.");
            System.out.println("\t-R\tenables recursive listing of data space content");
            System.out.println("\t--help\tprints this screen");
            return;
        }
        ls.execute();
        ls.prettyPrint();
        PALifeCycle.exitSuccess();
    }
}
