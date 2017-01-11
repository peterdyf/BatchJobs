package net.peter.batch.jobs.aossUpdateName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.MssqlDbConfig4Test;
import net.peter.test.batch.TestConfigs;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, MssqlDbConfig4Test.class, AossUpdateName.class, SpGetNameAddr.class, SpPbUserUpdName.class })
@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
public class TestAossUpdateName {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	DSLContext sql;

	private static final String RMID = "HKIH9732774";
	private static final String UPDATED_BY = "PETER-TAM";

	private String id;
	private String oldNm, oldTcNm, oldScNm;

	@Before
	public void setUp() {
		insert(RMID);
		id = select(RMID, UPDATED_BY);
		Record rec = selectPbUser(RMID);
		oldNm = rec.getValue(DSL.field("NM", String.class));
		oldTcNm = rec.getValue(DSL.field("TC_NM", String.class));
		oldScNm = rec.getValue(DSL.field("SC_NM", String.class));
	}

	@Test
	public void test() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertThat("ExitCode", jobExecution.getExitStatus().getExitCode(), equalTo("COMPLETED"));
		assertThat("nm", selectPbUser(RMID).getValue(DSL.field("NM", String.class)), equalTo("HZ HZZ UZZH"));
		assertThat("tc_nm", selectPbUser(RMID).getValue(DSL.field("TC_NM", String.class)), equalTo("田心中"));
		assertThat("sc_nm", selectPbUser(RMID).getValue(DSL.field("SC_NM", String.class)), equalTo("田心中"));
		assertThat("executed", selectExecute(id), equalTo("Y"));
	}

	@After
	public void clear() {
		delete(id);
		rollback(RMID, oldNm, oldTcNm, oldScNm);
	}

	@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.UnusedPrivateMethod" })
	private void delete(String id) {
		sql.delete(DSL.table("TBL_ESB_AOSS_NAME_FLAG")).where("ID = ?", id).execute();
	}

	private void insert(String rmid) {
		// @formatter:off
		sql.insertInto(DSL.table("TBL_ESB_AOSS_NAME_FLAG")).set(DSL.field("RMID", String.class), rmid)
				.set(DSL.field("NAME_FLAG", String.class), "X").set(DSL.field("UPDATED_BY", String.class), UPDATED_BY)
				.execute();
		// @formatter:on
	}

	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	private void rollback(String rmid, String oldNm, String oldTcNm, String oldScNm) {
		sql.update(DSL.table("TBL_PB_USER")).set(DSL.field("NM", String.class), oldNm).set(DSL.field("TC_NM", String.class), oldTcNm).set(DSL.field("SC_NM", String.class), oldScNm)
				.where("ID = ?", getPbUserId(rmid)).execute();

	}

	private String select(String rmid, String updatedBy) {
		return sql.select().from("TBL_ESB_AOSS_NAME_FLAG").where("RMID = ?", rmid).and("UPDATED_BY =?", updatedBy).fetchOne(DSL.field("ID", String.class));
	}

	private String selectExecute(String id) {
		return sql.select().from("TBL_ESB_AOSS_NAME_FLAG").where("ID = ?", id).fetchOne(DSL.field("EXECUTED", String.class));
	}

	private Record selectPbUser(String rmid) {
		return sql.select().from("TBL_PB_USER").where("ID = ?", getPbUserId(rmid)).and("IS_CLS = 0").fetchOne();
	}

	private String getPbUserId(String rmid) {
		return sql.select().from("TBL_PB_USER").where("RMID = ?", rmid).and("IS_CLS = 0").fetchOne(DSL.field("ID", String.class));
	}

}
