package net.peter.batch.jobs.aossUpdateName;

import net.peter.batch.jobs.aossUpdateName.SpPbUserUpdName.SpPbUserUpdNameParamOut;
import net.peter.batch.jobs.aossUpdateName.SpPbUserUpdName.UpdNameParam;
import com.cncbinternational.spring.annotation.Order;
import com.cncbinternational.spring.annotation.TransactionalService;
import com.cncbinternational.spring.template.ConventionalStoreProcedure;

@TransactionalService
public class SpPbUserUpdName extends ConventionalStoreProcedure<UpdNameParam, SpPbUserUpdNameParamOut> {
	public String spName() {
		return "PB_USER_UPD_NAME";
	}

	public static class UpdNameParam {

		@Order(100)
		String rmidIn;

		@Order(200)
		String nmIn;

		@Order(300)
		String tcNmIn;

		@Order(400)
		String scNmIn;

		public UpdNameParam(String rmidIn, String nmIn, String tcNmIn, String scNmIn) {
			this.rmidIn = rmidIn;
			this.nmIn = nmIn;
			this.tcNmIn = tcNmIn;
			this.scNmIn = scNmIn;
		}

	}

	public static class SpPbUserUpdNameParamOut {
		String status;
	}

	public void update(UpdNameParam updNameParam) {
		super.execute(updNameParam);
	}

}