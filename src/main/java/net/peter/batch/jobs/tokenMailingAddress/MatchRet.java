package net.peter.batch.jobs.tokenMailingAddress;

import org.apache.commons.lang3.builder.ToStringBuilder;

@SuppressWarnings("PMD.TooManyMethods") // functional style
public final class MatchRet {

	static final String MATCHED = "Y";
	static final String UNMATCHED = "N";

	String rmid;
	String acNo;
	String category;
	String name;
	String addr;
	String ret;

	private MatchRet() {
	}

	public static MatchRet fromChiRM(ChiDb chi) {
		MatchRet ret = new MatchRet();
		ret.rmid = chi.getNaKey();
		ret.acNo = "";
		ret.category = "RM";
		ret.name = chi.getName1() + " " + chi.getName2();
		ret.addr = chi.getAddr1() + " " + chi.getAddr2() + " " + chi.getAddr3() + " " + chi.getAddr4();
		ret.ret = MATCHED;
		return ret;
	}

	public static MatchRet fromCF(CfDb cf) {
		MatchRet ret = new MatchRet();
		ret.rmid = cf.getRmid();
		ret.acNo = "";
		ret.category = "CF";
		ret.name = cf.getName1() + " " + cf.getName2();
		ret.addr = cf.getAddr1() + " " + cf.getAddr2() + " " + cf.getAddr3() + " " + cf.getAddr4();
		ret.ret = MATCHED;
		return ret;
	}

	public static MatchRet fromChiCC(ChiDb chi, String rmid) {
		return fromChi(rmid, chi, "CC");
	}

	public static MatchRet fromCC(CcDb cc, String rmid) {
		MatchRet ret = new MatchRet();
		ret.rmid = rmid;
		ret.acNo = cc.getAcNo();
		ret.category = "CC";
		ret.name = cc.getFullName();
		ret.addr = cc.getAddr1() + " " + cc.getAddr2() + " " + cc.getAddr3() + " " + cc.getAddr4();
		ret.ret = MATCHED;
		return ret;
	}

	public static MatchRet fromChiIM(ChiDb chi, String rmid) {
		return fromChi(rmid, chi, "IM");
	}

	public static MatchRet fromIM(ImDb im, String rmid) {
		MatchRet ret = new MatchRet();
		ret.rmid = rmid;
		ret.acNo = im.getAcNo();
		ret.category = "IM";
		ret.name = im.getName1() + " " + im.getName2() + " " + im.getName3() + " " + im.getName4();
		ret.addr = im.getAddr1() + " " + im.getAddr2() + " " + im.getAddr3() + " " + im.getAddr4() + " " + im.getAddr5() + " " + im.getAddr6() + " " + im.getCity();
		ret.ret = MATCHED;
		return ret;
	}

	public static MatchRet fromChiST(ChiDb chi, String rmid) {
		return fromChi(rmid, chi, "ST");
	}

	public static MatchRet fromST(StDb st, String rmid) {
		MatchRet ret = new MatchRet();
		ret.rmid = rmid;
		ret.acNo = st.getAcNo();
		ret.category = "ST";
		ret.name = st.getName1() + " " + st.getName2() + " " + st.getName3() + " " + st.getName4();
		ret.addr = st.getAddr1() + " " + st.getAddr2() + " " + st.getAddr3() + " " + st.getAddr4() + " " + st.getAddr5() + " " + st.getAddr6() + " " + st.getCity();
		ret.ret = MATCHED;
		return ret;
	}

	public static MatchRet unmatched(String rmid) {
		MatchRet ret = new MatchRet();
		ret.rmid = rmid;
		ret.acNo = "";
		ret.category = "";
		ret.name = "";
		ret.addr = "";
		ret.ret = UNMATCHED;
		return ret;
	}

	private static MatchRet fromChi(String rmid, ChiDb chi, String category) {
		MatchRet ret = new MatchRet();
		ret.rmid = rmid;
		ret.acNo = chi.getNaKey();
		ret.category = category;
		ret.name = chi.getName1() + " " + chi.getName2();
		ret.addr = chi.getAddr1() + " " + chi.getAddr2() + " " + chi.getAddr3() + " " + chi.getAddr4();
		ret.ret = MATCHED;
		return ret;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getRmid() {
		return rmid;
	}

	public String getAcNo() {
		return acNo;
	}

	public String getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public String getAddr() {
		return addr;
	}

	public String getRet() {
		return ret;
	}
}
