package net.peter.test;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import com.cncbinternational.spring.constant.DataSourceNames;
import com.cncbinternational.spring.constant.ProfileNames;

@Configurable
@PropertySource("classpath:test-datasource.properties")
@Profile(ProfileNames.TEST)
public class MssqlDbConfig4Test {

	@Bean(name = DataSourceNames.MSSQL)
	@SuppressWarnings("PMD.UseObjectForClearerAPI") // properties inject
	public DataSource dataSourceMssql(@Value("${mssql.driver}") String driver, @Value("${mssql.url}") String urlStr, @Value("${mssql.user}") String userName,
			@Value("${mssql.password}") String userPwd) {

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

}
