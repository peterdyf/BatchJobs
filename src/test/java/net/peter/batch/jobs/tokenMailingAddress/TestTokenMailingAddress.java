package net.peter.batch.jobs.tokenMailingAddress;

import static com.cncbinternational.common.util.MyDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.peter.batch.constant.JobConvention;
import net.peter.batch.service.JdbcService;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.peter.batch.jobs.tokenMailingAddress.TestTokenMailingAddress.TestTokenMailingAddressContext;
import com.cncbinternational.common.service.FilesService;
import com.cncbinternational.common.service.TransNoService;
import com.cncbinternational.common.service.host.HostService;
import com.cncbinternational.common.service.host.TR08Service;
import com.cncbinternational.common.service.host.message.TR08Out;
import com.cncbinternational.common.storeprocedure.SpTransRefXSeqSel;
import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.batch.TestConfigs;
import net.peter.test.batch.TestConfigs4Ftp;
import net.peter.test.batch.TestFileUtilFTP;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, TokenMailingAddress.class, TestConfigs4Ftp.class, TransNoService.class, SpTransRefXSeqSel.class, TestTokenMailingAddressContext.class,
		JdbcService.class, TR08Service.class, TR08AcctNoAdapter.class, TokenMailingAddressMatchingService.class })
@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
@SuppressWarnings("PMD")
public class TestTokenMailingAddress {

	private static final String FILE_PATH = "classpath:com/cncbinternational/batch/jobs/tokenMailingAddress/";

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	DSLContext sql;

	@Autowired
	TestFileUtilFTP fileUtil;

	@Autowired
	FilesService filesService;

	private static final String APP_ID = "AL";
	private static final String NA_KEY = "TestNaKey";
	private static final String RMID = "TestRmid";
	private static final String RMID_UNMATCHED = "TestRmid2";
	private static final String ID_NO = "TestIdNo";
	private static final String AC_NO = "TestAcNo";

	private static final String NAME_1 = "TestName1";
	private static final String NAME_2 = "TestName2";
	private static final String NAME_3 = "TestName3";
	private static final String NAME_4 = "TestName4";
	private static final String FULL_NAME = "TestFullName";

	private static final String ADDR_1 = "TestAddr1";
	private static final String ADDR_2 = "TestAddr2";
	private static final String ADDR_3 = "TestAddr3";
	private static final String ADDR_4 = "TestAddr4";
	private static final String ADDR_5 = "TestAddr5";
	private static final String ADDR_6 = "TestAddr6";

	private static final String CITY = "TestCity";

	@Configuration
	@Profile(ProfileNames.TEST)
	public static class TestTokenMailingAddressContext {
		@Bean
		public HostService forTR08() {
			return new HostService() {
				@SuppressWarnings("unchecked")
				@Override
				public <I, O> O send(I in, Class<O> clsOut) {
					return (O) new TR08Out() {
						{
							setAcctNoInfos(Arrays.asList());
						}
					};
				}
			};
		}
	}

