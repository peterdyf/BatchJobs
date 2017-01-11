package net.peter.batch.jobs.tokenMailingAddress;

import net.peter.batch.annotation.Length;
import com.cncbinternational.spring.annotation.Order;

public class CcFile {

	@Order(10)
	@Length(8)
	@SuppressWarnings("PMD") // annotation only
	private final String skip1 = null;

	@Order(20)
	@Length(19)
	private String idNo;

	@Order(30)
	@Length(3)
	@SuppressWarnings("PMD") // annotation only
	private final String skip2 = null;

	@Order(40)
	@Length(16)
	private String acNo;

	@Order(50)
	@Length(30)
	private String fullName;

	@Order(60)
	@Length(30)
	private String addr1;

	@Order(70)
	@Length(30)
	private String addr2;

	@Order(80)
	@Length(30)
	private String addr3;

	@Order(90)
	@Length(30)
	private String addr4;

	@Order(100)
	@Length(130 + 8 + 21)
	@SuppressWarnings("PMD") // annotation only
	private final String skip3 = null;

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public void setAcNo(String acNo) {
		this.acNo = acNo;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}

	public void setAddr3(String addr3) {
		this.addr3 = addr3;
	}

	public void setAddr4(String addr4) {
		this.addr4 = addr4;
	}

	public void setSkip1(String skip1) {
		// no need
	}

	public void setSkip2(String skip2) {
		// no need
	}

	public void setSkip3(String skip3) {
		// no need
	}

	public String getIdNo() {
		return idNo;
	}

	public String getAcNo() {
		return acNo;
	}

	public String getFullName() {
		return fullName;
	}

	public String getAddr1() {
		return addr1;
	}

	public String getAddr2() {
		return addr2;
	}

	public String getAddr3() {
		return addr3;
	}

	public String getAddr4() {
		return addr4;
	}

}
