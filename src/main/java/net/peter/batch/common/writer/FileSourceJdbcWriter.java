package net.peter.batch.common.writer;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cncbinternational.common.service.FilesService;
import com.cncbinternational.spring.constant.DataSourceNames;
import com.google.common.base.Preconditions;

public class FileSourceJdbcWriter<T> extends JdbcBatchItemWriter<T> {

	@Autowired
	private FilesService filesService;

	private final String sqlFilePath;
	
	public FileSourceJdbcWriter(String sqlFilePath) {
		super();
		Preconditions.checkNotNull(sqlFilePath);
		this.sqlFilePath = sqlFilePath;
	}

	@Autowired
	@Qualifier(DataSourceNames.DEFAULT)
	public void setDataSource(DataSource dataSource) {
		Preconditions.checkNotNull(dataSource);
		super.setDataSource(dataSource);
	}

	@PostConstruct
	void init() {
		super.setSql(filesService.readResource(sqlFilePath));
	}

}
