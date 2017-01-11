package net.peter.test.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.scope.JobScope;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.cncbinternational.spring.constant.DataSourceNames;
import com.cncbinternational.spring.constant.ProfileNames;

@Configurable
@EnableTransactionManagement
@Profile(ProfileNames.TEST)
@SuppressWarnings("PMD.TooManyMethods") // Spring Batch Test needed
public class JobConfig4Test {

	@Bean
	public JobLauncher jobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		return jobLauncher;
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		return jobRegistryBeanPostProcessor;
	}

	@Bean
	public JobRepositoryFactoryBean jobRepository(@Qualifier(DataSourceNames.DEFAULT) DataSource dataSource, PlatformTransactionManager transactionManager) {
		JobRepositoryFactoryBean jobRepository = new JobRepositoryFactoryBean();
		jobRepository.setDataSource(dataSource);
		jobRepository.setTransactionManager(transactionManager);
		jobRepository.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
		return jobRepository;
	}

	@Bean
	public JobOperator jobOperator(JobRepository jobRepository, JobLauncher jobLauncher, ListableJobLocator jobRegistry, JobExplorer jobExplorer) {
		SimpleJobOperator jobOperator = new SimpleJobOperator();
		jobOperator.setJobRepository(jobRepository);
		jobOperator.setJobLauncher(jobLauncher);
		jobOperator.setJobRegistry(jobRegistry);
		jobOperator.setJobExplorer(jobExplorer);
		return jobOperator;
	}

	@Bean
	public JobExplorerFactoryBean jobExplorer(@Qualifier(DataSourceNames.DEFAULT) DataSource dataSource) {
		JobExplorerFactoryBean jobExplorer = new JobExplorerFactoryBean();
		jobExplorer.setDataSource(dataSource);
		return jobExplorer;
	}

	@Bean
	public JobRegistry jobRegistry() {
		return new MapJobRegistry();
	}

	@Bean
	public JdbcTemplate jdbcTemplate(@Qualifier(DataSourceNames.DEFAULT) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public JobBuilderFactory jobBuilderFactory(JobRepository jobRepository) {
		return new JobBuilderFactory(jobRepository);
	}

	@Bean
	public StepBuilderFactory stepBuilderFactory(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilderFactory(jobRepository, transactionManager);
	}

	@Bean
	public JobLauncherTestUtils jobLauncherTestUtils(JobRepository jobRepository, JobLauncher jobLauncher) {
		return new JobLauncherTestUtils();
	}

	@Bean
	public StepScope stepScope() {
		return new StepScope();
	}

	@Bean
	public JobScope jobScope() {
		return new JobScope();
	}

}
