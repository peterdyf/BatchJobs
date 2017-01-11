package net.peter.batch.jobs.cleanHistoricJobExecution;

import static com.cncbinternational.common.util.MyDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.sql.Date;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.DefaultJobKeyGenerator;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cncbinternational.spring.constant.ProfileNames;
import net.peter.test.batch.TestConfigs;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigs.class, CleanHistoricJobExecution.class })
@ActiveProfiles({ ProfileNames.TEST, ProfileNames.CONFIG_DEV })
@SuppressWarnings({ "PMD.UnusedImports", "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public class TestCleanHistoricJobExecution {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	DSLContext sql;

	private Long instanceId;
	private Long executionId;

	@Before
	public void setUp() {
		instanceId = insertInstance();
		executionId = insertExecution();
	}

	@Test
	public void test() throws Exception {

		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertThat("ExitCode", jobExecution.getExitStatus().getExitCode(), equalTo("COMPLETED"));
		assertThat("BATCH_JOB_EXECUTION_CONTEXT", selectStepExecutionContext(executionId), equalTo(0));
		assertThat("BATCH_STEP_EXECUTION_CONTEXT", selectExecutionContext(executionId), equalTo(0));
		assertThat("BATCH_STEP_EXECUTION", selectStepExecution(executionId), equalTo(0));
		assertThat("BATCH_JOB_EXECUTION_PARAMS", selectExecutionParams(executionId), equalTo(0));
		assertThat("BATCH_JOB_EXECUTION", selectExecution(executionId), equalTo(0));
		assertThat("BATCH_JOB_INSTANCE", selectInstance(instanceId), equalTo(0));
	}

	@After
	public void clear() {
		delete(executionId);
	}

	@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.UnusedPrivateMethod" })
	private void delete(Long id) {
		sql.delete(DSL.table("BATCH_JOB_EXECUTION_CONTEXT")).where("job_execution_id = ?", id).execute();
		sql.delete(DSL.table("BATCH_STEP_EXECUTION_CONTEXT")).where("STEP_EXECUTION_ID = (select step_execution_id from BATCH_STEP_EXECUTION where job_execution_id= ?)", id).execute();
		sql.delete(DSL.table("BATCH_STEP_EXECUTION")).where("job_execution_id = ?", id).execute();
		sql.delete(DSL.table("BATCH_JOB_EXECUTION_PARAMS")).where("job_execution_id = ?", id).execute();
		sql.delete(DSL.table("BATCH_JOB_EXECUTION")).where("job_execution_id = ?", id).execute();
	}

	private Long insertInstance() {

		Long instanceId = sql.select(DSL.field("BATCH_JOB_SEQ.nextval", Long.class)).from("dual").fetchOneInto(Long.class);

		// @formatter:off
		sql.insertInto(DSL.table("BATCH_JOB_INSTANCE"))
			.set(DSL.field("JOB_INSTANCE_ID", Long.class), instanceId)
			.set(DSL.field("JOB_NAME", String.class), "testJob")
			.set(DSL.field("JOB_KEY", String.class), new DefaultJobKeyGenerator().generateKey(new JobParametersBuilder().addLong("test", System.currentTimeMillis()).toJobParameters()))
			.set(DSL.field("VERSION", Integer.class), 0)
			.execute();
		// @formatter:on
		return instanceId;
	}

	private Long insertExecution() {

		Long executionId = sql.select(DSL.field("BATCH_JOB_EXECUTION_SEQ.nextval", Long.class)).from("dual").fetchOneInto(Long.class);

		// @formatter:off
		
		sql.insertInto(DSL.table("BATCH_JOB_EXECUTION"))
				.set(DSL.field("JOB_EXECUTION_ID", Long.class), executionId)
				.set(DSL.field("JOB_INSTANCE_ID", Long.class), instanceId)
				.set(DSL.field("CREATE_TIME", Date.class), now().minusDays(CleanHistoricJobExecution.LIMITED_DAY + 1).sql())
				.execute();
		
		sql.insertInto(DSL.table("BATCH_JOB_EXECUTION_PARAMS"))
				.set(DSL.field("JOB_EXECUTION_ID", Long.class), executionId)
				.set(DSL.field("TYPE_CD", String.class), "test")
				.set(DSL.field("KEY_NAME", String.class), "test")
				.set(DSL.field("STRING_VAL", String.class), "test")
				.set(DSL.field("IDENTIFYING", String.class), "N")
				.execute();
		
		Long stepExecutionId= sql.select(DSL.field("BATCH_STEP_EXECUTION_SEQ.nextval", Long.class)).from("dual").fetchOneInto(Long.class);
		
		sql.insertInto(DSL.table("BATCH_STEP_EXECUTION"))
				.set(DSL.field("STEP_EXECUTION_ID", Long.class), stepExecutionId)
				.set(DSL.field("VERSION", Long.class), 0L)
				.set(DSL.field("JOB_EXECUTION_ID", Long.class), executionId)
				.set(DSL.field("STEP_NAME", String.class), "test")
				.set(DSL.field("START_TIME", Date.class), now().sql())
				.execute();
		// @formatter:on

		return executionId;
	}

	private int selectStepExecutionContext(Long id) {
		return sql.selectCount().from("BATCH_JOB_EXECUTION_CONTEXT").where("job_execution_id = ?", id).fetchOne(0, int.class);
	}

	private int selectExecutionContext(Long id) {
		return sql.selectCount().from("BATCH_STEP_EXECUTION_CONTEXT").where("STEP_EXECUTION_ID = (select step_execution_id from BATCH_STEP_EXECUTION where job_execution_id= ?)", id).fetchOne(0,
				int.class);
	}

	private int selectStepExecution(Long id) {
		return sql.selectCount().from("BATCH_STEP_EXECUTION").where("job_execution_id = ?", id).fetchOne(0, int.class);
	}

	private int selectExecutionParams(Long id) {
		return sql.selectCount().from("BATCH_JOB_EXECUTION_PARAMS").where("job_execution_id = ?", id).fetchOne(0, int.class);
	}

	private int selectExecution(Long id) {
		return sql.selectCount().from("BATCH_JOB_EXECUTION").where("job_execution_id = ?", id).fetchOne(0, int.class);
	}

	private int selectInstance(Long id) {
		return sql.selectCount().from("BATCH_JOB_INSTANCE").where("job_instance_id = ?", id).fetchOne(0, int.class);
	}

}
