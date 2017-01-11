package net.peter.tools;

import java.util.Date;

public class GenSqlBeanWorkspace extends GenSqlBean {

	protected String fileName() {
		return "classpath:com/cncbinternational/batch/jobs/emailUpdateReport/cc/EmailUpdateCcExceptionReport.sql";
	}
	
	protected Object[] getParams() {
		return new Object[]{new Date()};
	}
}
