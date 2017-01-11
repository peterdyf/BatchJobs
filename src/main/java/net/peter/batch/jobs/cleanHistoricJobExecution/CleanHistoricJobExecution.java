package net.peter.batch.jobs.cleanHistoricJobExecution;

import static net.peter.batch.common.writer.CompositeItemWriterPartial.multi;

import net.peter.batch.common.writer.MapWrapperWriterPartial;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import net.peter.batch.annotation.JobConfiguration;
import net.peter.batch.annotation.StepBean;
import net.peter.batch.build.AbstractJob;
import net.peter.batch.common.reader.SqlSourceJdbcReader;
import net.peter.batch.common.writer.SqlSourceJdbcWriter;

@JobConfiguration
public class CleanHistoricJobExecution extends AbstractJob {

	static final int LIMITED_DAY = 7;
	private static final String JOB_NAME = "CleanHistoricJobExecution";
	private static final String STEP_NAME = "ReadAndDelete";
	private static final int CHUNK_SIZE = 1000;
	private static final String SELECT = "select job_execution_id, job_instance_id from BATCH_JOB_EXECUTION where create_time<sysdate-" + LIMITED_DAY;
	private static final String DETETE_STEP_EXECUTION_CONTEXT = "delete from BATCH_JOB_EXECUTION_CONTEXT where job_execution_id= :id";
	private static final String DETETE_EXECUTION_CONTEXT = "delete from BATCH_STEP_EXECUTION_CONTEXT where STEP_EXECUTION_ID in (select step_execution_id from BATCH_STEP_EXECUTION where job_execution_id= :id)";
	private static final String DETETE_STEP_EXECUTION = "delete from BATCH_STEP_EXECUTION where job_execution_id= :id";
	private static final String DETETE_EXECUTION_PARAMS = "delete from BATCH_JOB_EXECUTION_PARAMS where job_execution_id= :id";
	private static final String DETETE_EXECUTION = "delete from BATCH_JOB_EXECUTION where job_execution_id= :id";
	private static final String DETETE_INSTANCE = "delete from BATCH_JOB_INSTANCE where job_instance_id= :id";

	@Override
	public String getJobName() {
		return JOB_NAME;
	}

	@Bean
	protected Job buildJob() {
		return jobBuilder().start(step()).build();
	}

	@Bean
	Step step() {
		//@formatter:off
		return stepBuilder(STEP_NAME)
				.<HistoricJobExecutionDbData, HistoricJobExecutionDbData> chunk(CHUNK_SIZE)	
				.reader(reader())
				.writer(
						multi(
							MapWrapperWriterPartial.map(HistoricJobExecutionDbData::getJobExecutionId).wrapper(multi(
								deleteStepExecutionContext(),
								deleteExecutionContext(),
								deleteStepExecution(),
								deleteExecutionParams(),
								deleteExecution()
							)),
							MapWrapperWriterPartial.map(HistoricJobExecutionDbData::getJobInstanceId).wrapper(deleteInstance())
						)
				)
				.build();
		//@formatter:on
	}

	@StepBean
	ItemStreamReader<HistoricJobExecutionDbData> reader() {
		return autowire(new SqlSourceJdbcReader<>(SELECT, HistoricJobExecutionDbData.class));
	}

	@StepBean
	ItemWriter<Long> deleteStepExecutionContext() {
		return autowire(new SqlSourceJdbcWriter<Long>(DETETE_STEP_EXECUTION_CONTEXT) {
			{
				setItemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id));
			}
		});
	}

	@StepBean
	ItemWriter<Long> deleteExecutionContext() {
		return autowire(new SqlSourceJdbcWriter<Long>(DETETE_EXECUTION_CONTEXT) {
			{
				setItemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id));
			}
		});
	}

	@StepBean
	ItemWriter<Long> deleteStepExecution() {
		return autowire(new SqlSourceJdbcWriter<Long>(DETETE_STEP_EXECUTION) {
			{
				setItemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id));
			}
		});
	}

	@StepBean
	ItemWriter<Long> deleteExecutionParams() {
		return autowire(new SqlSourceJdbcWriter<Long>(DETETE_EXECUTION_PARAMS) {
			{
				setItemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id));
			}
		});
	}

	@StepBean
	ItemWriter<Long> deleteExecution() {
		return autowire(new SqlSourceJdbcWriter<Long>(DETETE_EXECUTION) {
			{
				setItemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id));
			}
		});
	}

	@StepBean
	ItemWriter<Long> deleteInstance() {
		return autowire(new SqlSourceJdbcWriter<Long>(DETETE_INSTANCE) {
			{
				setItemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id));
			}
		});
	}
}
