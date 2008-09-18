package temp;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.ActiveBody;
import org.objectweb.proactive.core.body.proxy.UniversalBodyProxy;
import org.objectweb.proactive.core.body.request.BlockingRequestQueue;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.component.representative.ProActiveComponentRepresentative;
import org.objectweb.proactive.core.util.CircularArrayList;
import org.objectweb.proactive.Service;

public class DelegateFunctionalCode extends Thread{

	


		private Component comp;
		private Component rootComp;
		
		
		public DelegateFunctionalCode(Component comp) {
			super();
			this.comp = comp;
		}
		public DelegateFunctionalCode(Component comp, Component root) {
			super();
			this.comp = comp;
			this.rootComp = root;
		}


		public void run(){
			
			BlockingRequestQueue rq;
			rq = ((ActiveBody)((UniversalBodyProxy)((ProActiveComponentRepresentative) comp)
	        		 .getProxy())
	        		 .getBody())
	        		 .getRequestQueue();
			rq.suspend();
			Body bodyComp =((ActiveBody)((UniversalBodyProxy)((ProActiveComponentRepresentative) comp)
	        		 .getProxy())
	        		 .getBody());
			Service serviceComp = new Service(bodyComp);
			
			
			StringBuffer sb = new StringBuffer();
			while(rq.size() < 5){
				try {
					((Runnable) comp.getFcInterface("r")).run();
					
				} catch (NoSuchInterfaceException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sb.append("composant: "+comp.toString())
				.append(" requests queue = "+rq.toString()+"size"+rq.size())
				.append("busy? "+serviceComp.hasRequestToServe()).append("\n");
				System.out.println("methode call  = "+sb.toString());
//				
//				
//				 int count = 0;
//				java.util.Iterator iterator = requestQueue.iterator();
//				while (iterator.hasNext()) {
//					Request currentrequest = (Request) iterator.next();
//					//UniqueID id = currentrequest.getFTManager()
//					System.err.println("seq nbr msg "+currentrequest.getSequenceNumber());
//					sb.append(count).append("--> ").append(currentrequest.getMethodCall()).append("\n");
//					count++;
//				}
				//System.out.println("methode call  = "+sb.toString());
				//(Re)rq.getInternalQueue().
			}
			
//			BlockingRequestQueue rqRoot;
//			rqRoot = ((ActiveBody)((UniversalBodyProxy)((ProActiveComponentRepresentative) rootComp)
//	        		 .getProxy())
//	        		 .getBody())
//	        		 .getRequestQueue();
//			
//			int count = 0;
//			CircularArrayList requestQueue = rq.getInternalQueue();
//			java.util.Iterator iterator = requestQueue.iterator();
//			while (iterator.hasNext()) {
//				Request currentrequest = (Request) iterator.next();
//				rqRoot.add(currentrequest);
//				//rq.removeOldest();
//				count++;
//			}
//			
//			System.out.println("queue state  : "+sb.toString());
			
//			try {
//				Fractal.getLifeCycleController(comp).stopFc();
//			} catch (IllegalLifeCycleException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NoSuchInterfaceException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			System.out.println("requests queue = "+rq.toString());
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			try {
//				Fractal.getLifeCycleController(comp).startFc();
//			} catch (IllegalLifeCycleException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NoSuchInterfaceException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			//System.out.println("requests queue bis = "+rq.toString());
		}
}
	
	
	
	
	
	
	
	
	
	

