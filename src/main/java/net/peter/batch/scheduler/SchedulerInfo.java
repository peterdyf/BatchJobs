package net.peter.batch.scheduler;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.quartz.CronTrigger;

public final class SchedulerInfo {
	private final String name;
	private final String cron;
	private final Date nextTime;
	
	private SchedulerInfo(final CronTrigger trigger) {
		this.name = trigger.getKey().getName();
		this.cron = trigger.getCronExpression();
		this.nextTime = trigger.getNextFireTime();
	}

	public static SchedulerInfo newInstance(final CronTrigger trigger) {
		return new SchedulerInfo(trigger);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getName() {
		return name;
	}

	public String getCron() {
		return cron;
	}

	public Date getNextTime() {
		return nextTime;
	}
}