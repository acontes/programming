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

public class MaxRespTimeOutgoingMetric extends Metric<Long> {

	public MaxRespTimeOutgoingMetric() {
		subscribedEvents.add(RemmosEventType.OUTGOING_REQUEST_EVENT);
	}
	
	public Long calculate(final Object[] params) {

		List<OutgoingRequestRecord> recordList = null;
		recordList = records.getOutgoingRequestRecords(new Condition<OutgoingRequestRecord>(){
			// condition that returns true for every record
			@Override
			public boolean evaluate(OutgoingRequestRecord orr) {
				return true;
			}
		}
		);
		
		// and calculates the average
		long max = 0;
		long respTime;
		for(OutgoingRequestRecord orr : recordList) {
			if(orr.isFinished()) {
				respTime = orr.getReplyReceptionTime() - orr.getSentTime();
				if( respTime >= max ) {
					max = respTime;
				}
			}
		}
		value = max;
		return value;
	}
	
}


