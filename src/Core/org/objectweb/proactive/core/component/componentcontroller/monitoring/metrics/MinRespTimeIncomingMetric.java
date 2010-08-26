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

public class MinRespTimeIncomingMetric extends Metric<Long> {

	public MinRespTimeIncomingMetric() {
		subscribedEvents.add(RemmosEventType.INCOMING_REQUEST_EVENT);
	}
	
	public Long calculate(final Object[] params) {

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
		long min = Long.MAX_VALUE;
		long respTime;
		for(IncomingRequestRecord irr : recordList) {
			if(irr.isFinished()) {
				respTime = irr.getReplyTime() - irr.getArrivalTime();
				if( respTime <= min ) {
					min = respTime;
				}
			}
		}
		value = min;
		return value;
	}
	
}


