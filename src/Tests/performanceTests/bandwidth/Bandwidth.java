package performanceTests.bandwidth;

import functionalTests.GCMFunctionalTestDefaultNodes;


public abstract class Bandwidth extends GCMFunctionalTestDefaultNodes {
    /** The buffer included in each message */
    static final public byte buf[] = new byte[10 * 1024 * 1024]; // 1Mo

    public Bandwidth() {
        super(1, 1);
    }
}
