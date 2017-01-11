package net.peter.batch.scheduler;

import static com.cncbinternational.common.util.MyDate.now;

import net.peter.batch.constant.JobConvention;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;

import net.peter.batch.register.InstanceManager;
import com.google.common.base.Throwables;

@DisallowConcurrentExecution
public class QuartzJob implements Job {

	@Autowired
	private InstanceManager instances;

	@Autowired
	private JobLauncher jobLauncher;

	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		try {
			jobLauncher.run(instances.instance(ctx.getJobDetail().getKey().getName()), parameters().toJobParameters());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			throw Throwables.propagate(e);
		}
	}

	private JobParametersBuilder parameters() {
		return new JobParametersBuilder().addString(JobConvention.JOB_PARAMETER_START_TIME, now().string()).addLong("test", System.currentTimeMillis());
	}
}