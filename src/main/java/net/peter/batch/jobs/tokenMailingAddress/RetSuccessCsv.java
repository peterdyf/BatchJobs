package net.peter.batch.jobs.tokenMailingAddress;

import net.peter.batch.annotation.ReportHeader;
import com.cncbinternational.spring.annotation.Order;

class RetSuccessCsv {

	@Order(100)
	@ReportHeader("RMID")
	String rmid;

	@Order(200)
	@ReportHeader("Acct NO")
	String acNo;

	@Order(300)
	@ReportHeader("Category")
	String category;

	@Order(400)
	@ReportHeader("Name")
	String name;

	@Order(500)
	@ReportHeader("Address")
	String addr;

}
