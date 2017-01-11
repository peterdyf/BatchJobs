package net.peter.tools;

import com.cncbinternational.spring.annotation.Order;

public class GenSpResultWorkspace extends GenSpResult {

	public static final String SP_NAME = "pb_reg_fail_sel";

	public static class ParamIn {
		@Order(100)
		String rmidIn;
	}

	@Override
	protected ParamIn paramIn() {
		ParamIn params = new ParamIn();
		params.rmidIn = "123";
		return params;
	}

}
