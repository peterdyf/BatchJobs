package net.peter.batch.build;

import javax.annotation.PostConstruct;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class AbstractJob implements JobFactory, ApplicationContextPropagation, JobBuilderProvider, StepBuilderProvider {

	private Job job;

	@Autowired
	private ApplicationContext context;

	@Override
	public final Job createJob() {
		return job;
	}

	protected abstract Job buildJob();

	@PostConstruct
	@SuppressWarnings("PMD.UnusedPrivateMethod") // spring init
	private void init() {
		job = buildJob();
	}

	public ApplicationContext applicationContext() {
		return context;
	}

}
