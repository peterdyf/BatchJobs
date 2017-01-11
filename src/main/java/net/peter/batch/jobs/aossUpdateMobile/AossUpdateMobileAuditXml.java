package net.peter.batch.jobs.aossUpdateMobile;

import com.cncbinternational.spring.annotation.AuditLog;

@AuditLog(override = "twoFactor")
class AossUpdateMobileAuditXml {
	@AuditLog
	String ibAcctNo;
	@AuditLog
	String otpMbCtry;
	@AuditLog
	String otpMbArea;
	@AuditLog
	String otpMbNo;

	public void setIbAcctNo(String ibAcctNo) {
		this.ibAcctNo = ibAcctNo;
	}

	public void setOtpMbCtry(String otpMbCtry) {
		this.otpMbCtry = otpMbCtry;
	}

	public void setOtpMbArea(String otpMbArea) {
		this.otpMbArea = otpMbArea;
	}

	public void setOtpMbNo(String otpMbNo) {
		this.otpMbNo = otpMbNo;
	}

}