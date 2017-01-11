package net.peter.batch.jobs.emailUpdateReport.success;

import java.util.Date;

public class EmailUpdateSuccessReportDbData {
	String rmid;
	String name;
	String email;
	Date transDate;

	public String getRmid() {
		return rmid;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public Date getTransDate() {
		return transDate;
	}
}
