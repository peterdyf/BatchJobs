package net.peter.batch.jobs.aossUpdateEmail;

import static com.cncbinternational.common.util.MyDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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

import net.peter.batch.constant.JobConvention;
import net.peter.batch.service.LocalFilesService;
import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.batch.TestConfigs;
import net.peter.test.batch.TestFileUtilLocal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, LocalFilesService.class, AossGenUpdateEmailFile.class, TestFileUtilLocal.class })

@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
public class TestAossGenUpdateEmailFile {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	DSLContext sql;

	@Autowired
	TestFileUtilLocal fileUtil;

	private static final String RMID = "HKIH9353609";
	private static final String EMAIL_ADDR = "Test" + System.currentTimeMillis() + "@email.com";
	private static final String FILE_NAME = "nameChgEmail.txt";

	@Before
	public void setUp() {
		fileUtil.clean(FILE_NAME);
		insert(RMID, EMAIL_ADDR);
	}

	@Test
	public void test() throws Exception {

		JobParameters params = new JobParametersBuilder().addString(JobConvention.JOB_PARAMETER_START_TIME, now().string()).addLong("RandomForTest", System.currentTimeMillis()).toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);
		assertThat("ExitCode", jobExecution.getExitStatus().getExitCode(), equalTo("COMPLETED"));
		assertThat("Text File", fileUtil.getFile(FILE_NAME), notNullValue());
		assertThat("Record 1 Deletion", select(EMAIL_ADDR), equalTo("Y"));

	}

	@After
	public void clear() {
		delete(RMID, EMAIL_ADDR);
		fileUtil.clean(FILE_NAME);
	}

	private void delete(String rmid, String emailAddress) {
		sql.delete(DSL.table("TBL_ESB_AOSS_EMAIL_ADDR")).where("rmid = ?", rmid).and("EMAIL_ADDR = ?", emailAddress).execute();
	}

	private void insert(String rmid, String emailAddress) {
		//@formatter:off
		sql.insertInto(DSL.table("TBL_ESB_AOSS_EMAIL_ADDR"))
			.set(DSL.field("RMID", String.class), rmid)	
			.set(DSL.field("EMAIL_ADDR", String.class), emailAddress)
			.set(DSL.field("UPDATED_BY", String.class), "test")
			.execute();
		//@formatter:on
	}

	private String select(String emailAddress) {
		return sql.select().from("TBL_ESB_AOSS_EMAIL_ADDR").where("EMAIL_ADDR = ?", emailAddress).fetchOne(DSL.field("EXECUTED", String.class));
	}
}
