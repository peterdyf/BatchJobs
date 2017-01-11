package net.peter.batch.jobs.emailUpdateReport.success;

import static com.cncbinternational.common.util.MyDate.date;

import java.sql.Date;

import net.peter.batch.annotation.StepBean;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import net.peter.batch.annotation.JobConfiguration;
import net.peter.batch.build.AbstractJob;
import net.peter.batch.common.reader.FileSourceJdbcReader;
import net.peter.batch.common.tasklet.FtpUploadTasklet;
import net.peter.batch.common.writer.CsvWriter;
import com.google.common.base.Preconditions;

@JobConfiguration
public class EmailUpdateSuccessReport extends AbstractJob {

	private static final String JOB_NAME = "EmailUpdateSuccessReport";
	private static final String REPORT_FILE_NAME = JOB_NAME + ".csv";
	private static final String STEP_NAME = "GenReport";
	private static final String STEP_FTP_NAME = "ReportToFtp";
	private static final int CHUNK_SIZE = 1000;
	private static final String SELECT_SQL = "classpath:com/cncbinternational/batch/jobs/emailUpdateReport/success/EmailUpdateSuccessReport.sql";
	static final String FTP_FOLDER = "MISReport";

	@Override
	public String getJobName() {
		return JOB_NAME;
	}

	@Bean
	protected Job buildJob() {
		//@formatter:off
		return jobBuilder()
				.start(stepGenReport())
				.next(stepBuilder(STEP_FTP_NAME).tasklet(autowire(new FtpUploadTasklet(REPORT_FILE_NAME, FTP_FOLDER))).build())
				.build();
		//@formatter:on
	}

	@Bean
	Step stepGenReport() {
		//@formatter:off
		return stepBuilder(STEP_NAME)
				.<EmailUpdateSuccessReportDbData, EmailUpdateSuccessReportCsvData> chunk(CHUNK_SIZE)	
				.reader(reader(null))
				.processor(processor())
				.writer(csvWriter())
				.build();
		//@formatter:on
	}

	@StepBean
	ItemStreamReader<EmailUpdateSuccessReportDbData> reader(@Value("#{jobParameters['startTime']}") String startTimeStr) {
		Preconditions.checkNotNull(startTimeStr, "Not Found Job Parameter:startTime");
		return autowire(new FileSourceJdbcReader<EmailUpdateSuccessReportDbData>(SELECT_SQL, EmailUpdateSuccessReportDbData.class) {
			{
				Date param = date(startTimeStr).sql();
				setParameters(param);
			}
		});
	}

	@StepBean
	ItemProcessor<EmailUpdateSuccessReportDbData, EmailUpdateSuccessReportCsvData> processor() {
		return db -> new EmailUpdateSuccessReportCsvData() {
			{
				setCustomerId(db.getRmid());
				setCustomerName(db.getName());
				setNewEmailAddress(db.getEmail());
				setTransDate(date(db.getTransDate()).timeString());
			}
		};
	}

	@StepBean
	ItemStreamWriter<EmailUpdateSuccessReportCsvData> csvWriter() {
		return autowire(new CsvWriter<>(REPORT_FILE_NAME, EmailUpdateSuccessReportCsvData.class));
	}

}
