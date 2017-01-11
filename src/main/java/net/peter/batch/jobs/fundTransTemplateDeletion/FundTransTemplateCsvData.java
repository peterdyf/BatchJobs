package net.peter.batch.jobs.fundTransTemplateDeletion;

import net.peter.batch.annotation.ReportHeader;
import com.cncbinternational.spring.annotation.Order;

class FundTransTemplateCsvData {

	@Order(100)
	@ReportHeader("Data Time")
	String dateTime;

	@Order(200)
	@ReportHeader("Status")
	String status = "success";

	@Order(300)
	@ReportHeader("Account From")
	String accountFrom;

	@Order(400)
	@ReportHeader("Account To")
	String accountTo;

	@Order(500)
	@ReportHeader("Function Accessed")
	String functionAccessed = "Fund Transfer Template";

	@Order(600)
	@ReportHeader("RMID")
	String rmid;

	@Order(700)
	@ReportHeader("Action")
	String action = "delete";

	@Order(800)
	@ReportHeader("Channel")
	String channel = "BO";

	@Order(900)
	@ReportHeader("Template name")
	String templateName;

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public void setAccountFrom(String accountFrom) {
		this.accountFrom = accountFrom;
	}

	public void setAccountTo(String accountTo) {
		this.accountTo = accountTo;
	}

	public void setRmid(String rmid) {
		this.rmid = rmid;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}
