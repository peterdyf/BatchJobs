package net.peter.batch.jobs.tokenMailingAddress;

import net.peter.batch.annotation.ReportHeader;
import com.cncbinternational.spring.annotation.Order;

class RetFailCsv {

	@Order(100)
	@ReportHeader("RMID")
	String rmid;
}
