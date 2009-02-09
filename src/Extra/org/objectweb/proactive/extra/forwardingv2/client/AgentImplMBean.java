package org.objectweb.proactive.extra.forwardingv2.client;

public interface AgentImplMBean {

    public long getLocalAgentID();

    public String getLocalAddress();

    public int getLocalPort();

    public String getRemoteAddress();

    public int getRemotePort();

    public String[] getMailboxes();

}
