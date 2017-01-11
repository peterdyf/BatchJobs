package net.peter.batch.jobs.futureTimeDeposit;

public class FutureTimeDepositFlagDbData {
	String id;
	String pbUserId;
	String rmid;
	String ibAcctNo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPbUserId() {
		return pbUserId;
	}
	public void setPbUserId(String pbUserId) {
		this.pbUserId = pbUserId;
	}	
	public String getRmid() {
		return rmid;
	}
	public void setRmid(String rmid) {
		this.rmid = rmid;
	}
	public String getIbAcctNo() {
		return ibAcctNo;
	}
	public void setIbAcctNo(String ibAcctNo) {
		this.ibAcctNo = ibAcctNo;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Records: ").append("ID=").append(id).append("; ")
							  .append("PB USER ID=").append(pbUserId).append("; ")
							  .append("RMID=").append(rmid).append("; ")
							  .append("IB ACCT NO=").append(ibAcctNo);
		
		return sb.toString();
	}

	
	
}
