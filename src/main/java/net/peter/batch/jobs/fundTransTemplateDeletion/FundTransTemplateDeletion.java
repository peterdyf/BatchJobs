package net.peter.batch.jobs.fundTransTemplateDeletion;

import static net.peter.batch.common.writer.CompositeItemWriterPartial.multi;
import static com.cncbinternational.common.util.MyDate.date;
import static com.cncbinternational.spring.apis.Bean.toAuditLogXML;

import java.sql.Date;
import java.util.function.Function;

import net.peter.batch.annotation.StepBean;
import net.peter.batch.common.writer.FileSourceJdbcWriter;
import net.peter.batch.common.writer.ForEachItemWriter;
import net.peter.batch.common.writer.MapWrapperWriterPartial;
import net.peter.batch.service.FtpService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import net.peter.batch.annotation.JobConfiguration;
import net.peter.batch.build.AbstractJob;
import net.peter.batch.common.reader.FileSourceJdbcReader;
import net.peter.batch.common.tasklet.FtpUploadTasklet;
import net.peter.batch.common.tasklet.SimpleTasklet;
import net.peter.batch.common.writer.CsvWriter;
import net.peter.batch.service.AccountUtil;
import com.cncbinternational.common.service.SysParamService;
import com.cncbinternational.common.storeprocedure.SpAuditLogInsert;
import com.cncbinternational.common.storeprocedure.SpAuditLogInsert.AuditLog;
import com.google.common.base.Preconditions;

@JobConfiguration
public class FundTransTemplateDeletion extends AbstractJob {

	private static final String JOB_NAME = "FundTransTemplateDeletion";
	private static final String REPORT_FILE_NAME = JOB_NAME + ".csv";
	private static final String STEP_NAME = "ReadAndCopyToAuditLog";
	private static final String STEP_FTP_NAME = "ReportToFtp";
	private static final int CHUNK_SIZE = 1000;
	private static final String DURATION_PARAM = "FT_TMP_DURATION";
	private static final String SELECT_SQL = "classpath:com/cncbinternational/batch/jobs/fundTransTemplateDeletion/FundTransTemplateDeletionSelect.sql";
	private static final String DELETE_SQL = "classpath:com/cncbinternational/batch/jobs/fundTransTemplateDeletion/FundTransTemplateDeletion.sql";

	private static final String AUDITLOG_FUNC_TYPE = "FTTM";
	private static final String AUDITLOG_CHANNEL_CD = "PIB";
	private static final String AUDITLOG_ACTION_TYPE = "batch";

	static final String FTP_FOLDER = "MISReport";

	@Override
	public String getJobName() {
		return JOB_NAME;
	}

	@Bean
	protected Job buildJob() {
		//@formatter:off
		return jobBuilder()
				.start(mainStep())
				.next(stepBuilder(STEP_FTP_NAME).tasklet(autowire(new FtpUploadTasklet(REPORT_FILE_NAME, FTP_FOLDER))).build())
				.build();
		//@formatter:on
	}

	@Bean
	Step mainStep() {
		//@formatter:off
		return stepBuilder(STEP_NAME)
				.<FundTransTemplateDbData, FundTransTemplateDbData> chunk(CHUNK_SIZE)
				.reader(reader(null, null))
				.writer(multi(
						MapWrapperWriterPartial.map(auditMapper()).wrapper(auditWriter(null)),
						MapWrapperWriterPartial.map(csvMapper(null)).wrapperStream(csvWriter()),
						MapWrapperWriterPartial.map(FundTransTemplateDbData::getId).wrapper(deleteRecordWriter())
				))
				.build();
		//@formatter:on
	}

	@StepBean
	ItemStreamReader<FundTransTemplateDbData> reader(@Value("#{jobParameters['startTime']}") String startTimeStr, SysParamService sysParam) {
		Preconditions.checkNotNull(startTimeStr, "Not Found Job Parameter:startTime");
		return autowire(new FileSourceJdbcReader<FundTransTemplateDbData>(SELECT_SQL, FundTransTemplateDbData.class) {
			{
				Date param = date(startTimeStr).minusDays(sysParam.getInt(DURATION_PARAM)).sql();
				setParameters(param);
			}
		});
	}

	@StepBean
	Function<FundTransTemplateDbData, AuditLog> auditMapper() {
		return db -> new AuditLog() {
			{
				setRmidIn(db.getRmid());
				setSubFunctionTypeIn(AUDITLOG_FUNC_TYPE);
				setActionTypeIn(AUDITLOG_ACTION_TYPE);
				setChannelCdIn(AUDITLOG_CHANNEL_CD);
				setPreImgIn(toAuditLogXML(new FundTransTemplateAuditXml() {
					{
						setTemplateNm(db.getTemplateNm());
						setFrPbAcctNmEng(db.getFrPbAcctNmEng() + " - " + AccountUtil.formatAcctNo(db.getFrPbAcctNo()) + ", " + db.getFrIbCurrEngDscp());
						setToPbAcctNmEng(db.getToPbAcctNmEng() + " - " + AccountUtil.formatAcctNo(db.getToAcctNo()) + ", " + db.getToIbCurrEngDscp());
						setTranCurrEngDscp(db.getTranCurrEngDscp());
						setNote(db.getNote());
					}
				}));
			}
		};
	}

	@StepBean
	Function<FundTransTemplateDbData, FundTransTemplateCsvData> csvMapper(@Value("#{jobParameters['startTime']}") String startTimeStr) {
		return db -> new FundTransTemplateCsvData() {
			{
				setDateTime(date(startTimeStr).string());
				setRmid(db.getRmid());
				setAccountFrom(db.getFrPbAcctNo());
				setAccountTo(db.getToAcctNo());
				setTemplateName(db.getTemplateNm());
			}
		};
	}

	@StepBean
	ForEachItemWriter<AuditLog> auditWriter(SpAuditLogInsert sp) {
		return sp::insert;
	}

	@StepBean
	ItemStreamWriter<FundTransTemplateCsvData> csvWriter() {
		return autowire(new CsvWriter<>(REPORT_FILE_NAME, FundTransTemplateCsvData.class));
	}

	@StepBean
	ItemWriter<String> deleteRecordWriter() {
		return autowire(new FileSourceJdbcWriter<String>(DELETE_SQL) {
			{
				setItemSqlParameterSourceProvider(id -> new MapSqlParameterSource("id", id));
			}
		});
	}
	
	@StepBean
	SimpleTasklet ftpTask(FtpService ftpService) {
		return () -> ftpService.send(REPORT_FILE_NAME, FTP_FOLDER);
	}

}
