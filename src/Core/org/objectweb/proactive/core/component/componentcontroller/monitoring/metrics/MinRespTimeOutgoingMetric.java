package org.objectweb.proactive.core.component.componentcontroller.monitoring.metrics;

import java.util.List;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Condition;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.OutgoingRequestRecord;

/**
 * Calculates the Average Response Time of all the requests that have been served by the component.
 * 
 * @author cruz
 *
 */

public class MinRespTimeOutgoingMetric extends Metric<Long> {

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
		long min = Long.MAX_VALUE;
		long respTime;
		for(OutgoingRequestRecord orr : recordList) {
			if(orr.isFinished()) {
				respTime = orr.getReplyReceptionTime() - orr.getSentTime();
				if( respTime <= min ) {
					min = respTime;
				}
			}
		}
		value = min;
		return value;
	}
	
}


