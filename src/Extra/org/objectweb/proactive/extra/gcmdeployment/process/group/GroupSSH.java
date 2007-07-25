package org.objectweb.proactive.extra.gcmdeployment.process.group;

public class GroupSSH extends AbstractGroup {
    public GroupSSH() {
    }

    public GroupSSH(GroupSSH groupSSH) {
        super(groupSSH);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return new GroupSSH(this);
    }
}
