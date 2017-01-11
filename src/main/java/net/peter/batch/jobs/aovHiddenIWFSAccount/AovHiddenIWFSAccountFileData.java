package net.peter.batch.jobs.aovHiddenIWFSAccount;

import net.peter.batch.annotation.Length;
import com.cncbinternational.spring.annotation.Order;

public class AovHiddenIWFSAccountFileData {

	@Order(10)
	@Length(12)
	private String accNo;

	@Order(20)
	@Length(3)
	@SuppressWarnings("PMD") // annotation only
	private final String skipField1 = null;

	@Order(30)
	@Length(8)
	private String acctTermDt;

	@Order(40)
	@Length(1)
	private String custType;

	@Order(50)
	@Length(60 + 40 * 4 + 18)
	@SuppressWarnings("PMD") // annotation only
	private final String skipField2 = null;

	@Order(60)
	@Length(1)
	private String acctStatus;

	@Order(70)
	@Length(1)
	private String acctUStatus;

	@Order(80)
	@Length(1)
	@SuppressWarnings("PMD") // annotation only
	private final String skipField3 = null;

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public void setSkipField1(String skipField1) {
		// no need
	}

	public void setAcctTermDt(String acctTermDt) {
		this.acctTermDt = acctTermDt;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public void setSkipField2(String skipField2) {
		// no need
	}

	public void setAcctStatus(String acctStatus) {
		this.acctStatus = acctStatus;
	}

	public void setAcctUStatus(String acctUStatus) {
		this.acctUStatus = acctUStatus;
	}

	public void setSkipField3(String skipField3) {
		// no need
	}

	public String getAccNo() {
		return accNo;
	}

	public String getAcctTermDt() {
		return acctTermDt;
	}

	public String getCustType() {
		return custType;
	}

	public String getAcctStatus() {
		return acctStatus;
	}

	public String getAcctUStatus() {
		return acctUStatus;
	}

}
