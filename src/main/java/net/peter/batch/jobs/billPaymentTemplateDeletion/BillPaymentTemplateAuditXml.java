package net.peter.batch.jobs.billPaymentTemplateDeletion;

import com.cncbinternational.spring.annotation.AuditLog;
import com.google.common.base.CaseFormat;

@AuditLog(override = "pb_bill_pymt_tmp")
class BillPaymentTemplateAuditXml {

	@AuditLog
	String id;

	@AuditLog
	String ibAcctNo;

	@AuditLog
	String billNo;

	@AuditLog
	String templateNm;

	@AuditLog(toFormat = CaseFormat.LOWER_CAMEL)
	String catDesc;

	@AuditLog(toFormat = CaseFormat.LOWER_CAMEL)
	String mchntDesc;

	@AuditLog(toFormat = CaseFormat.LOWER_CAMEL)
	String billTypeDesc;

	@AuditLog(toFormat = CaseFormat.LOWER_CAMEL)
	String billAccDesc;

	public void setId(String id) {
		this.id = id;
	}

	public void setIbAcctNo(String ibAcctNo) {
		this.ibAcctNo = ibAcctNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public void setTemplateNm(String templateNm) {
		this.templateNm = templateNm;
	}

	public void setCatDesc(String catDesc) {
		this.catDesc = catDesc;
	}

	public void setMchntDesc(String mchntDesc) {
		this.mchntDesc = mchntDesc;
	}

	public void setBillTypeDesc(String billTypeDesc) {
		this.billTypeDesc = billTypeDesc;
	}

	public void setBillAccDesc(String billAccDesc) {
		this.billAccDesc = billAccDesc;
	}

}
