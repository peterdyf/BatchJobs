package net.peter.batch.exception;

public class JobSchedulerNotExsitsException extends AbstractJobException {

	private static final long serialVersionUID = -6731206414940636757L;

	public JobSchedulerNotExsitsException(String jobName) {
		super(jobName);
	}

	@Override
	public String getMessage() {
		return String.format("Not Found Scheduler for job [%s]", getJobName());
	}

}
