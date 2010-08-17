package org.objectweb.proactive.core.component.componentcontroller.monitoring.metrics;

import java.util.List;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Condition;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.IncomingRequestRecord;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.OutgoingRequestRecord;

/**
 * Calculates the Average Response Time of all the requests that have been served by the component.
 * 
 * @author cruz
 *
 */

public class AvgRespTimeOutgoingMetric extends Metric {

	public Object calculate(Object[] params) {

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
	
	public Object getValue() {
		return value;
	}
	
}


