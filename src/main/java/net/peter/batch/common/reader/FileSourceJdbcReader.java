package net.peter.batch.common.reader;

import static com.cncbinternational.spring.apis.Bean.toBean;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.batch.core.resource.ListPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cncbinternational.common.service.FilesService;
import com.cncbinternational.spring.constant.DataSourceNames;
import com.google.common.base.Preconditions;

/**
 * 
 * <b>Design Intent</b>
 * <p>
 * Since JdbcCursorItemReader use standard JDBC APIs rather than Spring ones,
 * <br>
 * PreparedStatementSetter cannot use name to locate parameters but only the
 * index.<br>
 * That why a parameters map not been use here, a List-base parameter will not
 * loss any more information than an Index-base parameter setter
 * 
 * @author Peter.DI
 *
 */
public class FileSourceJdbcReader<T> extends JdbcCursorItemReader<T> {

	@Autowired
	private FilesService filesService;

	private final String sqlFilePath;
	
	private final Class<T> rowClass;
	
	public FileSourceJdbcReader(String sqlFilePath, Class<T> rowClass) {
		super();
		Preconditions.checkNotNull(sqlFilePath);
		Preconditions.checkNotNull(rowClass);
		this.sqlFilePath = sqlFilePath;
		this.rowClass = rowClass;
	}

	@Autowired
	@Qualifier(DataSourceNames.DEFAULT)
	public void setDataSource(DataSource dataSource) {
		Preconditions.checkNotNull(dataSource);
		super.setDataSource(dataSource);
	}

	public void setParameters(Object... params) {
		Preconditions.checkNotNull(params);
		ListPreparedStatementSetter ps = new ListPreparedStatementSetter();
		ps.setParameters(Arrays.asList(params));
		super.setPreparedStatementSetter(ps);
	}

	@PostConstruct
	void init() {
		super.setRowMapper((rs, rowNum) -> toBean(rs, rowClass));
		super.setSql(filesService.readResource(sqlFilePath));
	}
}
