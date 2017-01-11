package net.peter.batch.jobs.tokenMailingAddress;

import net.peter.batch.annotation.Length;
import com.cncbinternational.spring.annotation.Order;

public class ImFile {

	@Order(10)
	@Length(10)
	@SuppressWarnings("PMD") // annotation only
	private final String skip1 = null;

	@Order(15)
	@Length(18)
	@SuppressWarnings("PMD") // annotation only
	private final String acNoSkip =null;
	
	@Order(20)
	@Length(17)
	private String acNo;

	@Order(30)
	@Length(390)
	@SuppressWarnings("PMD") // annotation only
	private final String skip2 = null;

	@Order(40)
	@Length(40)
	private String name1;

	@Order(50)
	@Length(40)
	private String name2;

	@Order(60)
	@Length(40)
	private String name3;

	@Order(70)
	@Length(40)
	private String name4;

	@Order(100)
	@Length(40)
	private String addr1;

	@Order(110)
	@Length(40)
	private String addr2;

	@Order(120)
	@Length(40)
	private String addr3;

	@Order(130)
	@Length(40)
	private String addr4;

	@Order(140)
	@Length(40)
	private String addr5;

	@Order(150)
	@Length(40)
	private String addr6;

	@Order(200)
	@Length(113)
	@SuppressWarnings("PMD") // annotation only
	private final String skip3 = null;

	@Order(300)
	@Length(30)
	private String city;

	@Order(400)
	@Length(522)
	@SuppressWarnings("PMD") // annotation only
	private final String skip4 = null;

	public void setAcNo(String acNo) {
		this.acNo = acNo;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public void setName3(String name3) {
		this.name3 = name3;
	}

	public void setName4(String name4) {
		this.name4 = name4;
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

	public void setAddr5(String addr5) {
		this.addr5 = addr5;
	}

	public void setAddr6(String addr6) {
		this.addr6 = addr6;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public void setAcNoSkip(String acNoSkip) {
		// no need
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

	public void setSkip4(String skip4) {
		// no need
	}

	public String getAcNo() {
		return acNo;
	}

	public String getName1() {
		return name1;
	}

	public String getName2() {
		return name2;
	}

	public String getName3() {
		return name3;
	}

	public String getName4() {
		return name4;
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

	public String getAddr5() {
		return addr5;
	}

	public String getAddr6() {
		return addr6;
	}

	public String getCity() {
		return city;
	}

}
