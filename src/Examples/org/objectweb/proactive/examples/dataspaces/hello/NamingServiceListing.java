package org.objectweb.proactive.examples.dataspaces.hello;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.NamingService;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.SpacesMountManager;


/**
 * Tool for printing listing of a NamingService content.
 */
public class NamingServiceListing {

    private static final int APPLICATION_ID = 0;

    private String namingServiceURL;
    private boolean recursively;
    private DataSpacesURI query;
    private Set<SpaceInstanceInfo> listing;
    private Map<DataSpacesURI, List<String>> recurseTree = new HashMap<DataSpacesURI, List<String>>();
    private NamingService namingService;
    private SpacesMountManager mountManager;

    private void buildLSQuery(long applicationId) {
        query = DataSpacesURI.createURI(applicationId);
    }

    private String prettyPrint(FileObject fo) throws FileSystemException {
        final String uri = fo.getName().getURI();
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

    private void processFileTree(List<String> ret, FileObject fo) throws FileSystemException {
        ret.add(prettyPrint(fo));

        if (fo.getType().hasChildren()) {
            final FileObject[] ch = fo.getChildren();

            for (int i = 0; i < ch.length; i++) {
                processFileTree(ret, ch[i]);
            }
        }
    }

    private void processRecursively() throws FileSystemException {

        // we need to mount spaces for that..
        mountManager = new SpacesMountManager(namingService);

        // get FileObject for each space
        final Map<DataSpacesURI, FileObject> files = mountManager.resolveSpaces(query);

        for (Entry<DataSpacesURI, FileObject> space : files.entrySet()) {
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
        if (args.length == 2 && "-R".equals(args[0])) {
            recursively = true;
            namingServiceURL = args[1];
        } else if (args.length == 1) {
            namingServiceURL = args[0];
        } else {
            System.out
                    .println("Print listing of all data spaces mounted in <naming service URL>. The -R option enables recursive listing of data space content");
            System.out.println("Usage: java " + this.getClass().getName() + " [-R] <naming service URL>");
            System.exit(0);
        }
    }

    public void prettyPrint() {
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

    public Set<SpaceInstanceInfo> execute(long applicationId) {
        buildLSQuery(applicationId);

        try {
            namingService = NamingService.createNamingServiceStub(namingServiceURL);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        listing = namingService.lookupMany(query);

        if (recursively)
            try {
                processRecursively();
            } catch (FileSystemException e) {
                e.printStackTrace();
                recursively = false;
            }

        return Collections.unmodifiableSet(listing);
    }

    public static void main(String[] args) throws FileSystemException {
        final NamingServiceListing ls = new NamingServiceListing(args);
        ls.execute(APPLICATION_ID);
        ls.prettyPrint();
        PALifeCycle.exitSuccess();
    }
}
