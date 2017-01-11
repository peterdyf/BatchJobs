package net.peter.batch.register;

import org.springframework.batch.core.scope.JobScope;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Peter.DI
 * @see JobsRegister
 */
@Service
public class ChildApplicationBuilder {

	private final ApplicationContext parent;

	@Autowired
	public ChildApplicationBuilder(ApplicationContext parent) {
		this.parent = parent;
	}

	public ApplicationContext build(Class<?> configurationClass) {
		AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext();
		child.register(configurationClass, StepScope.class, JobScope.class);
		child.setParent(parent);
		child.refresh();
		return child;
	}
}
