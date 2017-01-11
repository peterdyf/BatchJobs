package net.peter.batch.constant;

import java.lang.annotation.Annotation;

import net.peter.batch.annotation.JobConfiguration;

public final class JobConvention {

	public static final String JOBS_PACKAGE = "com.cncbinternational.batch.jobs";
	public static final Class<? extends Annotation> JOBS_ANNOTATION = JobConfiguration.class;

	public static final String JOB_PARAMETER_START_TIME = "startTime";

	private JobConvention(){
	}
}
