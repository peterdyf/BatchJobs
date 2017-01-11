package net.peter.batch.jobs.futureTimeDeposit;

import static com.cncbinternational.common.util.MyDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.peter.batch.jobs.futureTimeDeposit.TestFutureTimeDepsoitPlaceOrder.TestFutureTimeDepsoitContext;
import com.cncbinternational.common.service.host.HostService;
import com.cncbinternational.common.service.host.ME07Service;
import com.cncbinternational.common.service.host.message.ME07Out;
import com.cncbinternational.spring.annotation.Loggable;
import com.cncbinternational.spring.config.LogConfig;
import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.batch.TestConfigs;
import net.peter.test.batch.TestConfigs4Host;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, FutureTimeDepositPlaceOrder.class, ME07Service.class, TestConfigs4Host.class, TestFutureTimeDepsoitContext.class, LogConfig.class,
		SpTblPbRMNoIns.class, FutureTimeDepositService.class })
@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
public class TestFutureTimeDepsoitPlaceOrder {

	private static final String HOST_TRX_NO = "TD9999999999";

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	DSLContext sql;

	private static final String RMID = "HKID4897324";
	private static final String rmNumberIn_part1 = "0018000000000";
	private static String rmNumberIn_part2 = getCurrentTimeStamp();
	private static String rmNumberIn = rmNumberIn_part1 + rmNumberIn_part2;

	@Configuration
	@Profile(ProfileNames.TEST)
	public static class TestFutureTimeDepsoitContext {

		public static class HostService4TestFutureTimeDepsoit implements HostService {
			@SuppressWarnings("unchecked")
			@Override
			public <I, O> O send(I in, Class<O> clsOut) {
				return (O) new ME07Out() {
					{
						setRmNumber(rmNumberIn);
						setReturnCode("00");
					}
				};
			}
		}

		@Bean
		public HostService me07Service() {
			return new HostService4TestFutureTimeDepsoit();
		}
	}

	private String pbUserIdIn = "";

	@Before
	public void setUp() {
		pbUserIdIn = getPbUserId(RMID);
		try {
			insert(pbUserIdIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertThat("ExitCode", jobExecution.getExitStatus().getExitCode(), equalTo("COMPLETED"));
		assertThat("[RM Number]", select(pbUserIdIn, rmNumberIn), equalTo(rmNumberIn));
	}

	@After
	public void clear() {
		assertThat("Delete TBL_PB_RMNO", delete(pbUserIdIn, rmNumberIn), equalTo(1));
		assertThat("Delete TBL_PB_FD_PLCMNT", delete(pbUserIdIn, HOST_TRX_NO, ""), equalTo(1));
		assertThat("[RM Number]", select(pbUserIdIn, rmNumberIn), isEmptyOrNullString());
	}

	@Loggable
	private String select(String pbUserId, String rmNumber) {
		return sql.select().from("TBL_PB_RMNO").where("PB_USER_ID = ?", pbUserId).and("RM_NO = ?", rmNumber).fetchOne(DSL.field("RM_NO", String.class));
	}

	private String getPbUserId(String rmid) {
		return sql.select().from("TBL_PB_USER").where("RMID = ?", rmid).and("IS_CLS = 0").fetchOne(DSL.field("ID", String.class));
	}

	private static String getCurrentTimeStamp() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}

	@Loggable
	private void insert(String pbUserId) throws Exception {
		//@formatter:off
		sql.insertInto(DSL.table("tbl_pb_fd_plcmnt"))
			.set(DSL.field("PB_USER_ID", String.class), pbUserId)	
			.set(DSL.field("IB_TRX_NO", String.class), HOST_TRX_NO)
			.set(DSL.field("PARAM_TENURE_CD", String.class), "007D")
			.set(DSL.field("DEBIT_AMT",Integer.class), 1)
			.set(DSL.field("TRX_AMT", Integer.class), 1)
			.set(DSL.field("LOCAL_AMT", Integer.class), 1)
			.set(DSL.field("CHARGE_IB_CURR_ID", String.class), "D4B5BD3A11987363E03400306EC38365")
			.set(DSL.field("CHARGE_AMT", Integer.class), 1)
			.set(DSL.field("DT_REQUEST", java.sql.Date.class), now().sql())
			.set(DSL.field("SYS_CONST_MAT_INSTR_CD", String.class), "1")
			.set(DSL.field("IS_NOTIFIED", Integer.class), 0)
			.set(DSL.field("SYS_CONST_TRX_STATUS_CD", String.class), "AF")
			.set(DSL.field("HOST_TRX_CD", String.class), "00")
			.set(DSL.field("DT_CREATED", java.sql.Date.class), now().sql())
			.set(DSL.field("VERSION", String.class), "9999999999")
			.set(DSL.field("CHANNEL_CD", String.class), "PIB")
			.execute();
		//@formatter:on
	}

	private int delete(String pbUserId, String rmNumber) {
		return sql.delete(DSL.table("TBL_PB_RMNO")).where("PB_USER_ID = ?", pbUserId).and("RM_NO = ?", rmNumber).execute();
	}

	private int delete(String pbUserIdIn, String hostTrxNo, String dummy) {
		return sql.delete(DSL.table("tbl_pb_fd_plcmnt")).where("PB_USER_ID = ?", pbUserIdIn).and("IB_TRX_NO = ?", hostTrxNo).execute();
	}

}