	@Before
	public void setUp() throws IOException {
		setUpFile(TokenMailingAddress.FILE_CHI, new ConcurrentHashMap<String, String>() {
			private static final long serialVersionUID = 1L;

			{
				put("appId", APP_ID);
				put("naKey", NA_KEY);
				put("name1", NAME_1);
				put("name2", NAME_2);
				put("addr1", ADDR_1);
				put("addr2", ADDR_2);
				put("addr3", ADDR_3);
				put("addr4", ADDR_4);
			}
		});
		setUpFile(TokenMailingAddress.FILE_CF, new ConcurrentHashMap<String, String>() {
			private static final long serialVersionUID = 1L;

			{
				put("rmid", f(RMID, 15));
				put("name1", f(NAME_1, 40));
				put("name2", f(NAME_2, 40));
				put("addr1", f(ADDR_1, 40));
				put("addr2", f(ADDR_2, 40));
				put("addr3", f(ADDR_3, 40));
				put("addr4", f(ADDR_4, 40));
			}
		});
		setUpFile(TokenMailingAddress.FILE_CC, new ConcurrentHashMap<String, String>() {
			private static final long serialVersionUID = 1L;

			{
				put("idNo", f(ID_NO, 19));
				put("acNo", f(AC_NO, 16));
				put("fullName", f(FULL_NAME, 30));
				put("addr1", f(ADDR_1, 30));
				put("addr2", f(ADDR_2, 30));
				put("addr3", f(ADDR_3, 30));
				put("addr4", f(ADDR_4, 30));

			}
		});
		setUpFile(TokenMailingAddress.FILE_IM, new ConcurrentHashMap<String, String>() {
			private static final long serialVersionUID = 1L;

			{
				put("acNo", f(AC_NO, 17));
				put("name1", f(NAME_1, 40));
				put("name2", f(NAME_2, 40));
				put("name3", f(NAME_3, 40));
				put("name4", f(NAME_4, 40));
				put("addr1", f(ADDR_1, 40));
				put("addr2", f(ADDR_2, 40));
				put("addr3", f(ADDR_3, 40));
				put("addr4", f(ADDR_4, 40));
				put("addr5", f(ADDR_5, 40));
				put("addr6", f(ADDR_6, 40));
				put("city", f(CITY, 30));
			}
		});
		setUpFile(TokenMailingAddress.FILE_ST, new ConcurrentHashMap<String, String>() {
			private static final long serialVersionUID = 1L;

			{
				put("acNo", f(AC_NO, 20));
				put("name1", f(NAME_1, 40));
				put("name2", f(NAME_2, 40));
				put("name3", f(NAME_3, 40));
				put("name4", f(NAME_4, 40));
				put("addr1", f(ADDR_1, 40));
				put("addr2", f(ADDR_2, 40));
				put("addr3", f(ADDR_3, 40));
				put("addr4", f(ADDR_4, 40));
				put("addr5", f(ADDR_5, 40));
				put("addr6", f(ADDR_6, 40));
				put("city", f(CITY, 30));
			}
		});

		setUpFile(TokenMailingAddress.FILE_RMID, new ConcurrentHashMap<String, String>() {
			private static final long serialVersionUID = 1L;

			{
				put("rmid1", RMID);
				put("rmid2", RMID_UNMATCHED);
			}
		});

		fileUtil.clean(TokenMailingAddress.FTP_FOLDER + File.separator + TokenMailingAddress.REPORT_SUCCESS);
		fileUtil.clean(TokenMailingAddress.FTP_FOLDER + File.separator + TokenMailingAddress.REPORT_UNMATCHED);
	}

	private void setUpFile(String file, Map<String, String> valuesMap) throws IOException {
		fileUtil.clean(file);
		String resolvedString = new StrSubstitutor(valuesMap).replace(filesService.readResource(FILE_PATH + file));
		fileUtil.writeFile(resolvedString, file);
	}

	@Test
	public void test() throws Exception {
		JobParameters params = new JobParametersBuilder().addString(JobConvention.JOB_PARAMETER_START_TIME, now().string()).addLong("RandomForTest", System.currentTimeMillis()).toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);
		assertThat("ExitCode", jobExecution.getExitStatus().getExitCode(), equalTo("COMPLETED"));
		matchCHI();
		matchCF();
		matchCC();
		matchIM();
		matchST();

		matchRET();

