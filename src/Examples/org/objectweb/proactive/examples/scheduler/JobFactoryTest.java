package org.objectweb.proactive.examples.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobFactory;

public class JobFactoryTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception{
		File f = new File("descriptors/scheduler/jobs/Job_2_tasks.xml");
		if(f.exists()) {
			System.out.println("OK");
			InputStream is = new FileInputStream(f);
			Job j = JobFactory.getFactory().createJob(is);
			System.out.println(j.getTasks().size());
		}
	}
}
