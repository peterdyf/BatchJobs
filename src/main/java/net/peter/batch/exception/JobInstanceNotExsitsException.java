package net.peter.batch.exception;

public class JobInstanceNotExsitsException extends AbstractJobException {

	private static final long serialVersionUID = -6731206414940636757L;

	public JobInstanceNotExsitsException(String jobName) {
		super(jobName);
	}

	@Override
	public String getMessage() {
		return String.format("Job [%s] not Found", getJobName());
	}

}
