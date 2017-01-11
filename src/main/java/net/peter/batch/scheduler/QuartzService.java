package net.peter.batch.scheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import net.peter.batch.exception.InvalidCronExpressionException;
import net.peter.batch.exception.JobInstanceNotExsitsException;
import net.peter.batch.exception.JobSchedulerExsitsException;
import net.peter.batch.exception.JobSchedulerNotExsitsException;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;

import net.peter.batch.register.InstanceManager;
import com.cncbinternational.spring.annotation.Loggable;
import com.cncbinternational.spring.annotation.TransactionalService;
import com.google.common.base.Throwables;

/**
 * Convention:<br>
 * One Quartz Scheduler for one Batch Job.<br>
 * Quartz Job Name and Quartz Trigger Name should be the same with its Batch Job
 * Name. <br>
 * 
 * @author Peter.DI
 *
 */
@TransactionalService
public class QuartzService {

	private final Scheduler scheduler;

	private final InstanceManager instanceManager;

	@Autowired
	public QuartzService(Scheduler scheduler, InstanceManager instanceManager) {
		this.scheduler = scheduler;
		this.instanceManager = instanceManager;
	}

	@Loggable
	public List<SchedulerInfo> queryAll() {
		try {
			List<SchedulerInfo> list = new ArrayList<>();
			Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(null);
			for (TriggerKey triggerKey : triggerKeys) {
				CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
				list.add(SchedulerInfo.newInstance(trigger));

			}
			return list;

		} catch (SchedulerException e) {
			throw Throwables.propagate(e);
		}
	}

	@Loggable
	public Set<String> queryWithoutScheduler() {
		try {
			return new HashSet<String>() {
				private static final long serialVersionUID = -4304639540830621934L;

				{
					addAll(instanceManager.allInstanceNames());
					removeAll(scheduler.getTriggerKeys(null).stream().map(TriggerKey::getName).collect(Collectors.toSet()));
				}
			};

		} catch (SchedulerException e) {
			throw Throwables.propagate(e);
		}
	}

	@Loggable
	public SchedulerInfo query(String name) {
		try {
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(name));
			if (trigger == null) {
				throw new JobSchedulerNotExsitsException(name);
			}
			return SchedulerInfo.newInstance(trigger);
		} catch (SchedulerException e) {
			throw Throwables.propagate(e);
		}
	}

	@Loggable
	public void add(String name, String cron) {
		try {
			if (scheduler.checkExists(JobKey.jobKey(name))) {
				CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(name));
				throw new JobSchedulerExsitsException(name, trigger.getCronExpression());
			}
			if (!instanceManager.hasInstance(name)) {
				throw new JobInstanceNotExsitsException(name);
			}

			if (!org.quartz.CronExpression.isValidExpression(cron)) {
				throw new InvalidCronExpressionException(name, cron);
			}

			JobDetail job = JobBuilder.newJob(QuartzJob.class).withIdentity(name).build();
			Trigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(name).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
			scheduler.scheduleJob(job, cronTrigger);

		} catch (SchedulerException e) {
			throw Throwables.propagate(e);
		}
	}

	@Loggable
	public void modify(String name, String cron) {
		try {
			if (!scheduler.checkExists(JobKey.jobKey(name))) {
				throw new JobSchedulerNotExsitsException(name);
			}
			if (!org.quartz.CronExpression.isValidExpression(cron)) {
				throw new InvalidCronExpressionException(name, cron);
			}
			Trigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(name).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
			scheduler.rescheduleJob(TriggerKey.triggerKey(name), cronTrigger);
		} catch (SchedulerException e) {
			throw Throwables.propagate(e);
		}
	}

	@Loggable
	public void remove(String name) {
		try {
			if (!scheduler.checkExists(JobKey.jobKey(name))) {
				throw new JobSchedulerNotExsitsException(name);
			}
			scheduler.unscheduleJob(TriggerKey.triggerKey(name));
			scheduler.deleteJob(JobKey.jobKey(name));

		} catch (SchedulerException e) {
			throw Throwables.propagate(e);
		}
	}

	@PreDestroy
	@SuppressWarnings("PMD.UnusedPrivateMethod") // used by Spring
	private void close() {
		try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
			throw Throwables.propagate(e);
		}
	}

}
