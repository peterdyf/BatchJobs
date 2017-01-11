package net.peter.batch.jobs.aossUpdateMobile;

class AossUpdateMobileDbData {
	String id;
	String rmid;
	String ibAcctNo;
	String smsPhoneNumber;
	String updatedBy;
	String preIbAcctNo;
	String preOtpMbCtry;
	String preOtpMbArea;
	String preOtpMbNo;

	public String getId() {
		return id;
	}

	public String getRmid() {
		return rmid;
	}

	public String getIbAcctNo() {
		return ibAcctNo;
	}

	public String getSmsPhoneNumber() {
		return smsPhoneNumber;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public boolean isPreExists() {
		return preIbAcctNo != null;
	}

	public String getPreIbAcctNo() {
		return preIbAcctNo;
	}

	public String getPreOtpMbCtry() {
		return preOtpMbCtry;
	}

	public String getPreOtpMbArea() {
		return preOtpMbArea;
	}

	public String getPreOtpMbNo() {
		return preOtpMbNo;
	}

}
