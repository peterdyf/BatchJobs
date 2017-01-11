package net.peter.batch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.context.annotation.Bean;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Bean
@StepScope
public @interface ReportHeader {
	String value();
}