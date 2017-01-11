package net.peter.batch.jobs.aovHiddenIWFSAccount;

import static com.cncbinternational.common.util.MyDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.peter.batch.constant.JobConvention;
import net.peter.batch.service.JdbcService;
import org.apache.commons.lang3.text.StrSubstitutor;
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

import com.cncbinternational.common.service.FilesService;
import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.batch.TestConfigs;
import net.peter.test.batch.TestConfigs4Ftp;
import net.peter.test.batch.TestFileUtilFTP;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, AovHiddenIWFSAccount.class, TestConfigs4Ftp.class, JdbcService.class })

@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
public class TestAovHiddenIWFSAccount {

	private static final String TEST_FILE = "classpath:com/cncbinternational/batch/jobs/aovHiddenIWFSAccount/iw_wm_acc.dat";

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	DSLContext sql;

	@Autowired
	TestFileUtilFTP fileUtil;

	@Autowired
	FilesService filesService;

	private static final String FILE_NAME = "iw_wm_acc.dat";

	private static final String ACC_NO_1 = "TEST001";
	private static final String ACC_NO_2 = "TEST002";
	private static final String ACC_NO_3 = "TEST003";

	@Before
	public void setUp() throws IOException {
		fileUtil.clean(FILE_NAME);
		Map<String, String> valuesMap = new ConcurrentHashMap<>();
		valuesMap.put("acc1", ACC_NO_1);
		valuesMap.put("acc2", ACC_NO_2);
		valuesMap.put("acc3", ACC_NO_3);
		valuesMap.put("date1", now().fileString());
		valuesMap.put("date2", now().fileString());
		valuesMap.put("date3", now().fileString());
		valuesMap.put("sts1", "T ");
		valuesMap.put("sts2", "A ");
		valuesMap.put("sts3", "AC");

		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		String template = filesService.readResource(TEST_FILE);
		String resolvedString = sub.replace(template);

		fileUtil.writeFile(resolvedString, FILE_NAME);
	}

	@Test
	public void test() throws Exception {
		JobParameters params = new JobParametersBuilder().addString(JobConvention.JOB_PARAMETER_START_TIME, now().string()).addLong("RandomForTest", System.currentTimeMillis()).toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);
		assertThat("ExitCode", jobExecution.getExitStatus().getExitCode(), equalTo("COMPLETED"));

		assertThat("acc1", select(ACC_NO_1), equalTo(1));
		assertThat("acc2", select(ACC_NO_2), equalTo(0));
		assertThat("acc3", select(ACC_NO_3), equalTo(1));
	}

	@After
	public void clear() {
		fileUtil.clean(FILE_NAME);
		delete(ACC_NO_1);
		delete(ACC_NO_2);
		delete(ACC_NO_3);
	}

	private void delete(String acctNo) {
		sql.delete(DSL.table("TBL_PB_ACCT_CLS_IWFS")).where("ACC_NO = ?", acctNo).execute();
	}

	private int select(String acctNo) {
		return sql.selectCount().from("TBL_PB_ACCT_CLS_IWFS").where("ACC_NO = ?", acctNo).fetchOne(0, int.class);
	}
}
