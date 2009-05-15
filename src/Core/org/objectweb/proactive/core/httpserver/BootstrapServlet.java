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
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.objectweb.proactive.core.runtime.StartPARuntime;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;


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

    public boolean registerAppli(String id, GCMApplicationInternal gcma, HashMap<String, String> values) {
        synchronized (get()) {
            logger.info("BootstrapServlet is registering an App with id: " + id);
            String deploymentIdKey = new Long(gcma.getDeploymentId()).toString();
            String tmp = null;
            try {
                tmp = RuntimeFactory.getDefaultRuntime().getURL();
            } catch (ProActiveException e) {
                logger.error("Cannot determine the URL of this runtime. Childs will not be able to register",
                        e);
                logger.error("Aborting virtual machine lauching.");
                return false;
            }
            String deploymentId = "-" + StartPARuntime.Params.deploymentId.shortOpt() + " " + deploymentIdKey;
            String parentURL = "-" + StartPARuntime.Params.parent.shortOpt() + " " + tmp;
            String communicationProtocol = PAProperties.PA_COMMUNICATION_PROTOCOL.getValue();
            if (communicationProtocol.equals("pamr")) {
                String routerAddress = PAProperties.PA_NET_ROUTER_ADDRESS.getValue();
                String routerPort = PAProperties.PA_NET_ROUTER_PORT.getValue();
                values.put(BootstrapServlet.ROUTER_ADDRESS, routerAddress);
                values.put(BootstrapServlet.ROUTER_PORT, routerPort);
            } else if (communicationProtocol.equals("rmi")) {
                String serverCodebase = "-" + StartPARuntime.Params.codebase.shortOpt() + " " +
                    ClassServerServlet.get().getCodeBase();
                values.put(BootstrapServlet.SERVER_CODEBASE, serverCodebase);
            }
            values.put(BootstrapServlet.COMMUNICATION_PROTOCOL, communicationProtocol);
            values.put(BootstrapServlet.DEPLOYMENT_ID, deploymentId);
            values.put(BootstrapServlet.PARENT_URL, parentURL);
            updateClassPath(gcma, values);
            applis.put(id, values);
            return true;
        }
    }

    private void updateClassPath(GCMApplicationInternal gcma, HashMap<String, String> values) {
        //TODO fill me
    }
}
