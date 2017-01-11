package net.peter.batch.jobs.loadCustChiAddr;

import net.peter.batch.common.parameter.BeanMappingParameterProvider;
import net.peter.batch.common.tasklet.TruncateTableTasklet;
import net.peter.batch.common.writer.FileSourceJdbcWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;

import net.peter.batch.annotation.JobConfiguration;
import net.peter.batch.annotation.StepBean;
import net.peter.batch.build.AbstractJob;
import net.peter.batch.common.reader.DelimitedFileReader;
import net.peter.batch.common.tasklet.CheckPlatFileDateTask;
import net.peter.batch.common.tasklet.FtpDownloadTasklet;
import net.peter.batch.common.tasklet.SimpleTasklet;
import net.peter.batch.service.LocalFilesService;
import com.cncbinternational.common.service.SysParamService;

@JobConfiguration
public class LoadCustChiAddr extends AbstractJob {

	private static final int CHUNK_SIZE = 1000;

	private static final String JOB_NAME = "loadCustChiAddr";

	private static final String STEP_DOWNLOAD = "DownloadFile";
	private static final String STEP_CHECK_DATE = "CheckDate";
	private static final String STEP_START_STATE = "SetStartState";
	private static final String STEP_CLEAN_DB = "CleanDb";
	private static final String STEP_FILE_TO_DB = "FileToDb";
	private static final String STEP_END_STATE = "SetEndState";
	private static final String STEP_BACKAP_FILE = "BackupFile";

	private static final String TRUNCATE_TABLE = "CUST_CHI_ADDR";
	private static final String INSERT_SQL = "classpath:com/cncbinternational/batch/jobs/loadCustChiAddr/LoadCustChiAddr.sql";
	private static final String STATE_PARAM = "CUST_CHI_ADDR";
	private static final String STATE_START = "1";
	private static final String STATE_END = "0";

	private static final String FILE_NAME = "IFA_NA_PPB.DAT";
	private static final String HEADER_FOOTER_FILTER = "\\d+;";
	private static final String DELIMITER = "||";

	@Override
	public String getJobName() {
		return JOB_NAME;
	}

	@Bean
	protected Job buildJob() {
		//@formatter:off
		return jobBuilder()
				.start(stepBuilder(STEP_DOWNLOAD).tasklet(autowire(new FtpDownloadTasklet(FILE_NAME))).build())
				.next(stepBuilder(STEP_CHECK_DATE).tasklet(checkDateTask()).build())
				.next(stepBuilder(STEP_START_STATE).tasklet(startStateTask(null)).build())
				.next(stepBuilder(STEP_CLEAN_DB).tasklet(autowire(new TruncateTableTasklet(TRUNCATE_TABLE))).build())
				.next(fileToDbStep())
				.next(stepBuilder(STEP_END_STATE).tasklet(endStateTask(null)).build())
				.next(stepBuilder(STEP_BACKAP_FILE).tasklet(backupTask(null)).build())
				.build();
		//@formatter:on
	}

	@StepBean
	Tasklet checkDateTask() {
		return autowire(new CheckPlatFileDateTask(FILE_NAME));
	}

	@StepBean
	SimpleTasklet startStateTask(SysParamService sysParam) {
		return () -> sysParam.setParam(STATE_PARAM, STATE_START);
	}

	@StepBean
	SimpleTasklet endStateTask(SysParamService sysParam) {
		return () -> sysParam.setParam(STATE_PARAM, STATE_END);
	}

	@Bean
	Step fileToDbStep() {
		//@formatter:off
		return stepBuilder(STEP_FILE_TO_DB)
				.<LoadCustChiAddrFileData, LoadCustChiAddrFileData> chunk(CHUNK_SIZE)	
				.reader(reader())
				.writer(writer())
				.build();
		//@formatter:on
	}

	@StepBean
	SimpleTasklet backupTask(LocalFilesService localFilesService) {
		return () -> localFilesService.backupFile(FILE_NAME);
	}

	@StepBean
	ItemStreamReader<LoadCustChiAddrFileData> reader() {
		return autowire(new DelimitedFileReader<LoadCustChiAddrFileData>(FILE_NAME, DELIMITER, LoadCustChiAddrFileData.class) {
			{
				setSkippedPatterns(new String[] { HEADER_FOOTER_FILTER });
			}
		});
	}

	@StepBean
	ItemWriter<LoadCustChiAddrFileData> writer() {
		return autowire(new FileSourceJdbcWriter<LoadCustChiAddrFileData>(INSERT_SQL) {
			{
				setItemSqlParameterSourceProvider(BeanMappingParameterProvider<LoadCustChiAddrFileData>::new);
			}
		});
	}
}
