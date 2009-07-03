package org.objectweb.proactive.extensions.structuredp2p.core.requests;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestReceiver;


/**
 * Personalized {@link RequestReceiver} used in order to be able to stop the reception of new
 * requests. So the active object implementing this kind of {@code RequestReceiver} cannot any more
 * receive new requests. However, it always treats the requests in its requests queue. Moreover, it
 * can always perform method calls on other active objects.
 * 
 * @author Pellegrino Laurent
 */
@SuppressWarnings("serial")
public class BlockingRequestReceiver extends org.objectweb.proactive.core.body.request.RequestReceiverImpl {
    boolean allowReception = true;

    /**
     * Constructor.
     */
    public BlockingRequestReceiver() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public int receiveRequest(Request request, Body bodyReceiver) {
        if (!this.allowReception) {
            throw new BlockingRequestReceiverException(this.getClass().getName());
        } else {
            return super.receiveRequest(request, bodyReceiver);
        }
    }

    /**
     * Indicates if the {@code RequestReceiver} accepts to receive new requests or not.
     * 
     * @return <code>true</code> if the {@code RequestReceiver} accepts to receive new requests.
     *         <code>false</code> otherwise.
     */
    public boolean allowReception() {
        return this.allowReception;
    }

    /**
     * Allows the {@code RequestReceiver} to receive new requests.
     */
    public void acceptReception() {
        this.allowReception = true;
    }

    /**
     * Prohibits the {@code RequestReceiver} to receive new requests.
     */
    public void blockReception() {
        this.allowReception = false;
    }

}
