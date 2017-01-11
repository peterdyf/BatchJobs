package net.peter.batch.build;

import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;

public interface JobBuilderProvider extends JobFactory, ApplicationContextPropagation{

	default JobBuilder jobBuilder() {
		return applicationContext().getBean(JobBuilderFactory.class).get(getJobName());
	}
}
