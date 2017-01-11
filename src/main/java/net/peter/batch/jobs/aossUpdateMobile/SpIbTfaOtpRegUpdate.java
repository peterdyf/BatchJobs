package net.peter.batch.jobs.aossUpdateMobile;
import java.util.Date;

import net.peter.batch.jobs.aossUpdateMobile.SpIbTfaOtpRegUpdate.SpIbTfaOtpRegUpdateParamIn;
import net.peter.batch.jobs.aossUpdateMobile.SpIbTfaOtpRegUpdate.SpIbTfaOtpRegUpdateParamOut;
import com.cncbinternational.spring.annotation.Order;
import com.cncbinternational.spring.annotation.TransactionalService;
import com.cncbinternational.spring.template.ConventionalStoreProcedure;

@TransactionalService
public class SpIbTfaOtpRegUpdate extends ConventionalStoreProcedure<SpIbTfaOtpRegUpdateParamIn, SpIbTfaOtpRegUpdateParamOut> {
	public String spName() {
		return "IB_TFA_OTP_REG";
	}

	public static class SpIbTfaOtpRegUpdateParamIn {
		@Order(100)
		String otpRegIdIn;

		@Order(200)
		String ibAcctNoIn;

		@Order(300)
		String otpRegExMsgIn;

		@Order(400)
		String otpRegActionIn;

		@Order(500)
		String otpMbCtryIn;

		@Order(600)
		String otpMbAreaIn;

		@Order(700)
		String otpMbNoIn;

		@Order(800)
		String otpUpdatedByIn;

	}

	public static class SpIbTfaOtpRegUpdateParamOut {
		String status;
		String otpRegId;
		Date otpDtUpdated;
	}

	public void updateForAOSS(String accNo, String mobile, String updatedBy) {
		SpIbTfaOtpRegUpdateParamIn params = new SpIbTfaOtpRegUpdateParamIn();
		params.otpRegIdIn = "";
		params.ibAcctNoIn = accNo;
		params.otpRegExMsgIn = "";
		params.otpRegActionIn = "UPDATE";
		params.otpMbCtryIn = "";
		params.otpMbAreaIn = "";
		params.otpMbNoIn = mobile;
		params.otpUpdatedByIn = updatedBy;
		super.execute(params);
	}
	
	public void insertForAOSS(String accNo, String mobile, String updatedBy) {
		SpIbTfaOtpRegUpdateParamIn params = new SpIbTfaOtpRegUpdateParamIn();
		params.otpRegIdIn = "";
		params.ibAcctNoIn = accNo;
		params.otpRegExMsgIn = "";
		params.otpRegActionIn = "REG";
		params.otpMbCtryIn = "";
		params.otpMbAreaIn = "";
		params.otpMbNoIn = mobile;
		params.otpUpdatedByIn = updatedBy;
		super.execute(params);
	}
	
}