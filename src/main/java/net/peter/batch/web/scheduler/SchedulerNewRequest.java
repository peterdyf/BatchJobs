package net.peter.batch.web.scheduler;

public class SchedulerNewRequest {
	private String name;
	private String cron;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
}
