package modelisation.server.multiqueue;

import modelisation.multiqueueserver.CompositeQueueMetaObjectFactory;

import modelisation.server.singlequeue.SelectiveServer;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.body.request.BlockingRequestQueue;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestQueue;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeFactory;


public class SelectiveCompositeQueueServer extends SelectiveServer
    implements org.objectweb.proactive.RunActive {
    public SelectiveCompositeQueueServer() {
    }

    public SelectiveCompositeQueueServer(String url) {
        super(url);
    }

    public void runActivity(Body body) {
        System.out.println("SelectiveServer.live");
        Request request = null;
        this.register();
        BlockingRequestQueue queue = body.getRequestQueue();
        while (body.isActive()) {
            try {
                queue.waitForRequest();
                //                System.out.println("XXXX SelectiveServer.live requests available" );
                //                 System.out.println(queue);
                //   queue.processRequests(selectiveProcessor);
                this.serve(body, this.getNextRequest(queue));
                //                if (this.sourceRequest != null) {
                //                    queue.add(sourceRequest);
                //                    this.sourceRequest = null;
                //                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected Request getNextRequest(RequestQueue queue) {
        Request requestToServe;
        synchronized (queue) {
            requestToServe = queue.removeOldest("updateLocation");
            if (requestToServe == null) {
                requestToServe = queue.removeOldest("searchObject");
            }

            // System.out.println("SelectiveCompositeQueueServer.getNextRequest " + requestToServe);
            return requestToServe;
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("usage: java " +
                SelectiveCompositeQueueServer.class.getName() +
                " <server url> [node]");
            System.exit(-1);
        }
        Object[] arg = new Object[1];
        arg[0] = args[0];
        SelectiveServer server = null;
        try {
            if (args.length == 2) {
                server = (SelectiveCompositeQueueServer) ProActive.newActive(SelectiveCompositeQueueServer.class.getName(),
                        arg, NodeFactory.getNode(args[1]), null,
                        CompositeQueueMetaObjectFactory.newInstance());
            } else {
                server = (SelectiveCompositeQueueServer) ProActive.newActive(SelectiveCompositeQueueServer.class.getName(),
                        arg, (Node) null, null,
                        CompositeQueueMetaObjectFactory.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
