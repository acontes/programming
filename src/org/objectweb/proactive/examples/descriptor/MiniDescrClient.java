package org.objectweb.proactive.examples.descriptor;

import org.apache.log4j.Logger;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;


/**
 *
 *
 * @author Jerome+Sylvain
 */
public class MiniDescrClient {
    static Logger logger = Logger.getLogger(MiniDescrClient.class);
    private static final int NB_THREADS = 10;
    private static final int NB_CALLS_PER_THREAD = 109;

    public MiniDescrClient(String location) {
        VirtualNode virtualnode = null;

        ProActiveDescriptor pad = null;
        logger.info("-+-+-+-+-+-+-+- MiniDescrClient launched -+-+-+-+-+-+-+-");

        try {
            pad = ProActive.getProactiveDescriptor(location);
            virtualnode = pad.getVirtualNode("MiniVNServer");
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        virtualnode.activate();

        Node[] nodes = null;
        Thread[] bombs = new Thread[NB_THREADS];

        try {
            nodes = virtualnode.getNodes();

            for (int i = 0; i < nodes.length; i++) {
                for (int j = 0; j < NB_THREADS; j++) {
                    bombs[j] = new Bomber(j, nodes[i]);
                    bombs[j].start();

                    // Use this line instead to make sequential calls
                    // bombs[j].run();
                }
            }

            // Wait for all threads to finish before to return
            for (int j = 0; j < NB_THREADS; j++)
                try {
                    bombs[j].join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            new MiniDescrClient(MiniDescrClient.class.getResource(
                    "minidescriptor_client.xml").getPath());
        } else {
            new MiniDescrClient(args[0]);
        }

        System.out.println("Done");
        System.exit(0);
    }

    class Bomber extends Thread {
        int no;
        Node node;

        Bomber(int no, Node node) {
            this.no = no;
            this.node = node;
        }

        void appendZeros(StringBuffer buf, int n, int maxDigits) {
            int nbZeros = maxDigits -
                (int) Math.ceil((Math.log(n + 1) / Math.log(10)));

            for (int i = 0; i < nbZeros; i++)
                buf.append('0');
        }

        public void run() {
            try {
                MiniDescrActive desc = (MiniDescrActive) ProActive.newActive(MiniDescrActive.class.getName(),
                        null, node);

                int threadNbDigits = (int) Math.ceil((Math.log(NB_THREADS + 1) / Math.log(
                            10)));
                int callsNbDigits = (int) Math.ceil((Math.log(NB_CALLS_PER_THREAD +
                            1) / Math.log(10)));

                String threadTrace;

                {
                    StringBuffer buf = new StringBuffer();
                    buf.append("Thread No");
                    appendZeros(buf, no + 1, threadNbDigits);
                    buf.append(no + 1);
                    buf.append(" & Call No");
                    threadTrace = buf.toString();
                }

                for (int k = 0; k < NB_CALLS_PER_THREAD; k++) {
                    Message msg = desc.getComputerInfo();
                    StringBuffer buf = new StringBuffer(threadTrace);
                    appendZeros(buf, k + 1, callsNbDigits);
                    buf.append(k + 1);
                    buf.append(" -+-+-+-+-+-+-+- ");
                    buf.append(msg);
                    buf.append(" -+-+-+-+-+-+-+-");
                    logger.info(buf.toString());
                }
            } catch (ActiveObjectCreationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NodeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Error during remote call: ");
                e.printStackTrace();
            }
        }
    }
}
