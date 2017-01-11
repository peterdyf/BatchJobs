package net.peter.batch.build;

import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.StepBuilder;

public interface StepBuilderProvider extends JobFactory, ApplicationContextPropagation {

	default StepBuilder stepBuilder(String stepName) {
		return applicationContext().getBean(StepBuilderFactory.class).get(stepName);
	}

}
