package functionalTests.activeobject.request.tags;

import java.io.Serializable;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAMessageTagging;
import org.objectweb.proactive.core.body.tags.MessageTags;
import org.objectweb.proactive.core.body.tags.Tag;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.NodeException;


public class A implements Serializable {

    private B activeB;

    public A() {
    }

    public void initialize() throws ActiveObjectCreationException, NodeException {
        activeB = (B) PAActiveObject.newActive(B.class.getName(), new Object[0]);
    }

    public int propagateTag() {
        MessageTags tags = PAMessageTagging.getCurrentTags();
        Tag t = tags.addTag(new Tag("TEST_TAGS_00") {
            public Tag apply() {
                return this;
            }
        });
        t.setData(new Integer(42));
        return activeB.getResult();
    }

    public boolean stopPropagateTag() {
        MessageTags tags = PAMessageTagging.getCurrentTags();
        Tag t = tags.addTag(new Tag("TEST_TAGS_01") {
            public Tag apply() {
                return null;
            }
        });
        return activeB.checkTag("TEST_TAGS_01");
    }

    public Integer localMemory1() {
        MessageTags tags = PAMessageTagging.getCurrentTags();
        Tag t = tags.addTag(new Tag("TEST_TAGS_02") {
            public Tag apply() {
                Integer i = (Integer) getLocalMemory().get("MT_00");
                if (i != null) {
                    getLocalMemory().put("MT_00", ++i);
                }
                return this;
            }
        });
        t.createLocalMemory(10).put("MT_00", new Integer(0));
        int res = (Integer) t.getLocalMemory().get("MT_00");

        // To propagate the TAG and test the apply code
        activeB.getNumber();

        return res;
    }

    public Integer localMemory2() {
        MessageTags tags = PAMessageTagging.getCurrentTags();
        Tag t = tags.addTag(new Tag("TEST_TAGS_02") {
            public Tag apply() {
                return this;
            }
        });
        return (Integer) t.getLocalMemory().get("MT_00");
    }

    public boolean checkNoLocalMemoryOnB() {
        MessageTags tags = PAMessageTagging.getCurrentTags();
        Tag t = tags.addTag(new Tag("TEST_TAGS_02") {
            public Tag apply() {
                return this;
            }
        });
        return activeB.checkNoLocalMemory();
    }

    public boolean localMemoryLeaseExceeded() {
        MessageTags tags = PAMessageTagging.getCurrentTags();
        Tag t = tags.addTag(new Tag("TEST_TAGS_03-A") {
            public Tag apply() {
                return this;
            }
        });
        t.createLocalMemory(7).put("MT_01", new Integer(0));
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean resOnB = activeB.localMemoryLeaseExceeded();
        return t.getLocalMemory() == null && resOnB;
    }

    public boolean localMemoryLeaseClean2() {
        MessageTags tags = PAMessageTagging.getCurrentTags();
        Tag t = tags.addTag(new Tag("TEST_TAGS_04") {
            public Tag apply() {
                return this;
            }
        });
        t.createLocalMemory(8).put("MT_02", new Integer(0));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Integer res = (Integer) t.getLocalMemory().get("MT_02");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = (Integer) t.getLocalMemory().get("MT_02");
        return res != null;
    }

    public void exit() throws Exception {
        PAActiveObject.terminateActiveObject(true);
    }
}
