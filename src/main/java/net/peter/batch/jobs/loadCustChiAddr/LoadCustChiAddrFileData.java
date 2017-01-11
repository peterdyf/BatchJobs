package net.peter.batch.jobs.loadCustChiAddr;

import com.cncbinternational.spring.annotation.Order;

public class LoadCustChiAddrFileData {

	@Order(10)
	private String appId;

	@Order(20)
	private String naKey;

	@Order(30)
	private String tpLang;

	@Order(40)
	private String name1;

	@Order(50)
	private String name2;

	@Order(60)
	private String addr1;

	@Order(70)
	private String addr2;

	@Order(80)
	private String addr3;

	@Order(90)
	private String addr4;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getNaKey() {
		return naKey;
	}

	public void setNaKey(String naKey) {
		this.naKey = naKey;
	}

	public String getTpLang() {
		return tpLang;
	}

	public void setTpLang(String tpLang) {
		this.tpLang = tpLang;
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

}
