package net.peter.batch.scheduler;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionHamcrestMatchers.hasMessage;
import static com.googlecode.catchexception.apis.CatchExceptionHamcrestMatchers.hasNoCause;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.peter.batch.exception.InvalidCronExpressionException;
import net.peter.batch.exception.JobInstanceNotExsitsException;
import net.peter.batch.exception.JobSchedulerExsitsException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;

import net.peter.batch.exception.JobSchedulerNotExsitsException;
import net.peter.batch.register.InstanceManager;

@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals" }) // TestCases
public class TestQuartzService {

	private static final String JOB_NAME_1 = "testJobName1";
	private static final TriggerKey JOB_TRIGGER_KEY_1 = TriggerKey.triggerKey(JOB_NAME_1);
	private static final String JOB_CRON_1 = "0 0 12 * * ?";
	private static final String JOB_CRON_2 = "0 0 13 * * ?";
	private static final String JOB_CRON_INVALIDE_1 = "aaa";

	private static final String JOB_NAME_INSTANCE_ONLY_1 = "testJobNameInstanceOnly1";

	private static final String NOT_EXSITS_JOB_INSTANCE_NAME = "testNotExsitsJobInstanceName";

	final InstanceManager instanceManager = mock(InstanceManager.class);
	final Scheduler scheduler = mock(Scheduler.class);
	final QuartzService quartzService = new QuartzService(scheduler, instanceManager);

	@Before
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis") // pmd bug
	public void setUp() throws SchedulerException, ParseException {
		reset(scheduler);
		reset(instanceManager);
		when(scheduler.getTriggerKeys(null)).thenReturn(new HashSet<>(Arrays.asList(JOB_TRIGGER_KEY_1)));
		when(scheduler.getTrigger(JOB_TRIGGER_KEY_1)).thenReturn(new CronTriggerImpl() {
			private static final long serialVersionUID = 1L;

			{
				setKey(TriggerKey.triggerKey(JOB_NAME_1));
				setCronExpression(JOB_CRON_1);
			}
		});

		when(scheduler.checkExists(JobKey.jobKey(JOB_NAME_1))).thenReturn(true);
		when(scheduler.checkExists(JobKey.jobKey(JOB_NAME_INSTANCE_ONLY_1))).thenReturn(false);

		when(instanceManager.hasInstance(JOB_NAME_1)).thenReturn(true);
		when(instanceManager.hasInstance(JOB_NAME_INSTANCE_ONLY_1)).thenReturn(true);
		when(instanceManager.hasInstance(NOT_EXSITS_JOB_INSTANCE_NAME)).thenReturn(false);
		when(instanceManager.allInstanceNames()).thenReturn(Arrays.asList(JOB_NAME_INSTANCE_ONLY_1, JOB_NAME_1));

	}

	@Test
	public void queryAll() {
		List<SchedulerInfo> schedulerInfos = quartzService.queryAll();
		assertThat("schedulerInfos", schedulerInfos, hasSize(1));
		//@formatter:off
		assertThat("schedulerInfo", schedulerInfos.get(0), allOf(
				Matchers.<SchedulerInfo>hasProperty("name", equalTo(JOB_NAME_1)),
				Matchers.<SchedulerInfo>hasProperty("cron", equalTo(JOB_CRON_1))
		));
		//@formatter:on
	}

	@Test
	public void queryWithoutScheduler() {
		Set<String> instanceNames = quartzService.queryWithoutScheduler();
		assertThat("instanceNames size", instanceNames, hasSize(1));
		assertThat("instanceNames", instanceNames, contains(JOB_NAME_INSTANCE_ONLY_1));
	}

	@Test
	public void query() {
		SchedulerInfo schedulerInfo = quartzService.query(JOB_NAME_1);
		//@formatter:off
		assertThat("schedulerInfo", schedulerInfo , allOf(
				Matchers.<SchedulerInfo>hasProperty("name", equalTo(JOB_NAME_1)),
				Matchers.<SchedulerInfo>hasProperty("cron", equalTo(JOB_CRON_1))
		));
		//@formatter:on
	}

	@Test
	public void queryNotExist() {
		catchException(quartzService).query(NOT_EXSITS_JOB_INSTANCE_NAME);
		//@formatter:off
		assertThat("JobSchedulerNotExsitsException", caughtException(), allOf(
			    instanceOf(JobSchedulerNotExsitsException.class), 
			    hasMessage(String.format("Not Found Scheduler for job [%s]",NOT_EXSITS_JOB_INSTANCE_NAME)),
			    hasNoCause()
		));
		//@formatter:on
	}

