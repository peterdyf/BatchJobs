package net.peter.batch.jobs.fundTransTemplateDeletion;

import com.cncbinternational.spring.annotation.AuditLog;

@AuditLog(override = "pb_fund_txfer_tmp")
class FundTransTemplateAuditXml {
	@AuditLog
	String note;
	@AuditLog
	String templateNm;
	@AuditLog
	String frPbAcctNmEng;
	@AuditLog
	String toPbAcctNmEng;
	@AuditLog
	String tranCurrEngDscp;

	public void setNote(String note) {
		this.note = note;
	}

	public void setTemplateNm(String templateNm) {
		this.templateNm = templateNm;
	}

	public void setFrPbAcctNmEng(String frPbAcctNmEng) {
		this.frPbAcctNmEng = frPbAcctNmEng;
	}

	public void setToPbAcctNmEng(String toPbAcctNmEng) {
		this.toPbAcctNmEng = toPbAcctNmEng;
	}

	public void setTranCurrEngDscp(String tranCurrEngDscp) {
		this.tranCurrEngDscp = tranCurrEngDscp;
	}

}
