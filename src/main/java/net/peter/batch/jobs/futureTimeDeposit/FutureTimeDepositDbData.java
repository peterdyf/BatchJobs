package net.peter.batch.jobs.futureTimeDeposit;

public class FutureTimeDepositDbData {
	String pbUserId;
	String rmNumber;
	String returnCd;
	
	public String getPbUserId() {
		return pbUserId;
	}
	public void setPbUserId(String pbUserId) {
		this.pbUserId = pbUserId;
	}
	public String getRmNumber() {
		return rmNumber;
	}
	public void setRmNumber(String rmNumber) {
		this.rmNumber = rmNumber;
	}
	public String getReturnCd() {
		return returnCd;
	}
	public void setReturnCd(String returnCd) {
		this.returnCd = returnCd;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("record: ").append("PB USER ID=").append(pbUserId).append("; ")
									  .append("RM Number=").append(rmNumber);
		
		return sb.toString();
	}
	
}
