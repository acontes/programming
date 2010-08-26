package org.objectweb.proactive.core.component.componentcontroller.monitoring.metrics;

import java.util.List;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Condition;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.IncomingRequestRecord;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;

/**
 * Calculates the Average Response Time of all the requests that have been served by the component.
 * 
 * @author cruz
 *
 */

public class AvgRespTimeIncomingMetric extends Metric<Double> {

	public AvgRespTimeIncomingMetric() {
		subscribedEvents.add(RemmosEventType.INCOMING_REQUEST_EVENT);
	}
	
	public Double calculate(final Object[] params) {

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
		value = sum/nRecords;
		return value;
	}
	
}


