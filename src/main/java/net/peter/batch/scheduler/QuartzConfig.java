package net.peter.batch.scheduler;

import static org.quartz.impl.StdSchedulerFactory.AUTO_GENERATE_INSTANCE_ID;
import static org.quartz.impl.StdSchedulerFactory.PROP_JOB_STORE_CLASS;
import static org.quartz.impl.StdSchedulerFactory.PROP_SCHED_INSTANCE_ID;

import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * THREAD_COUNT=1 because, <br>
 * batch jobs is asynchronous and will instantly return, <br>
 * so it is enough to have only one thread to trigger.<br>
 * 
 * @author Peter.DI
 *
 */
@Configuration
public class QuartzConfig {

	public static final int THREAD_COUNT = 1;
	public static final boolean IS_CLUSTERED = true;
	public static final String QUARTZ_TABLE_PREFIX = "QZ2_";

	@Bean
	public SchedulerFactoryBean schedulerFactory(DataSource dataSource, ApplicationContext springContext) {
		return new SchedulerFactoryBean() {
			{
				setJobFactory(new SpringBeanJobFactory() {
					@Override
					@SuppressWarnings("PMD.SignatureDeclareThrowsException") // native
					protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
						final Object job = super.createJobInstance(bundle);
						springContext.getAutowireCapableBeanFactory().autowireBean(job);
						return job;
					}
				});
				setDataSource(dataSource);
				setQuartzProperties(new Properties() {
					private static final long serialVersionUID = 1L;

					{
						setProperty(PROP_SCHED_INSTANCE_ID, AUTO_GENERATE_INSTANCE_ID);
						setProperty(PROP_JOB_STORE_CLASS, JobStoreTX.class.getName());
						setProperty("org.quartz.jobStore.isClustered", String.valueOf(IS_CLUSTERED));
						setProperty("org.quartz.jobStore.tablePrefix", QUARTZ_TABLE_PREFIX);
						setProperty("org.quartz.threadPool.threadCount", String.valueOf(THREAD_COUNT));
					}
				});
			}
		};
	}

}
