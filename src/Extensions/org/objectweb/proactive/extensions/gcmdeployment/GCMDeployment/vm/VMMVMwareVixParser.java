package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.vm;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.extensions.gcmdeployment.GCMParserHelper;
import org.ow2.proactive.virtualizing.core.error.VirtualServiceException;
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVMM;
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVMM.Service;
import org.w3c.dom.Node;


public class VMMVMwareVixParser extends AbstractVMMParser {

	static final String NODE_NAME = "vmware-vix";
	static final String PA_HYPERVISOR_PORT = "port";
	static final String PA_HYPERVISOR_SERVICE = "service";
	static final String PA_SERVICE_SERVER = "server";
	static final String PA_SERVICE_WORKSTATION = "workstation";
	static final String PA_SERVICE_VI = "vi";

	@Override
	public String getNodeName() {
		return NODE_NAME;
	}

	@Override
	public void initializeGCMVirtualMachineManager(Node vmmNode, XPath xpath,
			GCMVirtualMachineManager gcmVMM) throws VirtualServiceException {
		try {
			String port = GCMParserHelper.getAttributeValue(vmmNode, PA_HYPERVISOR_PORT);
			String service = GCMParserHelper.getAttributeValue(vmmNode, PA_HYPERVISOR_SERVICE);
			String user = gcmVMM.getUser(), pwd = gcmVMM.getPwd();
			Object[] params;
			Class<?>[] cstArgs;
			if (port != null && service != null){
				cstArgs = new Class<?>[]{String.class,String.class,String.class,int.class,Service.class};
				params = new Object[5];
				params[3] = Integer.parseInt(port);
				params[4] = getService(service);
			}else if(service != null) {
				cstArgs = new Class<?>[]{String.class,String.class,String.class,Service.class};
				params = new Object[4];
				params[3] = getService(service);		
			}else if(port != null){
				cstArgs = new Class<?>[]{String.class,String.class,String.class,int.class,Service.class};
				params = new Object[5];
				params[3] = Integer.parseInt(port);
				params[4] = Service.vmwareDefault;
			}else{
				cstArgs = new Class<?>[]{String.class,String.class,String.class};
				params = new Object[3];
			}
			for(String uri : gcmVMM.getUris()){
				params[0] = uri;
				params[1] = user;
				params[2] = pwd;
				gcmVMM.addVirtualMachineManager(VMwareVMM.class.getName(),cstArgs, params);
			}
		} catch (Exception e) {
			throw new VirtualServiceException(e , "Cannot initialize GCMVirtualMachineManager.");
		}		
	}

	private Service getService(String service){
		if (service.equals(PA_SERVICE_SERVER))
			return Service.vmwareServer;
		if (service.equals(PA_SERVICE_WORKSTATION))
			return Service.vmwareWorkstation;
		if (service.equals(PA_SERVICE_VI))
			return Service.vmwareServerVI;
		return Service.vmwareDefault;
	}

}
