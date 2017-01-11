package net.peter.batch.jobs.billPaymentTemplateDeletion;

import net.peter.batch.annotation.ReportHeader;
import com.cncbinternational.spring.annotation.Order;

class BillPaymentTemplateCsvData {

	@Order(100)
	@ReportHeader("Data Time")
	String dateTime;

	@Order(200)
	@ReportHeader("Status")
	String status = "success";

	@Order(300)
	@ReportHeader("Bill No")
	String billNo;

	@Order(400)
	@ReportHeader("Function Accessed")
	String functionAccessed = "Bill Payment Template";

	@Order(500)
	@ReportHeader("RMID")
	String rmid;

	@Order(600)
	@ReportHeader("Action")
	String action = "delete";

	@Order(700)
	@ReportHeader("Channel")
	String channel = "BO";

	@Order(800)
	@ReportHeader("Template name")
	String templateName;

	@Order(900)
	@ReportHeader("Merchant")
	String merchant;

	@Order(1000)
	@ReportHeader("Bill Type")
	String billType;

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public void setRmid(String rmid) {
		this.rmid = rmid;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

}
