package net.peter.batch.jobs.emailUpdateReport.cc;

import static com.cncbinternational.common.util.MyDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.sql.Date;

import net.peter.batch.constant.JobConvention;
import org.jooq.DSLContext;
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

import com.cncbinternational.common.util.MyDate;
import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.batch.TestConfigs;
import net.peter.test.batch.TestConfigs4Ftp;
import net.peter.test.batch.TestFileUtilFTP;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, EmailUpdateCcExceptionReport.class, TestConfigs4Ftp.class })
@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
public class TestEmailUpdateCcExceptionReport {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	DSLContext sql;

	@Autowired
	TestFileUtilFTP fileUtil;

	private static final String EMAIL1 = "testEmail1";
	private static final String EMAIL2 = "testEmail2";
	private static final String EMAIL3 = "testEmail3";

	private static final String EX_CODE = "01";
	private static final String EX_MSG = "testExMsg";

	private static final String CSV_FILE_NAME = EmailUpdateCcExceptionReport.FTP_FOLDER + File.separator + "EmailUpdateCcExceptionReport.csv";

	@Before
	public void setUp() {
		fileUtil.clean(CSV_FILE_NAME);

		insert(now().minusDays(1), EMAIL1, EX_CODE, EX_MSG);
		insert(now(), EMAIL2, "00", "");
		insert(now(), EMAIL3, EX_CODE, EX_MSG);

	}

	@Test
	public void test() throws Exception {

		JobParameters params = new JobParametersBuilder().addString(JobConvention.JOB_PARAMETER_START_TIME, now().string()).addLong("RandomForTest", System.currentTimeMillis()).toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);
		assertThat("ExitCode", jobExecution.getExitStatus().getExitCode(), equalTo("COMPLETED"));

		assertThat("CSV File", fileUtil.getFile(CSV_FILE_NAME), notNullValue());
		assertThat("CSV File", fileUtil.readFile(CSV_FILE_NAME), hasSize(2));
	}

	@After
	public void clear() {
		delete(EMAIL1);
		delete(EMAIL2);
		delete(EMAIL3);
		fileUtil.clean(CSV_FILE_NAME);
	}

	private void insert(MyDate date, String email, String code, String message) {
		//@formatter:off
		sql.insertInto(DSL.table("TBL_PB_PREFERENCE_ESB_RESP"))
			.set(DSL.field("PB_USER_ID", String.class), "ECAE1298E80E7257E03400306E4A7AD5")	
			.set(DSL.field("NEW_EMAIL_ADDRESS", String.class), email)
			.set(DSL.field("CC_RESPONSE_CODE", String.class), code)
			.set(DSL.field("CC_RESPONSE_DESC", String.class), message)
			.set(DSL.field("CC_RESPONSE_DT", Date.class), now().sql())
			.set(DSL.field("RM_QRY_RESPONSE_CODE", String.class), "00")
			.set(DSL.field("RM_QRY_RESPONSE_DESC", String.class), "")
			.set(DSL.field("RM_QRY_RESPONSE_DT", Date.class), now().sql())
			.set(DSL.field("CREATE_DT", Date.class), date.sql())
			.set(DSL.field("CREATE_BY", String.class), "createdBy")
			.execute();
		//@formatter:on
	}

	private int delete(String email) {
		return sql.delete(DSL.table("TBL_PB_PREFERENCE_ESB_RESP")).where("NEW_EMAIL_ADDRESS = ?", email).execute();
	}

}
