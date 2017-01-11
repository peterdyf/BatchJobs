package net.peter.batch.common.writer;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cncbinternational.spring.constant.DataSourceNames;
import com.google.common.base.Preconditions;

public class SqlSourceJdbcWriter<T> extends JdbcBatchItemWriter<T> {

	private final String sql;
	
	public SqlSourceJdbcWriter(String sql) {
		super();
		Preconditions.checkNotNull(sql);
		this.sql = sql;
	}

	@Autowired
	@Qualifier(DataSourceNames.DEFAULT)
	public void setDataSource(DataSource dataSource) {
		Preconditions.checkNotNull(dataSource);
		super.setDataSource(dataSource);
	}

	@PostConstruct
	void init() {
		super.setSql(sql);
	}

}
