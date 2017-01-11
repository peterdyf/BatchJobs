package net.peter.test;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import com.cncbinternational.spring.constant.DataSourceNames;
import com.cncbinternational.spring.constant.ProfileNames;

@Configurable
@PropertySource("classpath:test-datasource.properties")
@Profile(ProfileNames.TEST)
public class DbConfig4Test {

	@Bean(name = "dataSource")
	@SuppressWarnings("PMD.UseObjectForClearerAPI") // properties inject
	public DataSource dataSource(@Value("${driver}") String driver, @Value("${url}") String urlStr, @Value("${user}") String userName, @Value("${password}") String userPwd) {
		BasicDataSource ds = new BasicDataSource() {
			{
				setDriverClassName(driver);
				setUrl(urlStr);
				setUsername(userName);
				setPassword(userPwd);
			}
		};
		return new TransactionAwareDataSourceProxy(ds);
	}

	@Bean
	public DataSourceTransactionManager transactionManager(@Qualifier(DataSourceNames.DEFAULT) DataSource ds) {
		return new DataSourceTransactionManager(ds);
	}

	@Bean
	public DefaultConfiguration jooqDefaultConfiguration(DataSourceConnectionProvider connectionProvider) {
		DefaultConfiguration config = new DefaultConfiguration();
		config.set(connectionProvider);
		config.set(SQLDialect.DEFAULT);
		return config;
	}

	@Bean
	public DSLContext dslContext(DefaultConfiguration config) {
		return new DefaultDSLContext(config);
	}

	@Bean
	public DataSourceConnectionProvider jooqDataSourceConnectionProvider(@Qualifier(DataSourceNames.DEFAULT) DataSource ds) {
		return new DataSourceConnectionProvider(ds);
	}

	@Bean
	public PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
