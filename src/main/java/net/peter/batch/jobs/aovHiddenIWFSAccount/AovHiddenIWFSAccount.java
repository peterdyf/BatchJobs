package net.peter.batch.jobs.aovHiddenIWFSAccount;

import java.util.function.Function;

import net.peter.batch.annotation.StepBean;
import net.peter.batch.common.parameter.BeanMappingParameterProvider;
import net.peter.batch.common.reader.FixLengthFileReader;
import net.peter.batch.common.tasklet.TruncateTableTasklet;
import net.peter.batch.common.writer.FileSourceJdbcWriter;
import net.peter.batch.common.writer.MapWrapperWriterPartial;
import net.peter.batch.service.FtpService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;

import net.peter.batch.annotation.JobConfiguration;
import net.peter.batch.build.AbstractJob;
import net.peter.batch.common.tasklet.SimpleTasklet;

@JobConfiguration
public class AovHiddenIWFSAccount extends AbstractJob {

	private static final String JOB_NAME = "aovHiddenIWFSAccount";
	private static final String STEP_NAME = "FileToDb";
	private static final String STEP_CLEAN_DB = "CleanDb";
	private static final String STEP_DOWNLOAD_NAME = "DownloadFile";
	private static final String TRUNCATE_TABLE = "TBL_PB_ACCT_CLS_IWFS";
	private static final int CHUNK_SIZE = 1000;
	private static final String INSERT_SQL = "classpath:com/cncbinternational/batch/jobs/aovHiddenIWFSAccount/AovHiddenIWFSAccountInsert.sql";
	private static final String FILE_NAME = "iw_wm_acc.dat";
	private static final String HEADER_FOOTER_FILTER = "\\d+;";

	@Override
	public String getJobName() {
		return JOB_NAME;
	}

	@Bean
	protected Job buildJob() {
		//@formatter:off
		return jobBuilder()
				.start(stepDownload())
				.next(stepBuilder(STEP_CLEAN_DB).tasklet(autowire(new TruncateTableTasklet(TRUNCATE_TABLE))).build())
				.next(stepMain())
				.build();
		//@formatter:on
	}
	
	@Bean
	Step stepDownload() {
		return stepBuilder(STEP_DOWNLOAD_NAME).tasklet(downloadTask(null)).build();
	}

	@StepBean
	SimpleTasklet downloadTask(FtpService ftpService) {
		return () -> ftpService.download(FILE_NAME);
	}

	@Bean
	Step stepMain() {
		//@formatter:off
		return stepBuilder(STEP_NAME)
				.<AovHiddenIWFSAccountFileData, AovHiddenIWFSAccountFileData> chunk(CHUNK_SIZE)	
				.reader(reader())
				.processor(filter())
				.writer(MapWrapperWriterPartial.map(transform()).wrapper(writer()))
				.build();
		//@formatter:on
	}

	@StepBean
	ItemStreamReader<AovHiddenIWFSAccountFileData> reader() {
		return autowire(new FixLengthFileReader<AovHiddenIWFSAccountFileData>(FILE_NAME, AovHiddenIWFSAccountFileData.class) {
			{
				setSkippedPatterns(new String[] { HEADER_FOOTER_FILTER });
			}
		});
	}

	@StepBean
	ItemProcessor<AovHiddenIWFSAccountFileData, AovHiddenIWFSAccountFileData> filter() {
		return f -> {
			if (!"T".equals(f.getAcctStatus()) && !"C".equals(f.getAcctUStatus())) {
				return null;
			}
			return f;
		};
	}

	private Function<AovHiddenIWFSAccountFileData, AovHiddenIWFSAccountDbData> transform() {
		return f -> new AovHiddenIWFSAccountDbData() {
			{
				setAccNo(f.getAccNo());
				setAcctTermDt(f.getAcctTermDt());
				setCustType(f.getCustType());
			}
		};
	}

	@StepBean
	ItemWriter<AovHiddenIWFSAccountDbData> writer() {
		return autowire(new FileSourceJdbcWriter<AovHiddenIWFSAccountDbData>(INSERT_SQL) {
			{
				setItemSqlParameterSourceProvider(BeanMappingParameterProvider<AovHiddenIWFSAccountDbData>::new);
			}
		});
	}
}
