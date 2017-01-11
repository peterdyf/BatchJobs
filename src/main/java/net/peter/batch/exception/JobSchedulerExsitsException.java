package net.peter.batch.exception;

public class JobSchedulerExsitsException extends AbstractJobException {

	private static final long serialVersionUID = -6731206414940636757L;
	private final String cron;

	public JobSchedulerExsitsException(String jobName, String cron) {
		super(jobName);
		this.cron = cron;
	}

	@Override
	public String getMessage() {
		return String.format("Found Duplicated Job [%s] with a cron [%s]", getJobName(), cron);
	}

}
