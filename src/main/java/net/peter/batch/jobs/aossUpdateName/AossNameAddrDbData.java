package net.peter.batch.jobs.aossUpdateName;

public class AossNameAddrDbData {
	String rmid;
	String versionNo;
	String tpLang;
	String engName;
	String name1;
	String name2;
	String addrL1;
	String addrL2;
	String addrL3;
	String addrL4;
	
	public String getRmid() {
		return rmid;
	}
	public void setRmid(String rmid) {
		this.rmid = rmid;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
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
	public String getAddrL1() {
		return addrL1;
	}
	public void setAddrL1(String addrL1) {
		this.addrL1 = addrL1;
	}
	public String getAddrL2() {
		return addrL2;
	}
	public void setAddrL2(String addrL2) {
		this.addrL2 = addrL2;
	}
	public String getAddrL3() {
		return addrL3;
	}
	public void setAddrL3(String addrL3) {
		this.addrL3 = addrL3;
	}
	public String getAddrL4() {
		return addrL4;
	}
	public void setAddrL4(String addrL4) {
		this.addrL4 = addrL4;
	}
	public String getEngName() {
		return engName;
	}
	public void setEngName(String engName) {
		this.engName = engName;
	}	
	public String getChiNm()
	{
		if(this.name1 != null && this.name2 != null) {
			return this.name1 + this.name2;
		} else if(this.name1 == null) {
			return this.name2;
		} else {
			return this.name1;
		}

	}	
}
