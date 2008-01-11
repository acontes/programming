package org.objectweb.proactive.compi2.control;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.compi2.control.Ack;
import org.objectweb.proactive.compi.control.ProActiveMPICluster;
import org.objectweb.proactive.compi2.control.ProActiveMPIData;
import org.objectweb.proactive.compi2.control.DGNode;
import org.objectweb.proactive.compi2.control.ProActiveMPIComm;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProActiveGroup;
import org.objectweb.proactive.core.node.NodeException;

public class DGNodeImpl implements DGNode, BindingController {
	private ProActiveMPIComm target;   
	private DGCluster clusterItf;
	private int jobID;
	private int rank;
	private boolean isRegistered = false;


	public DGNodeImpl() {
	}

	public DGNodeImpl(String libName, Integer jobNum)
	throws IllegalAccessException, ActiveObjectCreationException,
	InstantiationException, ClassNotFoundException, NodeException {
		this.jobID = jobNum.intValue();
        this.target = new ProActiveMPIComm(libName, ProActive.getBodyOnThis().getID().hashCode(), this.jobID);
        this.target.setMyNode((DGNodeImpl)ProActive.getStubOnThis());
        this.target.createRecvThread();
	}


	//============= Handshaking Methods================
	
	public void register(int myRank) {
		this.rank = myRank;
		isRegistered = true;
		notify();
	}
	
	
	public boolean blockUntilReady() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void wakeUpThread() {
		target.wakeUpThread();
		
	}
	
	//============= Message Passing Methods================
    public void receiveFromMpi(ProActiveMPIData m_r) {
        this.target.receiveFromMpi(m_r);
    }

    public void receiveFromProActive(ProActiveMPIData m_r) {
        this.target.receiveFromProActive(m_r);
    }

    public void sendToMpi(int jobID, ProActiveMPIData m_r)
        throws IOException {
        int destRank = m_r.getDest();

        //get reference of destination node
//        if (destNode != null) {
//            destNode.receiveFromMpi(m_r);
//        } else {
//            throw new IndexOutOfBoundsException(" destination " + destRank +
//                " in the jobID " + jobID + " is unreachable!");
//        }
    }

    public Ack sendToMpi(int jobID, ProActiveMPIData m_r, boolean b)
        throws IOException {
        this.sendToMpi(jobID, m_r);
        return new Ack();
    }

    public void MPISend(byte[] buf, int count, int datatype, int destRank,
        int tag, int jobID) {
        //create Message to send and use the native method
        ProActiveMPIData m_r = new ProActiveMPIData();

        m_r.setData(buf);
        m_r.setCount(count);
        m_r.setDatatype(datatype);
        m_r.setDest(destRank);
        m_r.setTag(tag);
        m_r.setJobID(jobID);

        //get reference of destination node
//        ProActiveMPINode destNode = this.nodeCache.get("" + jobID + "_" +
//                destRank);
//        if (destNode == null) {
//            destNode = myCluster.getNode(jobID, destRank);
//            this.nodeCache.put("" + jobID + "_" + destRank, destNode);
//        }
//
//        if (destNode != null) {
//            destNode.receiveFromProActive(m_r);
//        } else {
//            throw new IndexOutOfBoundsException(" destination " + destRank +
//                " in the jobID " + jobID + " is unreachable!");
//        }
    }

    public void allSendToMpi(int jobID, ProActiveMPIData m_r) {
        ProActiveMPICluster destCluster;
        
        //get reference of the cluster (?) and send it
//        
//        if (jobID < myCluster.getMaxJobID()) {
//            destCluster = myCluster.getCluster(jobID);
//            destCluster.clusterReceiveFromMpi(m_r);
//        } else {
//            throw new IndexOutOfBoundsException(" MPI job with such ID: " +
//                jobID + " doesn't exist");
//        }
    }

    public void sendToProActive(int jobID, ProActiveMPIData m_r)
        throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException,
            ClassNotFoundException {
//        int dest = m_r.getDest();
//
//        if (jobID < myCluster.getMaxJobID()) {
//            Hashtable proSpmdByClasses = this.myCluster.getCluster(jobID)
//                                                       .getUserProxySpmdMap();
//
//            Object proSpmdGroup = proSpmdByClasses.get(m_r.getClazz());
//
//            // if the corresponding object exists, its a -ProSpmd object- or a -proxy-
//            if (proSpmdGroup != null) {
//                Group g = ProActiveGroup.getGroup(proSpmdByClasses.get(
//                            m_r.getClazz()));
//
//                // its a ProSpmd Object
//                if (g != null) {
//                    // extract the specified object from the group and call method on it
//                    ((Method) g.get(dest).getClass()
//                               .getDeclaredMethod(m_r.getMethod(),
//                        new Class[] { ProActiveMPIData.class })).invoke(g.get(dest),
//                        new Object[] { m_r });
//                } else {
//                    if (((Object[]) proSpmdByClasses.get(m_r.getClazz()))[dest] != null) {
//                        ((Method) ((Object[]) proSpmdByClasses
//                                                                                         .get(m_r.getClazz()))[dest].getClass()
//                                                                                         .getDeclaredMethod(m_r.getMethod(),
//                            new Class[] { ProActiveMPIData.class })).invoke(((Object[]) proSpmdByClasses.get(
//                                m_r.getClazz()))[dest], new Object[] { m_r });
//                    } else {
//                        throw new ClassNotFoundException(
//                            "The Specified User Class *** " + m_r.getClazz() +
//                            "*** doesn't exist !!!");
//                    }
//                }
//            }
//            // the specified class doesn't exist  
//            else {
//                throw new ClassNotFoundException(
//                    "The Specified User Class *** " + m_r.getClazz() +
//                    "*** doesn't exist !!!");
//            }
//        } else {
//            throw new IndexOutOfBoundsException(" No MPI job exists with num " +
//                jobID);
//        }
    }

	//======================================================
	

	//============= BindingController Methods================
	public void bindFc(String itfName, Object itf)
	throws NoSuchInterfaceException, IllegalBindingException,
	IllegalLifeCycleException {
		if (itfName.equals("clientItf")) {
			this.clusterItf = (DGCluster) itf;
		}

	}

	public String[] listFc() {
		return new String[] { "clientItf" };
	}

	public Object lookupFc(String itfName)
	throws NoSuchInterfaceException {

		if (itfName.equals("clientItf")) {
			return clusterItf;
		}
		return null;
	}

	public void unbindFc(String itfName) throws NoSuchInterfaceException,
	IllegalBindingException, IllegalLifeCycleException {
		if (itfName.equals("clientItf")) {
			clusterItf = null;
		}
	}
	//======================================================


}
