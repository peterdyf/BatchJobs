package net.peter.batch.jobs.tokenMailingAddress;

import static com.cncbinternational.spring.apis.Bean.convert;

import java.util.function.BiFunction;

import net.peter.batch.annotation.StepBean;
import net.peter.batch.common.parameter.BeanMappingParameterProvider;
import net.peter.batch.common.reader.FixLengthFileReader;
import net.peter.batch.common.tasklet.TruncateTableTasklet;
import net.peter.batch.common.writer.FileSourceJdbcWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;

import net.peter.batch.annotation.JobConfiguration;
import net.peter.batch.build.AbstractJob;
import net.peter.batch.common.reader.DelimitedFileReader;
import net.peter.batch.common.reader.SqlSourceJdbcReader;
import net.peter.batch.common.tasklet.FtpDownloadTasklet;
import net.peter.batch.common.tasklet.FtpUploadTasklet;
import net.peter.batch.common.writer.CsvWriter;

@JobConfiguration
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class TokenMailingAddress extends AbstractJob {

	static final String REPORT_SUCCESS = "Customer_mailing_address.csv";
	static final String REPORT_UNMATCHED = "Customer_mailing_address_unmatched.csv";
	static final String FILE_CHI = "ONETIME_IFA_NA_PPB.DAT";
	static final String FILE_CF = "ONETIME_CAFPBINF.DAT";
	static final String FILE_CC = "ONETIME_cccustdm.dat";
	static final String FILE_IM = "ONETIME_IM-DCUSTOMER.TXT";
	static final String FILE_ST = "ONETIME_ST-DCUSTOMER.TXT";
	static final String FILE_RMID = "RMID_IN.txt";
	
	
	private static final String SQL_CHI = "SqlCHI.sql";
	private static final String SQL_CF = "SqlCF.sql";
	private static final String SQL_CC = "SqlCC.sql";
	private static final String SQL_IM = "SqlIM.sql";
	private static final String SQL_ST = "SqlST.sql";
	private static final String SQL_RET = "SqlRET.sql";

	static final String TABLE_CHI = "TB_FORCE_SUBS_CHI";
	static final String TABLE_CF = "TB_FORCE_SUBS_CF";
	static final String TABLE_CC = "TB_FORCE_SUBS_CC";
	static final String TABLE_IM = "TB_FORCE_SUBS_IM";
	static final String TABLE_ST = "TB_FORCE_SUBS_ST";
	static final String TABLE_RET = "TB_FORCE_SUBS_RET";

	private static final String SQL_PATH = "classpath:com/cncbinternational/batch/jobs/tokenMailingAddress/";

	private static final int CHUNK_SIZE = 1000;

	private static final String JOB_NAME = "tokenMailingAddress";

	private static final String HEADER_FOOTER_FILTER = "\\d+;";
	private static final String DELIMITER = "||";

	static final String FTP_FOLDER = "MISReport";

	@Override
	public String getJobName() {
		return JOB_NAME;
	}

	@Bean
	protected Job buildJob() {
		//@formatter:off
		return jobBuilder()
				.start(stepBuilder("DOWNLOAD_IFA_NA_PPB_DAT").tasklet(autowire(new FtpDownloadTasklet(FILE_CHI))).build())
				.next(stepBuilder("DOWNLOAD_CAFPBINF_DAT").tasklet(autowire(new FtpDownloadTasklet(FILE_CF))).build())
				.next(stepBuilder("DOWNLOAD_CCCUSTDM_DAT").tasklet(autowire(new FtpDownloadTasklet(FILE_CC))).build())
				.next(stepBuilder("DOWNLOAD_IM_DCUSTOMER_txt").tasklet(autowire(new FtpDownloadTasklet(FILE_IM))).build())
				.next(stepBuilder("DOWNLOAD_ST_DCUSTOMER_TXT").tasklet(autowire(new FtpDownloadTasklet(FILE_ST))).build())
				.next(stepBuilder("CLEAN_TB_FORCE_SUBS_CHI").tasklet(autowire(new TruncateTableTasklet(TABLE_CHI))).build())
				.next(stepBuilder("CLEAN_TB_FORCE_SUBS_CF").tasklet(autowire(new TruncateTableTasklet(TABLE_CF))).build())
				.next(stepBuilder("CLEAN_TB_FORCE_SUBS_CC").tasklet(autowire(new TruncateTableTasklet(TABLE_CC))).build())
				.next(stepBuilder("CLEAN_TB_FORCE_SUBS_IM").tasklet(autowire(new TruncateTableTasklet(TABLE_IM))).build())
				.next(stepBuilder("CLEAN_TB_FORCE_SUBS_ST").tasklet(autowire(new TruncateTableTasklet(TABLE_ST))).build())
				.next(fileToDbStep(this::delimitedReader, "LoadCHI", ChiFile.class, ChiDb.class, FILE_CHI, SQL_PATH + SQL_CHI))
				.next(fileToDbStep(this::fixedLenghtReader, "LoadCF", CfFile.class, CfDb.class, FILE_CF, SQL_PATH + SQL_CF))
				.next(fileToDbStep(this::fixedLenghtReader, "LoadCC", CcFile.class, CcDb.class, FILE_CC, SQL_PATH + SQL_CC))
				.next(fileToDbStep(this::fixedLenghtReader, "LoadIM", ImFile.class, ImDb.class, FILE_IM, SQL_PATH + SQL_IM))
				.next(fileToDbStep(this::fixedLenghtReader, "LoadST", StFile.class, StDb.class, FILE_ST, SQL_PATH + SQL_ST))
				.next(stepBuilder("DOWNLOAD_RMID").tasklet(autowire(new FtpDownloadTasklet(FILE_RMID))).build())
				.next(stepBuilder("CLEAN_TB_FORCE_SUBS_RET").tasklet(autowire(new TruncateTableTasklet(TABLE_RET))).build())
				.next(matchStep())
				.next(reportStep("SUCCESS_REPORT", REPORT_SUCCESS, RetSuccessCsv.class, MatchRet.MATCHED ))
				.next(reportStep("FAILED_REPORT", REPORT_UNMATCHED, RetFailCsv.class,  MatchRet.UNMATCHED))
				.next(stepBuilder("FTP_SUCCESS_REPORT").tasklet(autowire(new FtpUploadTasklet(REPORT_SUCCESS, FTP_FOLDER))).build())
				.next(stepBuilder("FTP_FAILED_REPORT").tasklet(autowire(new FtpUploadTasklet(REPORT_UNMATCHED, FTP_FOLDER))).build())
				.build();
		//@formatter:on
	}

	private <I, O> Step fileToDbStep(BiFunction<String, Class<I>, ItemReader<? extends I>> readerBuilder, String name, Class<I> in, Class<O> out, String file, String inserSql) {
		//@formatter:off
		return stepBuilder(name)
				.<I, O> chunk(CHUNK_SIZE)
				.reader(readerBuilder.apply(file, in))
				.processor(f -> convert(f, out))
				.writer(writer(inserSql))
				.build();
		//@formatter:on
	}

	private <T> FixLengthFileReader<T> fixedLenghtReader(String file, Class<T> clazz) {
		return autowire(new FixLengthFileReader<T>(file, clazz) {
			{
				setSkippedPatterns(new String[] { HEADER_FOOTER_FILTER });
			}
		});
	}

	private <T> DelimitedFileReader<T> delimitedReader(String file, Class<T> clazz) {
		return autowire(new DelimitedFileReader<T>(file, DELIMITER, clazz) {
			{
				setSkippedPatterns(new String[] { HEADER_FOOTER_FILTER });
			}
		});
	}

	private <T> FileSourceJdbcWriter<T> writer(String sql) {
		return autowire(new FileSourceJdbcWriter<T>(sql) {
			{
				setItemSqlParameterSourceProvider(BeanMappingParameterProvider<T>::new);
			}
		});
	}

	@Bean
	Step matchStep() {
		//@formatter:off
		return stepBuilder("MATCH")
				.<RmidFile, MatchRet> chunk(CHUNK_SIZE)	
				.reader(delimitedReader(FILE_RMID, RmidFile.class))
				.processor(matchProcessor(null))
				.writer(writer(SQL_PATH + SQL_RET))
				.build();
		//@formatter:on
	}

	@StepBean
	ItemProcessor<RmidFile, MatchRet> matchProcessor(TokenMailingAddressMatchingService matchingService) {
		return r -> matchingService.match(r.getRmid());
	}

	private <O> Step reportStep(String stepName, String reportFile, Class<O> out, String ret) {
		//@formatter:off
		return stepBuilder(stepName)
				.<MatchRet, O> chunk(CHUNK_SIZE)	
				.reader(autowire(new SqlSourceJdbcReader<>("select * from TB_FORCE_SUBS_RET where ret = ?", MatchRet.class).setParameters(ret)))
				.processor(f -> convert(f, out))
				.writer(autowire(new CsvWriter<O>(reportFile, out)))
				.build();
		//@formatter:on
	}

}
