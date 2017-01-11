package net.peter.batch.web.scheduler;

public class SchedulerUpdateRequest {
	private String cron;

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
}
