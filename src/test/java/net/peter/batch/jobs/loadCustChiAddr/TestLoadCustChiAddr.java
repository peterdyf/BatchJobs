package net.peter.batch.jobs.loadCustChiAddr;

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

import com.cncbinternational.common.service.FilesService;
import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.batch.TestConfigs;
import net.peter.test.batch.TestConfigs4Ftp;
import net.peter.test.batch.TestFileUtilFTP;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, TestConfigs4Ftp.class, JdbcService.class, LoadCustChiAddr.class })
@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
public class TestLoadCustChiAddr {

	private static final String TEST_FILE = "classpath:com/cncbinternational/batch/jobs/loadCustChiAddr/IFA_NA_PPB.DAT";

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	DSLContext sql;

	@Autowired
	TestFileUtilFTP fileUtil;

	@Autowired
	FilesService filesService;

	private static final String FILE_NAME = "IFA_NA_PPB.DAT";

	private static final String APP_ID = "AL";
	private static final String NA_KEY = "100695015233748";
	private static final String TP_LANG = "EN";
	private static final String NAME_1 = "TestName1";
	private static final String NAME_2 = "TestName2";
	private static final String ADDR_1 = "TestAddr1";
	private static final String ADDR_2 = "TestAddr2";
	private static final String ADDR_3 = "TestAddr3";
	private static final String ADDR_4 = "TestAddr4";

	@Before
	public void setUp() throws IOException {
		fileUtil.clean(FILE_NAME);

		Map<String, String> valuesMap = new ConcurrentHashMap<>();
		valuesMap.put("date", now().fileString());
		valuesMap.put("appId", APP_ID);
		valuesMap.put("naKey", NA_KEY);
		valuesMap.put("tpLang", TP_LANG);
		valuesMap.put("name1", NAME_1);
		valuesMap.put("name2", NAME_2);
		valuesMap.put("addr1", ADDR_1);
		valuesMap.put("addr2", ADDR_2);
		valuesMap.put("addr3", ADDR_3);
		valuesMap.put("addr4", ADDR_4);

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
		Record r = select();
		assertThat("appId", r.getValue("APP_ID", String.class), equalTo(APP_ID));
		assertThat("naKey", r.getValue("NA_KEY", String.class), equalTo(NA_KEY));
		assertThat("tpLang", r.getValue("TP_LANG", String.class), equalTo(TP_LANG));
		assertThat("name1", r.getValue("NAME_1", String.class), equalTo(NAME_1));
		assertThat("name2", r.getValue("NAME_2", String.class), equalTo(NAME_2));
		assertThat("addr1", r.getValue("ADDR_1", String.class), equalTo(ADDR_1));
		assertThat("addr2", r.getValue("ADDR_2", String.class), equalTo(ADDR_2));
		assertThat("addr3", r.getValue("ADDR_3", String.class), equalTo(ADDR_3));
		assertThat("addr4", r.getValue("ADDR_4", String.class), equalTo(ADDR_4));
	}

	@After
	public void clear() {
		cleanTable();
		fileUtil.clean(FILE_NAME);
	}

	private void cleanTable() {
		sql.delete(DSL.table("CUST_CHI_ADDR")).execute();
	}

	private Record select() {
		return sql.select().from("CUST_CHI_ADDR").fetch().get(0);
	}
}
