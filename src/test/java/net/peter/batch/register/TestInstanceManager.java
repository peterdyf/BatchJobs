package net.peter.batch.register;

import static com.googlecode.catchexception.throwable.CatchThrowable.catchThrowable;
import static com.googlecode.catchexception.throwable.CatchThrowable.caughtThrowable;
import static com.googlecode.catchexception.throwable.apis.CatchThrowableHamcrestMatchers.hasMessage;
import static com.googlecode.catchexception.throwable.apis.CatchThrowableHamcrestMatchers.hasNoCause;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.NoSuchJobException;

public class TestInstanceManager {

	private static final String JOB_NAME_NORMAL_1 = "testJobNameNormal1";
	private static final String NOT_EXSITS_JOB_INSTANCE_NAME = "testNotExsitsJobInstanceName";

	final JobRegistry jobRegistry = mock(JobRegistry.class);

	final InstanceManager instanceManager;

	public TestInstanceManager() throws NoSuchJobException {
		when(jobRegistry.getJob(NOT_EXSITS_JOB_INSTANCE_NAME)).thenThrow(new NoSuchJobException(NOT_EXSITS_JOB_INSTANCE_NAME));
		Job mockJob = mockJob(JOB_NAME_NORMAL_1);
		when(jobRegistry.getJob(JOB_NAME_NORMAL_1)).thenReturn(mockJob);
		when(jobRegistry.getJobNames()).thenReturn(Arrays.asList(JOB_NAME_NORMAL_1));
		instanceManager = new InstanceManager(jobRegistry);
	}

	@Test
	public void notExistInstance() {
		catchThrowable(instanceManager).instance(NOT_EXSITS_JOB_INSTANCE_NAME);
		//@formatter:off
		assertThat("NoSuchJobException", caughtThrowable().getCause(), allOf(
			    instanceOf(NoSuchJobException.class), 
			    hasMessage(NOT_EXSITS_JOB_INSTANCE_NAME),
			    hasNoCause()
		));
		//@formatter:on
	}

	@Test
	public void allInstanceNames() {
		Collection<String> instances = instanceManager.allInstanceNames();
		assertThat("instances", instances, contains(JOB_NAME_NORMAL_1));
	}

	@Test
	public void instance() {
		Job instance = instanceManager.instance(JOB_NAME_NORMAL_1);
		assertThat("instance", instance.getName(), equalTo(JOB_NAME_NORMAL_1));
	}

	@Test
	public void hasInstance() {
		assertThat("instance", instanceManager.hasInstance(NOT_EXSITS_JOB_INSTANCE_NAME), equalTo(false));
	}

	private Job mockJob(String jobName) {
		Job job = mock(Job.class);
		when(job.getName()).thenReturn(jobName);
		return job;
	}
}
