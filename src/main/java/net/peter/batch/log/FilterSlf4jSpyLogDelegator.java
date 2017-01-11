package net.peter.batch.log;

import java.util.stream.Stream;

import net.peter.batch.scheduler.QuartzConfig;
import org.apache.commons.lang3.StringUtils;

import net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.sql.Spy;

/**
 * 
 * To Filter Spring Batch JDBC Log
 * <p>
 * 
 * @author Peter.DI
 *
 */
public class FilterSlf4jSpyLogDelegator extends Slf4jSpyLogDelegator {

	private static final String BATCH_ADMIN_SQL_STRING = " BATCH_";
	private static final String QUARTZ_SQL_STRING = " " + QuartzConfig.QUARTZ_TABLE_PREFIX;

	@Override
	public void sqlTimingOccurred(Spy spy, long execTime, String methodCall, String sql) {
		if (filter(sql)) {
			return;
		}
		super.sqlTimingOccurred(spy, execTime, methodCall, sql);
	}

	@Override
	public void sqlOccurred(Spy spy, String methodCall, String sql) {
		if (filter(sql)) {
			return;
		}
		super.sqlOccurred(spy, methodCall, sql);
	}

	protected boolean filter(String sql) {
		return Stream.of(BATCH_ADMIN_SQL_STRING, QUARTZ_SQL_STRING).anyMatch(s -> StringUtils.contains(sql, s));
	}

}