	@Test
	public void add() throws SchedulerException {
		quartzService.add(JOB_NAME_INSTANCE_ONLY_1, JOB_CRON_1);
		ArgumentCaptor<JobDetail> jobDetail = ArgumentCaptor.forClass(JobDetail.class);
		ArgumentCaptor<CronTrigger> trigger = ArgumentCaptor.forClass(CronTrigger.class);
		verify(scheduler, times(1)).scheduleJob(jobDetail.capture(), trigger.capture());

		assertThat("jobName", jobDetail.getValue().getKey().getName(), equalTo(JOB_NAME_INSTANCE_ONLY_1));
		assertThat("triggerName", trigger.getValue().getKey().getName(), equalTo(JOB_NAME_INSTANCE_ONLY_1));
		assertThat("cron", trigger.getValue().getCronExpression(), equalTo(JOB_CRON_1));
	}

	@Test
	public void addDuplicated() {
		catchException(quartzService).add(JOB_NAME_1, JOB_CRON_1);
		//@formatter:off
		assertThat("JobSchedulerExsitsException", caughtException(), allOf(
			    instanceOf(JobSchedulerExsitsException.class),
			    hasMessage(String.format("Found Duplicated Job [%s] with a cron [%s]",JOB_NAME_1,JOB_CRON_1)),
			    hasNoCause()
		));
		//@formatter:on
	}

	@Test
	public void addNoInstance() {
		catchException(quartzService).add(NOT_EXSITS_JOB_INSTANCE_NAME, JOB_CRON_1);
		//@formatter:off
		assertThat("JobInstanceNotExsitsException", caughtException(), allOf(
			    instanceOf(JobInstanceNotExsitsException.class),
			    hasMessage(String.format("Job [%s] not Found",NOT_EXSITS_JOB_INSTANCE_NAME)),
			    hasNoCause()
		));
		//@formatter:on
	}

	@Test
	public void addInvalidCron() {
		catchException(quartzService).add(JOB_NAME_INSTANCE_ONLY_1, JOB_CRON_INVALIDE_1);
		//@formatter:off
		assertThat("JobInstanceNotExsitsException", caughtException(), allOf(
			    instanceOf(InvalidCronExpressionException.class),
			    hasMessage(String.format("Cron [%s] is invalid",JOB_CRON_INVALIDE_1)),
			    hasNoCause()
		));
		//@formatter:on
	}

	@Test
	public void modify() throws SchedulerException {
		quartzService.modify(JOB_NAME_1, JOB_CRON_2);
		ArgumentCaptor<TriggerKey> triggerKey = ArgumentCaptor.forClass(TriggerKey.class);
		ArgumentCaptor<CronTrigger> trigger = ArgumentCaptor.forClass(CronTrigger.class);
		verify(scheduler, times(1)).rescheduleJob(triggerKey.capture(), trigger.capture());

		assertThat("triggerKey", triggerKey.getValue().getName(), equalTo(JOB_NAME_1));
		assertThat("triggerName", trigger.getValue().getKey().getName(), equalTo(JOB_NAME_1));
		assertThat("cron", trigger.getValue().getCronExpression(), equalTo(JOB_CRON_2));
	}

	@Test
	public void modifyNotExists() {
		catchException(quartzService).modify(JOB_NAME_INSTANCE_ONLY_1, JOB_CRON_1);
		//@formatter:off
		assertThat("JobSchedulerNotExsitsException", caughtException(), allOf(
			    instanceOf(JobSchedulerNotExsitsException.class), 
			    hasMessage(String.format("Not Found Scheduler for job [%s]",JOB_NAME_INSTANCE_ONLY_1)),
			    hasNoCause()
		));
		//@formatter:on
	}

	@Test
	public void modifyInvalidCron() {
		catchException(quartzService).modify(JOB_NAME_1, JOB_CRON_INVALIDE_1);
		//@formatter:off
		assertThat("JobInstanceNotExsitsException", caughtException(), allOf(
			    instanceOf(InvalidCronExpressionException.class), 
			    hasMessage(String.format("Cron [%s] is invalid",JOB_CRON_INVALIDE_1)),
			    hasNoCause()
		));
		//@formatter:on
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // mokito verify
	public void remove() throws SchedulerException {
		quartzService.remove(JOB_NAME_1);
		verify(scheduler, times(1)).unscheduleJob(TriggerKey.triggerKey(JOB_NAME_1));
		verify(scheduler, times(1)).deleteJob(JobKey.jobKey(JOB_NAME_1));
	}

	@Test
	public void removeNotExists() {
		catchException(quartzService).remove(JOB_NAME_INSTANCE_ONLY_1);
		//@formatter:off
		assertThat("JobSchedulerNotExsitsException", caughtException(), allOf(
			    instanceOf(JobSchedulerNotExsitsException.class), 
			    hasMessage(String.format("Not Found Scheduler for job [%s]",JOB_NAME_INSTANCE_ONLY_1)),
			    hasNoCause()
		));
		//@formatter:on
	}
}
