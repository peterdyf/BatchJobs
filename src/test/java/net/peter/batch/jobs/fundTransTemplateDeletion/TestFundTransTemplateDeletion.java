package net.peter.batch.jobs.fundTransTemplateDeletion;

import static com.cncbinternational.common.util.MyDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.math.BigDecimal;
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

import com.cncbinternational.common.service.SysParamService;
import com.cncbinternational.common.storeprocedure.SpAuditLogInsert;
import com.cncbinternational.common.util.MyDate;
import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.batch.TestConfigs;
import net.peter.test.batch.TestConfigs4Ftp;
import net.peter.test.batch.TestFileUtilFTP;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, FundTransTemplateDeletion.class, SpAuditLogInsert.class, TestConfigs4Ftp.class })
@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
public class TestFundTransTemplateDeletion {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private SysParamService sysParam;

	@Autowired
	private DSLContext sql;

	@Autowired
	TestFileUtilFTP fileUtil;

	private static final String TEMPLATE_NAME = "testTemplateName_" + System.currentTimeMillis();
	private static final String TEMPLATE_NAME_2 = "testTemplateName2_" + System.currentTimeMillis();

	private static final int DATA_DAY = 100 * 365;
	private static final MyDate START_DATE = now().minusDays(DATA_DAY);
	private static final String CSV_FILE_NAME = FundTransTemplateDeletion.FTP_FOLDER + File.separator + "FundTransTemplateDeletion.csv";

	@Before
	public void setUp() {
		fileUtil.clean(CSV_FILE_NAME);

		int duration = sysParam.getInt("FT_TMP_DURATION");

		insert("ECAE1298E2857257E03400306E4A7AD5", TEMPLATE_NAME, duration);
		insert("ECAE4AD62D757259E03400306EC323B2", TEMPLATE_NAME_2, duration);
	}

	@Test
	public void test() throws Exception {

		JobParameters params = new JobParametersBuilder().addString(JobConvention.JOB_PARAMETER_START_TIME, START_DATE.string()).addLong("RandomForTest", System.currentTimeMillis()).toJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);
		assertThat("ExitCode", jobExecution.getExitStatus().getExitCode(), equalTo("COMPLETED"));

		assertThat("Audit Record 1", selectAudit(TEMPLATE_NAME), equalTo(1));
		assertThat("Audit Record 2", selectAudit(TEMPLATE_NAME_2), equalTo(1));

		BigDecimal isDel = BigDecimal.ONE;
		assertThat("Record 1 Deletion", select(TEMPLATE_NAME), equalTo(isDel));
		assertThat("Record 2 Deletion", select(TEMPLATE_NAME_2), equalTo(isDel));

		assertThat("CSV File", fileUtil.getFile(CSV_FILE_NAME), notNullValue());
		assertThat("CSV File", fileUtil.readFile(CSV_FILE_NAME), hasSize(3));
	}

	@After
	public void clear() {
		assertThat("Delete template 1", delete(TEMPLATE_NAME), equalTo(1));
		assertThat("Delete template 2", delete(TEMPLATE_NAME_2), equalTo(1));

		assertThat("Delete Audit Record 1", deleteAudit(TEMPLATE_NAME), equalTo(1));
		assertThat("Delete Audit Record 2", deleteAudit(TEMPLATE_NAME_2), equalTo(1));

		assertThat("Clean CSV", fileUtil.clean(CSV_FILE_NAME), equalTo(1));
	}

	private void insert(String pbUserId, String templateName, int duration) {
		//@formatter:off
		sql.insertInto(DSL.table("TBL_PB_FUND_TXFER_TMP"))
			.set(DSL.field("PB_USER_ID", String.class), pbUserId)
			.set(DSL.field("TEMPLATE_NM", String.class), templateName)
			.set(DSL.field("IS_DEL", Integer.class), 0)
			.set(DSL.field("DT_CREATED", Date.class), now().sql())
			.set(DSL.field("TO_ACCT_NO",String.class),"123123123123123")
			.set(DSL.field("FR_PB_ACCT_ID",String.class),"414C9FDA16E05B75E044001A4B07128C")
			.set(DSL.field("FR_IB_CURR_ID",String.class),"D4B5BD3A11907363E03400306EC38365")
			.set(DSL.field("TO_PB_ACCT_ID",String.class),"414C9FDA16E05B75E044001A4B07128C")
			.set(DSL.field("TO_IB_CURR_ID",String.class),"D4B5BD3A11907363E03400306EC38365")
			.set(DSL.field("TXFER_IB_CURR_ID",String.class),"D4B5BD3A11907363E03400306EC38365")
			.set(DSL.field("DT_LST_TRANS",Date.class), now().minusDays(DATA_DAY + duration).sql())
			.execute();
		//@formatter:on
	}

	private BigDecimal select(String templateName) {
		return sql.select().from("TBL_PB_FUND_TXFER_TMP").where("TEMPLATE_NM = ?", templateName).fetchOne(DSL.field("IS_DEL", BigDecimal.class));
	}

	private int selectAudit(String templateName) {
		return sql.selectCount().from("TBL_AUDIT_LOG_TEMP").where("PRE_IMG like ?", "%" + templateName + "%").fetchOne(0, int.class);
	}

	private int delete(String templateName) {
		return sql.delete(DSL.table("TBL_PB_FUND_TXFER_TMP")).where("TEMPLATE_NM = ?", templateName).execute();
	}

	private int deleteAudit(String templateName) {
		return sql.delete(DSL.table("TBL_AUDIT_LOG_TEMP")).where("PRE_IMG like ?", "%" + templateName + "%").execute();
	}
}
