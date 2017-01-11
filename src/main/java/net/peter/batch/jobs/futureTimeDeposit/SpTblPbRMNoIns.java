package net.peter.batch.jobs.futureTimeDeposit;

import org.apache.commons.lang3.builder.ToStringBuilder;

import net.peter.batch.jobs.futureTimeDeposit.SpTblPbRMNoIns.InsRMNoParam;
import net.peter.batch.jobs.futureTimeDeposit.SpTblPbRMNoIns.SpTblPbRMNoInsParamOut;
import com.cncbinternational.spring.annotation.Loggable;
import com.cncbinternational.spring.annotation.Order;
import com.cncbinternational.spring.annotation.TransactionalService;
import com.cncbinternational.spring.template.ConventionalStoreProcedure;

@TransactionalService
public class SpTblPbRMNoIns extends ConventionalStoreProcedure<InsRMNoParam, SpTblPbRMNoInsParamOut>  {
	public String spName() {
		return "TBL_PB_RMNO_INS";
	}
	
	public static class InsRMNoParam {
		@Order(100)
		String pbUserIdIn;

		@Order(200)
		String rmNumberIn;

		public InsRMNoParam(String pbUserIdIn, String rmNumberIn) {
			this.pbUserIdIn = pbUserIdIn;
			this.rmNumberIn = rmNumberIn;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
		
		
	}

	public static class SpTblPbRMNoInsParamOut {
		String id;
	}

	@Loggable
	public void insert(InsRMNoParam insRMNoParam) {
		super.execute(insRMNoParam);
	}
	
}
