package org.objectweb.proactive.extensions.structuredp2p.core.requests;

import org.objectweb.proactive.core.body.ProActiveMetaObjectFactory;
import org.objectweb.proactive.core.body.request.RequestReceiverFactory;


/**
 * @author Pellegrino Laurent
 * @version 0.1, 07/09/2009
 */
@SuppressWarnings("serial")
public class StructuredMetaObjectFactory extends ProActiveMetaObjectFactory {
    protected RequestReceiverFactory blockingRequestReceiverFactoryInstance;

    public StructuredMetaObjectFactory() {
        super();
        this.blockingRequestReceiverFactoryInstance = this.newBlockingRequestReceiverFactorySingleton();
    }

    protected RequestReceiverFactory newBlockingRequestReceiverFactorySingleton() {
        return new BlockingRequestReceiverFactory();
    }

    public RequestReceiverFactory newRequestReceiverFactory() {
        return this.blockingRequestReceiverFactoryInstance;
    }

}