		matchReport();
	}

	private void matchReport() throws IOException {
		assertThat("Success Report", fileUtil.getFile(TokenMailingAddress.FTP_FOLDER + File.separator + TokenMailingAddress.REPORT_SUCCESS), notNullValue());
		assertThat("Success Report Length", fileUtil.readFile(TokenMailingAddress.FTP_FOLDER + File.separator + TokenMailingAddress.REPORT_SUCCESS), hasSize(2));
		assertThat("Failed Report", fileUtil.getFile(TokenMailingAddress.FTP_FOLDER + File.separator + TokenMailingAddress.REPORT_UNMATCHED), notNullValue());
		assertThat("Failed Report Length", fileUtil.readFile(TokenMailingAddress.FTP_FOLDER + File.separator + TokenMailingAddress.REPORT_UNMATCHED), hasSize(2));
	}

	private void matchCHI() {
		Record r = select(TokenMailingAddress.TABLE_CHI);
		assertThat("appId", r.getValue("APP_ID", String.class), equalTo(APP_ID));
		assertThat("naKey", r.getValue("NA_KEY", String.class), equalTo(NA_KEY));
		assertThat("name1", r.getValue("NAME_1", String.class), equalTo(NAME_1));
		assertThat("name2", r.getValue("NAME_2", String.class), equalTo(NAME_2));
		assertThat("addr1", r.getValue("ADDR_1", String.class), equalTo(ADDR_1));
		assertThat("addr2", r.getValue("ADDR_2", String.class), equalTo(ADDR_2));
		assertThat("addr3", r.getValue("ADDR_3", String.class), equalTo(ADDR_3));
		assertThat("addr4", r.getValue("ADDR_4", String.class), equalTo(ADDR_4));
	}

	private void matchCF() {
		Record r = select(TokenMailingAddress.TABLE_CF);
		assertThat("rmid", r.getValue("RMID", String.class), equalTo(RMID));
		assertThat("name1", r.getValue("NAME_1", String.class), equalTo(NAME_1));
		assertThat("name2", r.getValue("NAME_2", String.class), equalTo(NAME_2));
		assertThat("addr1", r.getValue("ADDR_1", String.class), equalTo(ADDR_1));
		assertThat("addr2", r.getValue("ADDR_2", String.class), equalTo(ADDR_2));
		assertThat("addr3", r.getValue("ADDR_3", String.class), equalTo(ADDR_3));
		assertThat("addr4", r.getValue("ADDR_4", String.class), equalTo(ADDR_4));
	}

	private void matchCC() {
		Record r = select(TokenMailingAddress.TABLE_CC);
		assertThat("idNo", r.getValue("ID_NO", String.class), equalTo(ID_NO));
		assertThat("acNo", r.getValue("AC_NO", String.class), equalTo(AC_NO));
		assertThat("fullName", r.getValue("FULL_NAME", String.class), equalTo(FULL_NAME));
		assertThat("addr1", r.getValue("ADDR_1", String.class), equalTo(ADDR_1));
		assertThat("addr2", r.getValue("ADDR_2", String.class), equalTo(ADDR_2));
		assertThat("addr3", r.getValue("ADDR_3", String.class), equalTo(ADDR_3));
		assertThat("addr4", r.getValue("ADDR_4", String.class), equalTo(ADDR_4));
	}

	private void matchIM() {
		Record r = select(TokenMailingAddress.TABLE_IM);
		assertThat("acNo", r.getValue("AC_NO", String.class), equalTo(AC_NO));
		assertThat("name1", r.getValue("NAME_1", String.class), equalTo(NAME_1));
		assertThat("name2", r.getValue("NAME_2", String.class), equalTo(NAME_2));
		assertThat("name3", r.getValue("NAME_3", String.class), equalTo(NAME_3));
		assertThat("name4", r.getValue("NAME_4", String.class), equalTo(NAME_4));
		assertThat("addr1", r.getValue("ADDR_1", String.class), equalTo(ADDR_1));
		assertThat("addr2", r.getValue("ADDR_2", String.class), equalTo(ADDR_2));
		assertThat("addr3", r.getValue("ADDR_3", String.class), equalTo(ADDR_3));
		assertThat("addr4", r.getValue("ADDR_4", String.class), equalTo(ADDR_4));
		assertThat("addr5", r.getValue("ADDR_5", String.class), equalTo(ADDR_5));
		assertThat("addr6", r.getValue("ADDR_6", String.class), equalTo(ADDR_6));
		assertThat("city", r.getValue("CITY", String.class), equalTo(CITY));
	}

	private void matchST() {
		Record r = select(TokenMailingAddress.TABLE_ST);
		assertThat("acNo", r.getValue("AC_NO", String.class), equalTo(AC_NO));
		assertThat("name1", r.getValue("NAME_1", String.class), equalTo(NAME_1));
		assertThat("name2", r.getValue("NAME_2", String.class), equalTo(NAME_2));
		assertThat("name3", r.getValue("NAME_3", String.class), equalTo(NAME_3));
		assertThat("name4", r.getValue("NAME_4", String.class), equalTo(NAME_4));
		assertThat("addr1", r.getValue("ADDR_1", String.class), equalTo(ADDR_1));
		assertThat("addr2", r.getValue("ADDR_2", String.class), equalTo(ADDR_2));
		assertThat("addr3", r.getValue("ADDR_3", String.class), equalTo(ADDR_3));
		assertThat("addr4", r.getValue("ADDR_4", String.class), equalTo(ADDR_4));
		assertThat("addr5", r.getValue("ADDR_5", String.class), equalTo(ADDR_5));
		assertThat("addr6", r.getValue("ADDR_6", String.class), equalTo(ADDR_6));
		assertThat("city", r.getValue("CITY", String.class), equalTo(CITY));
	}

	private void matchRET() {
		Result<Record> rets = sql.select().from(TokenMailingAddress.TABLE_RET).orderBy(DSL.field("RMID")).fetch();
		Record ret0 = rets.get(0);

		assertThat("rmid", ret0.getValue("RMID", String.class), equalTo(RMID));
		assertThat("acNo", ret0.getValue("AC_NO", String.class), nullValue());
		assertThat("category", ret0.getValue("CATEGORY", String.class), equalTo("CF"));
		assertThat("name", ret0.getValue("NAME", String.class), equalTo(NAME_1 + " " + NAME_2));
		assertThat("addr", ret0.getValue("ADDR", String.class), equalTo(ADDR_1 + " " + ADDR_2 + " " + ADDR_3 + " " + ADDR_4));
		assertThat("ret", ret0.getValue("RET", String.class), equalTo("Y"));

		Record ret1 = rets.get(1);

		assertThat("rmid", ret1.getValue("RMID", String.class), equalTo(RMID_UNMATCHED));
		assertThat("acNo", ret1.getValue("AC_NO", String.class), nullValue());
		assertThat("category", ret1.getValue("CATEGORY", String.class), nullValue());
		assertThat("name", ret1.getValue("NAME", String.class), nullValue());
		assertThat("addr", ret1.getValue("ADDR", String.class), nullValue());
		assertThat("ret", ret1.getValue("RET", String.class), equalTo("N"));
	}

	@After
	public void clear() {
		cleanTable(TokenMailingAddress.TABLE_CHI);
		cleanTable(TokenMailingAddress.TABLE_CF);
		cleanTable(TokenMailingAddress.TABLE_CC);
		cleanTable(TokenMailingAddress.TABLE_IM);
		cleanTable(TokenMailingAddress.TABLE_ST);
		cleanTable(TokenMailingAddress.TABLE_RET);
		fileUtil.clean(TokenMailingAddress.FILE_CHI);
		fileUtil.clean(TokenMailingAddress.FILE_CF);
		fileUtil.clean(TokenMailingAddress.FILE_CC);
		fileUtil.clean(TokenMailingAddress.FILE_IM);
		fileUtil.clean(TokenMailingAddress.FILE_ST);
		fileUtil.clean(TokenMailingAddress.FILE_RMID);
		fileUtil.clean(TokenMailingAddress.FTP_FOLDER + File.separator + TokenMailingAddress.REPORT_SUCCESS);
		fileUtil.clean(TokenMailingAddress.FTP_FOLDER + File.separator + TokenMailingAddress.REPORT_UNMATCHED);
	}

	private void cleanTable(String tableName) {
		sql.delete(DSL.table(tableName)).execute();
	}

	private Record select(String tableName) {
		return sql.select().from(tableName).fetch().get(0);
	}

	// fixed-length right space padding
	private static String f(String string, int length) {
		return String.format("%1$-" + length + "s", string);
	}
}
