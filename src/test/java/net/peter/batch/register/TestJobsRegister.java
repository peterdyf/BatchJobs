package net.peter.batch.register;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.peter.batch.register.TestJobsRegister.TestJobsRegisterContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestJobsRegisterContext.class })
public class TestJobsRegister {

	final InstanceManager jobRegistry = mock(InstanceManager.class);
	final ChildApplicationBuilder childApplicationBuilder = mock(ChildApplicationBuilder.class);
	final JobsRegister jobsRegister = new JobsRegister(jobRegistry, childApplicationBuilder);
	final ApplicationContext childApplicationContext = mock(ApplicationContext.class);
	final JobFactory jobFactory = mock(JobFactory.class);

	@Before
	public void setUp() {
		when(childApplicationBuilder.build(any())).thenReturn(childApplicationContext);
		when(childApplicationContext.getBean(JobFactory.class)).thenReturn(jobFactory);
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // by Mockito
	public void test() {
		jobsRegister.init();
		verify(childApplicationBuilder, atLeast(2)).build(any());
		verify(childApplicationContext, atLeast(2)).getBean(JobFactory.class);
		verify(jobRegistry, atLeast(2)).register(any());
	}

	@Configurable
	public static class TestJobsRegisterContext {
	}
}
