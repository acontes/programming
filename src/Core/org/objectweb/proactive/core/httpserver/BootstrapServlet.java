package org.objectweb.proactive.core.httpserver;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.jetty.servlet.ServletHolder;
import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class BootstrapServlet extends HttpServlet {
    final static public String NS = "/bootstrap";
    final static public String MAPPING = NS + "/*";
    final static public String VM_ID = "vmid";
    final static public String DEPLOYMENT_ID = "deploymentId";
    final static public String TOPOLOGY_ID = "topologyId";
    final static public String PARENT_URL = "parentURL";
    final static public String ROUTER_ADDRESS = "routerAddress";
    final static public String ROUTER_PORT = "routerPort";
    final static public String COMMUNICATION_PROTOCOL = "communicationProtocol";
    final static public String SERVER_CODEBASE = "serverCodebase";
    final static public String PA_RT_COMMAND = "runtimeCommand";
    private Map<String, HashMap<String, String>> applis = new HashMap<String, HashMap<String, String>>();

    final static private Logger logger = ProActiveLogger.getLogger(Loggers.VIRTUALIZATION_BOOTSTRAP);

    static BootstrapServlet servlet = null;

    static public synchronized BootstrapServlet get() {
        if (servlet == null) {
            HTTPServer server = HTTPServer.get();
            servlet = new BootstrapServlet();
            server.registerServlet(new ServletHolder(servlet), BootstrapServlet.MAPPING);
        }

        return servlet;
    }

    private BootstrapServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        synchronized (get()) {
            if (logger.isDebugEnabled()) {
                String pathInfo = req.getPathInfo();
                logger.warn("Serving request from " + pathInfo);
            }
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF-8");
            String vmid = req.getParameter(VM_ID);
            HashMap<String, String> values = applis.get(vmid.trim());
            if (values != null) {
                Set<String> keys = values.keySet();
                for (String key : keys) {
                    resp.getOutputStream().println(key + " = " + values.get(key));
                }
            } else
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

            if (logger.isDebugEnabled()) {
                final String from = req.getLocalAddr() + ":" + req.getLocalPort();
                logger.warn("Serving request from " + from);
            }
        }
    }

    public boolean isRegistered(String deploymentId) {
        synchronized (get()) {
            return applis.get(deploymentId) != null;
        }
    }

    public String getBaseURI() {
        final URI uri = URIBuilder.buildURI(URIBuilder.getHostNameorIP(ProActiveInet.getInstance()
                .getInetAddress()), NS + "/", Constants.XMLHTTP_PROTOCOL_IDENTIFIER,
                PAProperties.PA_XMLHTTP_PORT.getValueAsInt());

        return uri.toString();
    }

    /**
     * This method is used to register a new remote Virtual Machine runtime dedicated
     * web page. The virtual machine will be able to connect to the given url to
     * gather required pieces information to successfully bootstrap
     * child ProActive Runtime.
     * @param id A string used to register the virtual machine web page. You'll have to
     * use it later to retrieve the associated URL.
     * @param values A hashmap containing every information to display on the web page.
     * @return the address where you'll be able to display required information.
     */
    public String registerAppli(String id, HashMap<String, String> values) {
        synchronized (get()) {
            applis.put(id, values);
            String res = getBaseURI() + "?" + BootstrapServlet.VM_ID + "=" + id;
            logger.info("Bootstrap servlet registered an app on:  " + res);
            return res;
        }
    }
}
