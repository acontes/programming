package org.objectweb.proactive.core.component.reconfiguration.tagrequest;

import java.io.Serializable;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.body.request.BlockingRequestQueue;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.core.component.body.ComponentBody;
import org.objectweb.proactive.core.component.body.NFRequestFilterImpl;

public class ComponentServiceImpl extends Service {

	protected ProActiveAOPLikeController aopLikeController = null;
	protected LifeCycleController lifeCycleController = null;

	
	
	
	public ComponentServiceImpl(Body body) {
		super(body);
		// TODO Auto-generated constructor stub
		try {
			lifeCycleController = Fractal.getLifeCycleController(((ComponentBody) body)
                    .getProActiveComponentImpl());
			aopLikeController = 
				(ProActiveAOPLikeController) ((ComponentBody) body)
				.getProActiveComponentImpl()
				.getFcInterface(
						ProActiveAOPLikeController.AOP_LIKE_CONTROLLER_NAME);
		} catch (NoSuchInterfaceException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void fifoServing() {
		
		// TODO Auto-generated method stub
		while (LifeCycleController.STARTED.equals(lifeCycleController
				.getFcState())) {
			blockingServeOldest();
		}

	}

	public void blockingServeOldest(RequestFilter requestFilter, 
									  long timeout) {
		
		// TODO Auto-generated method stub
		Request r = requestQueue.blockingRemoveOldest(requestFilter, timeout);
		ComponentRequestTagUtilities utilities = aopLikeController.getRequestTagUtilities();
		
		if (r != null) {
			Component comp = ((ComponentBody) body).getProActiveComponentImpl(); 
			
		  utilities.preService(r.getMethodCall().getComponentMetadata().getTag(), comp);
		  body.serve(r);
		  utilities.postService(r.getMethodCall().getComponentMetadata().getTag(), comp);
		}
	}

	public void blockingServeOldest(RequestFilter requestFilter) {
		
		// TODO Auto-generated method stub
		RequestFilter utilsfilter = new ComponentRequestTagFilter(aopLikeController);

		blockingServeOldest(new composeABRequestFilter()
							 .getFilter(utilsfilter, requestFilter), 0);
	}


	public void blockingServeOldest() {
		
		RequestFilter requestFilter = null;
		blockingServeOldest(requestFilter);
	}

	private class ComponentRequestTagFilter implements RequestFilter{
		
		private ProActiveAOPLikeController aopLikeController;

		public ComponentRequestTagFilter(
				ProActiveAOPLikeController aopLikeController) {
			super();
			this.aopLikeController = aopLikeController;
		}

		public boolean acceptRequest(Request request) {
			// TODO Auto-generated method stub
			Object tag = request.getMethodCall().getComponentMetadata().getTag();
			return aopLikeController.getRequestTagUtilities().acceptRequest(tag);
		}		
		
	}
	
	private class composeABRequestFilter {

		public RequestFilter getFilter(RequestFilter requestFilterA,
				RequestFilter requestFilterB) {

		   RequestFilter filter = (requestFilterA == null) ? requestFilterB
				: (requestFilterB == null) ? requestFilterA
				: new composeNotNullABRequestFilter(requestFilterA,
									                requestFilterB);
		   return filter;
		}

		private class composeNotNullABRequestFilter implements RequestFilter {

			RequestFilter requestFilterA;
			RequestFilter requestFilterB;

			public composeNotNullABRequestFilter(
					RequestFilter requestFilterA,
					RequestFilter requestFilterB) {
				this.requestFilterA = requestFilterA;
				this.requestFilterB = requestFilterB;
			}

			public boolean acceptRequest(Request request) {
				// TODO Auto-generated method stub;
				return requestFilterA.acceptRequest(request)
						&& requestFilterB.acceptRequest(request);
			}
		}
	}

}
