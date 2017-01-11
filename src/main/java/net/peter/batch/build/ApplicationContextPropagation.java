package net.peter.batch.build;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

@FunctionalInterface
public interface ApplicationContextPropagation {
	ApplicationContext applicationContext();

	default <T> T autowire(T obj) {
		AutowireCapableBeanFactory bf = applicationContext().getAutowireCapableBeanFactory();
		bf.autowireBean(obj);
		bf.initializeBean(obj, obj.getClass().getName());
		return obj ;
	}
}
