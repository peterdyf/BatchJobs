package net.peter.batch.exception;

public class InvalidCronExpressionException extends AbstractJobException {

	private static final long serialVersionUID = -6731206414940636757L;

	private final String cron;

	public InvalidCronExpressionException(String jobName, String cron) {
		super(jobName);
		this.cron = cron;
	}

	@Override
	public String getMessage() {
		return String.format("Cron [%s] is invalid", cron);
	}

}
