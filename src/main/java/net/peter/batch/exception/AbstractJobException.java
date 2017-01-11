package net.peter.batch.exception;

public class AbstractJobException extends RuntimeException {

	private static final long serialVersionUID = 1621448830771705039L;
	private final String jobName;

	public AbstractJobException(String jobName) {
		super();
		this.jobName = jobName;
	}

	public String getJobName() {
		return jobName;
	}

}