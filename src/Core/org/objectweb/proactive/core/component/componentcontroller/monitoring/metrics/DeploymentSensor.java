package org.objectweb.proactive.core.component.componentcontroller.monitoring.metrics;

import java.util.List;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Condition;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.IncomingRequestRecord;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;

/**
 * Obtains Infrastructure-Deployment information: Host, Node, VN
 * 
 * @author cruz
 *
 */

public class DeploymentSensor extends Metric<String> {

	public String calculate(final Object[] params) {

		String response;
		
		List<IncomingRequestRecord> recordList = null;
		recordList = records.getIncomingRequestRecords(new Condition<IncomingRequestRecord>(){
			// condition that returns true for every record
			@Override
			public boolean evaluate(IncomingRequestRecord irr) {
				return true;
			}
		}
		);
		
		// and calculates the average
		double sum = 0.0;
		double nRecords = recordList.size();
		for(IncomingRequestRecord irr : recordList) {
			if(irr.isFinished()) {
				sum += (double)(irr.getReplyTime() - irr.getArrivalTime());
			}
		}
		//value = sum/nRecords;
		return value.toString();
	}
	
}


