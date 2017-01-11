package net.peter.batch.jobs.futureTimeDeposit;

import java.util.function.Function;

import net.peter.batch.annotation.StepBean;
import net.peter.batch.common.writer.CompositeItemWriterPartial;
import net.peter.batch.common.writer.ForEachItemWriter;
import net.peter.batch.common.writer.MapWrapperWriterPartial;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.context.annotation.Bean;

import net.peter.batch.annotation.JobConfiguration;
import net.peter.batch.build.AbstractJob;
import net.peter.batch.common.reader.FileSourceJdbcReader;
import com.cncbinternational.common.service.host.message.ME07Out;
import com.cncbinternational.spring.annotation.Loggable;

@JobConfiguration
public class FutureTimeDepositPlaceOrder extends AbstractJob {

	private static final String JOB_NAME = "futureTimeDeposit";
	private static final String STEP_NAME = "ReadAndCopy";
	private static final int CHUNK_SIZE = 1000;
	private static final String SELECT_SQL = "classpath:com/cncbinternational/batch/jobs/futureTimeDeposit/FutureTimeDepositSelect.sql";

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
				.<FutureTimeDepositFlagDbData, FutureTimeDepositFlagDbData> chunk(CHUNK_SIZE)	
				.reader(reader())
				.writer(CompositeItemWriterPartial.multi(
						MapWrapperWriterPartial.map(FutureTimeDepositFlagDbData::toString).wrapper(printRecord()),
						MapWrapperWriterPartial.map(recordsFromHost(null)).wrapper(insRMNoWriter(null)))
						)
				.build();
		//@formatter:on
	}

	@StepBean
	ItemStreamReader<FutureTimeDepositFlagDbData> reader() {
		return autowire(new FileSourceJdbcReader<>(SELECT_SQL, FutureTimeDepositFlagDbData.class));
	}

	@StepBean
	Function<FutureTimeDepositFlagDbData, FutureTimeDepositDbData> recordsFromHost(FutureTimeDepositService ftdService) {
		return db -> new FutureTimeDepositDbData() {
			{
				setPbUserId(db.getPbUserId());
				ME07Out me07Out = ftdService.recordsFromHost(db.getRmid());
				setReturnCd(me07Out.getReturnCode());
				setRmNumber(me07Out.getRmNumber());
			}
		};
	}

	@StepBean
	ForEachItemWriter<String> printRecord() {
		return System.out::println;
	}

	@StepBean
	@Loggable
	ForEachItemWriter<FutureTimeDepositDbData> insRMNoWriter(SpTblPbRMNoIns sp) {
		return db -> {
			if ("00".equals(db.getReturnCd())) {
				sp.insert(new SpTblPbRMNoIns.InsRMNoParam(db.getPbUserId(), db.getRmNumber()));
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> Inserted " + db.toString());
			} else {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> NOT inserted " + db.toString());
			}
		};
	}

}
