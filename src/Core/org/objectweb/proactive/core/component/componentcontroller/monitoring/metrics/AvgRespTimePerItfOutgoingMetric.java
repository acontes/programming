package org.objectweb.proactive.core.component.componentcontroller.monitoring.metrics;

import java.util.List;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Condition;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.OutgoingRequestRecord;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;

/**
 * Calculates the Average Response Time of all the requests that have been served by the component.
 * 
 * @author cruz
 *
 */

public class AvgRespTimePerItfOutgoingMetric extends Metric<Double> {

	public AvgRespTimePerItfOutgoingMetric() {
		subscribedEvents.add(RemmosEventType.OUTGOING_REQUEST_EVENT);
	}
	
	public Double calculate(final Object[] params) {

		List<OutgoingRequestRecord> recordList = null;
		recordList = records.getOutgoingRequestRecords(new Condition<OutgoingRequestRecord>(){
			// condition that returns true for every record that belongs to a given interface
			@Override
			public boolean evaluate(OutgoingRequestRecord orr) {
				String name = (String) params[0];
				if(orr.getInterfaceName().equals(name)) {
					return true;
				}
				return false;
			}
		}
		);
		// and calculates the average
		double sum = 0.0;
		double nRecords = recordList.size();
		for(OutgoingRequestRecord orr : recordList) {
			if(!orr.isVoidRequest() && orr.isFinished()) {
				sum += (double)(orr.getReplyReceptionTime() - orr.getSentTime());
			}
		}
		value = sum/nRecords;
		return value;
	}
		
}

