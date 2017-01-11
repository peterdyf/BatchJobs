package net.peter.batch.jobs.aossUpdateName;

import static net.peter.batch.common.writer.CompositeItemWriterPartial.multi;

import java.util.function.Function;

import net.peter.batch.common.writer.ForEachItemWriter;
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
import net.peter.batch.common.reader.FileSourceJdbcReader;
import net.peter.batch.common.writer.FileSourceJdbcWriter;
import net.peter.batch.jobs.aossUpdateName.SpGetNameAddr.NameAddr;
import net.peter.batch.jobs.aossUpdateName.SpPbUserUpdName.UpdNameParam;

@JobConfiguration
public class AossUpdateName extends AbstractJob {

	private static final String JOB_NAME = "aossUpdateName";
	private static final String STEP_NAME = "ReadAndCopy";
	private static final int CHUNK_SIZE = 1000;
	private static final String SELECT_SQL = "classpath:com/cncbinternational/batch/jobs/aossUpdateName/AossNameFlagCopySelect.sql";
	private static final String UPDATE_SQL = "classpath:com/cncbinternational/batch/jobs/aossUpdateName/AossNameFlagCopyUpdate.sql";

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
				.<AossNameFlagDbData, AossNameFlagDbData> chunk(CHUNK_SIZE)	
				.reader(reader())
				.writer(multi(
						MapWrapperWriterPartial.map(chiNameMapper(null)).wrapper(updateNameWriter(null)),
						MapWrapperWriterPartial.map(AossNameFlagDbData::getId).wrapper(updateRecordWriter())
					))
				.build();
		//@formatter:on
	}

	@StepBean
	ItemStreamReader<AossNameFlagDbData> reader() {
		return autowire(new FileSourceJdbcReader<>(SELECT_SQL, AossNameFlagDbData.class));
	}

	@StepBean
	Function<AossNameFlagDbData, AossNameAddrDbData> chiNameMapper(SpGetNameAddr sp) {
		return db -> new AossNameAddrDbData() {
			{
				setRmid(db.getRmid());
				setEngName(db.getNm());

				NameAddr nameAddr = sp.get(db.getRmid());
				setName1(nameAddr.getName1());
				setName2(nameAddr.getName2());
			}
		};
	}

	@StepBean
	ForEachItemWriter<AossNameAddrDbData> updateNameWriter(SpPbUserUpdName sp) {
		return db -> sp.update(new UpdNameParam(db.getRmid(), db.getEngName(), db.getChiNm(), db.getChiNm()));
	}

	@StepBean
	ItemWriter<String> updateRecordWriter() {
		return autowire(new FileSourceJdbcWriter<String>(UPDATE_SQL) {
			{
				setItemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id));
			}
		});

	}

}
