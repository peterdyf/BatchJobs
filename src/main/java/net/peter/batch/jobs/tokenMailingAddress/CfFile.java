package net.peter.batch.jobs.tokenMailingAddress;

import net.peter.batch.annotation.Length;
import com.cncbinternational.spring.annotation.Order;

public class CfFile {

	@Order(10)
	@Length(15)
	private String rmid;

	@Order(20)
	@Length(40)
	private String name1;

	@Order(30)
	@Length(40)
	private String name2;

	@Order(40)
	@Length(40)
	private String addr1;

	@Order(50)
	@Length(40)
	private String addr2;

	@Order(60)
	@Length(40)
	private String addr3;

	@Order(70)
	@Length(40)
	private String addr4;

	@Order(80)
	@Length(37 + 21 + 6 + 1)
	@SuppressWarnings("PMD") // annotation only
	private final String skip = null;

	public String getRmid() {
		return rmid;
	}

	public void setRmid(String rmid) {
		this.rmid = rmid;
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getAddr1() {
		return addr1;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public String getAddr2() {
		return addr2;
	}

	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}

	public String getAddr3() {
		return addr3;
	}

	public void setAddr3(String addr3) {
		this.addr3 = addr3;
	}

	public String getAddr4() {
		return addr4;
	}

	public void setAddr4(String addr4) {
		this.addr4 = addr4;
	}

	public String getSkip() {
		return skip;
	}

	public void setSkip(String skip) {
		// no need
	}

}
