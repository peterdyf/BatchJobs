package net.peter.batch.jobs.emailUpdateReport;

import java.util.Date;

public class EmailUpdateExceptionReportDbData {
	String rmid;
	String name;
	String email;
	String code;
	String msg;
	Date responseDate;

	public String getRmid() {
		return rmid;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Date getResponseDate() {
		return responseDate;
	}

}
