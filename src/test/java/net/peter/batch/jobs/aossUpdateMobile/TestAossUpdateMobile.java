package net.peter.batch.jobs.aossUpdateMobile;

import static com.cncbinternational.common.util.MyDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Random;

import net.peter.batch.constant.JobConvention;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cncbinternational.common.storeprocedure.SpAuditLogInsert;
import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.batch.TestConfigs;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, AossUpdateMobile.class, SpAuditLogInsert.class, SpIbTfaOtpRegUpdate.class })
@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
public class TestAossUpdateMobile {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	DSLContext sql;

	private static final String RMID = "HKIP9518863";
	private static final String MOBILE = "TEST" + new Random().nextInt(10000);

	private Record oldRecord;

	@Before
	public void setUp() {
		oldRecord = selectObject(RMID);
		insert(RMID, MOBILE);
	}

	@Test
	public void test() throws Exception {

		JobParameters params = new JobParametersBuilder().addString(JobConvention.JOB_PARAMETER_START_TIME, now().string()).addLong("RandomForTest", System.currentTimeMillis()).toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);
		assertThat("ExitCode", jobExecution.getExitStatus().getExitCode(), equalTo("COMPLETED"));

		assertThat("Audit Record", selectAudit(MOBILE), equalTo(1));
		assertThat("mobile update", selectMobile(RMID), equalTo(MOBILE));
		assertThat("Record 1 Deletion", select(MOBILE), equalTo("Y"));

	}

	@After
	public void clear() {
		delete(RMID, MOBILE);
		deleteAudit(MOBILE);
		rollbackObject(RMID, oldRecord);
	}

	private void insert(String rmid, String emailAddress) {
		//@formatter:off
		sql.insertInto(DSL.table("TBL_ESB_AOSS_SMS_NUMBER"))
			.set(DSL.field("RMID", String.class), rmid)	
			.set(DSL.field("SMS_PHONE_NUMBER", String.class), emailAddress)
			.set(DSL.field("UPDATED_BY", String.class), "test")
			.execute();
		//@formatter:on
	}

	private String select(String emailAddress) {
		return sql.select().from("TBL_ESB_AOSS_SMS_NUMBER").where("SMS_PHONE_NUMBER = ?", emailAddress).fetchOne(DSL.field("EXECUTED", String.class));
	}

	private int rollbackObject(String rmid, Record oldRecord) {
		return sql.update(DSL.table("TBL_TFA_USER_INFO")).set(oldRecord).where("IB_ACCT_NO = ?", getAccNo(rmid)).execute();
	}

	private Record selectObject(String rmid) {
		String accNo = getAccNo(rmid);
		return sql.select().from("TBL_TFA_USER_INFO").where("IB_ACCT_NO = ?", accNo).fetchOne();
	}

	private String selectMobile(String rmid) {
		return selectObject(rmid).getValue("OTP_MB_NO", String.class);
	}

	private int selectAudit(String mobile) {
		return sql.selectCount().from("TBL_AUDIT_LOG_TEMP").where("POST_IMG like ?", "%" + mobile + "%").and("SUB_FUNCTION_TYPE = ?", "TFOR").fetchOne(0, int.class);
	}

	private String getAccNo(String rmid) {
		return sql.select().from("TBL_PB_USER").where("RMID = ?", rmid).and("IS_CLS = 0").fetchOne(DSL.field("IB_ACCT_NO", String.class));
	}

	private int delete(String rmid, String mobile) {
		return sql.delete(DSL.table("TBL_ESB_AOSS_SMS_NUMBER")).where("rmid = ?", rmid).and("SMS_PHONE_NUMBER = ?", mobile).execute();
	}

	private int deleteAudit(String mobile) {
		return sql.delete(DSL.table("TBL_AUDIT_LOG_TEMP")).where("POST_IMG like ?", "%" + mobile + "%").and("SUB_FUNCTION_TYPE = ?", "TFOR").execute();
	}
}
