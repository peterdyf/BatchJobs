package net.peter.batch.jobs.aossUpdateMobile;

import static net.peter.batch.common.writer.CompositeItemWriterPartial.multi;
import static com.cncbinternational.spring.apis.Bean.toAuditLogXML;

import java.util.function.Function;

import net.peter.batch.common.writer.FileSourceJdbcWriter;
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
import com.cncbinternational.common.storeprocedure.SpAuditLogInsert;
import com.cncbinternational.common.storeprocedure.SpAuditLogInsert.AuditLog;

@JobConfiguration
public class AossUpdateMobile extends AbstractJob {

	private static final String JOB_NAME = "aossUpdateMobile";
	private static final String STEP_NAME = "UpdateMobile";
	private static final int CHUNK_SIZE = 1000;
	private static final String SELECT_SQL = "classpath:com/cncbinternational/batch/jobs/aossUpdateMobile/AossUpdateMobileSelect.sql";
	private static final String MARK_COMPLATE_SQL = "classpath:com/cncbinternational/batch/jobs/aossUpdateMobile/AossUpdateMobileMarkComplated.sql";

	private static final String AUDITLOG_FUNC_TYPE = "TFOR";
	private static final String AUDITLOG_CHANNEL_CD = "BO";
	private static final String AUDITLOG_ACTION_TYPE_INSERT = "insert";
	private static final String AUDITLOG_ACTION_TYPE_UPDATE = "update";

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
				.<AossUpdateMobileDbData, AossUpdateMobileDbData> chunk(CHUNK_SIZE)	
				.reader(reader())
				.writer(multi(
						MapWrapperWriterPartial.map(auditMapper()).wrapper(auditWriter(null)),
						updateEmailWriter(null),
						MapWrapperWriterPartial.map(AossUpdateMobileDbData::getId).wrapper(markComplatedWriter())
				))
				.build();
		//@formatter:on
	}

	@StepBean
	ItemStreamReader<AossUpdateMobileDbData> reader() {
		return autowire(new FileSourceJdbcReader<>(SELECT_SQL, AossUpdateMobileDbData.class));
	}

	@StepBean
	Function<AossUpdateMobileDbData, AuditLog> auditMapper() {
		return db -> new AuditLog() {
			{
				setRmidIn(db.getRmid());
				setSubFunctionTypeIn(AUDITLOG_FUNC_TYPE);
				setChannelCdIn(AUDITLOG_CHANNEL_CD);
				setUserCdIn(db.getUpdatedBy());
				if (db.isPreExists()) {
					setActionTypeIn(AUDITLOG_ACTION_TYPE_UPDATE);
					setPreImgIn(toAuditLogXML(new AossUpdateMobileAuditXml() {
						{
							setIbAcctNo(db.getPreIbAcctNo());
							setOtpMbCtry(db.getPreOtpMbCtry());
							setOtpMbArea(db.getPreOtpMbArea());
							setOtpMbNo(db.getPreOtpMbNo());
						}
					}));
				} else {
					setActionTypeIn(AUDITLOG_ACTION_TYPE_INSERT);
				}
				setPostImgIn(toAuditLogXML(new AossUpdateMobileAuditXml() {
					{
						setIbAcctNo(db.getIbAcctNo());
						setOtpMbNo(db.getSmsPhoneNumber());
					}
				}));
			}
		};
	}

	@StepBean
	ForEachItemWriter<AuditLog> auditWriter(SpAuditLogInsert sp) {
		return sp::insert;
	}

	@StepBean
	ForEachItemWriter<AossUpdateMobileDbData> updateEmailWriter(SpIbTfaOtpRegUpdate sp) {
		return db -> {
			if (db.isPreExists()) {
				sp.updateForAOSS(db.getIbAcctNo(), db.getSmsPhoneNumber(), db.getUpdatedBy());
			} else {
				sp.insertForAOSS(db.getIbAcctNo(), db.getSmsPhoneNumber(), db.getUpdatedBy());
			}
		};
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
