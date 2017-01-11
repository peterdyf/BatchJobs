package net.peter.batch.jobs.aossUpdateEmail;

import static net.peter.batch.common.writer.CompositeItemWriterPartial.multi;
import static com.cncbinternational.common.util.MyDate.now;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import net.peter.batch.common.writer.FileSourceJdbcWriter;
import net.peter.batch.common.writer.MapWrapperWriterPartial;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import net.peter.batch.annotation.JobConfiguration;
import net.peter.batch.annotation.StepBean;
import net.peter.batch.build.AbstractJob;
import net.peter.batch.common.reader.FileSourceJdbcReader;
import net.peter.batch.common.writer.FixLengthFileWriter;

@JobConfiguration
public class AossGenUpdateEmailFile extends AbstractJob {

	private static final String JOB_NAME = "aossGenUpdateEmailFile";
	private static final String STEP_NAME = "GenUpdateEmailFile";
	private static final int CHUNK_SIZE = 1000;
	private static final String SELECT_SQL = "classpath:com/cncbinternational/batch/jobs/aossUpdateEmail/AossUpdateEmailSelect.sql";
	private static final String MARK_COMPLATE_SQL = "classpath:com/cncbinternational/batch/jobs/aossUpdateEmail/AossUpdateEmailMarkComplated.sql";
	private static final String FILE_NAME = "nameChgEmail.txt";
	private static final String FORMAT = "%-17s%-47s";

	@Override
	public String getJobName() {
		return JOB_NAME;
	}

	@Bean
	protected Job buildJob() {
		//@formatter:off
		return jobBuilder()
				.start(mainStep())
				.build();
		//@formatter:on
	}

	@Bean
	Step mainStep() {
		//@formatter:off
		return stepBuilder(STEP_NAME)
				.<AossUpdateEmailDbData, AossUpdateEmailDbData> chunk(CHUNK_SIZE)	
				.reader(reader())
				.writer(multi(
						MapWrapperWriterPartial.map(AossUpdateEmailDbData::getId).wrapper(markComplatedWriter()),
						MapWrapperWriterPartial.map(fileMapper()).wrapperStream(updateEmailWriter())
				))
				.build();
		//@formatter:on
	}

	@StepBean
	Function<AossUpdateEmailDbData, AossUpdateEmailFileData> fileMapper() {
		return db -> new AossUpdateEmailFileData() {
			{
				setRmid(db.getRmid());
				setEmailAddr(db.getEmailAddr());
			}
		};
	}

	@StepBean
	ItemStreamReader<AossUpdateEmailDbData> reader() {
		return autowire(new FileSourceJdbcReader<>(SELECT_SQL, AossUpdateEmailDbData.class));
	}

	@StepBean
	ItemStreamWriter<AossUpdateEmailFileData> updateEmailWriter() {
		return autowire(new FixLengthFileWriter<AossUpdateEmailFileData>(FILE_NAME, AossUpdateEmailFileData.class, FORMAT) {
			final AtomicLong count = new AtomicLong();
			final Set<AossUpdateEmailFileData> exsiting = new HashSet<>();

			{
				setHeaderCallback(writer -> writer.write(new SimpleDateFormat("yyyyMMdd").format(now().minusDays(1).util())));
				setFooterCallback(writer -> writer.write(count.toString()));
			}

			@Override
			@SuppressWarnings("PMD.SignatureDeclareThrowsException") // override
			public void write(List<? extends AossUpdateEmailFileData> items) throws Exception {
				List<AossUpdateEmailFileData> newList = new ArrayList<>();
				items.stream().forEach(item -> {
					if (!exsiting.contains(item)) {
						exsiting.add(item);
						newList.add(item);
					}
				});
				count.addAndGet(newList.size());
				super.write(newList);
			}

		});
	}

	@StepBean
	ItemWriter<String> markComplatedWriter() {
		return autowire(new FileSourceJdbcWriter<String>(MARK_COMPLATE_SQL) {
			{
				setItemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id));
			}
		});
	}
}
