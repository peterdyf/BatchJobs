package net.peter.batch.jobs.emailUpdateReport;

import net.peter.batch.annotation.ReportHeader;
import com.cncbinternational.spring.annotation.Order;

public class EmailUpdateExceptionReportCsvData {

	@Order(100)
	@ReportHeader("Customer ID")
	String customerId;

	@Order(200)
	@ReportHeader("Customer Name")
	String customerName;

	@Order(300)
	@ReportHeader("New Email Address")
	String newEmailAddress;

	@Order(400)
	@ReportHeader("Update Date")
	String updateDate;

	@Order(500)
	@ReportHeader("Reject Reason")
	String rejectReason;

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setNewEmailAddress(String newEmailAddress) {
		this.newEmailAddress = newEmailAddress;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

}
