package net.peter.batch.service;

import static com.cncbinternational.spring.apis.Bean.toBean;

import java.util.List;
import java.util.Optional;

import com.cncbinternational.spring.annotation.Loggable;
import com.cncbinternational.spring.annotation.TransactionalService;
import com.cncbinternational.spring.template.AbstractJdbc;

@TransactionalService
public class JdbcService extends AbstractJdbc {

	@Loggable
	public void executeSimpleSql(String sql) {
		jdbc.execute(sql);
	}

	@Loggable
	public void truncateTable(String table) {
		executeSimpleSql("TRUNCATE TABLE " + table);
	}

	public <T> Optional<T> queryOne(String sql, Class<T> rowClass, Object... params) {
		List<T> list = query(sql, rowClass, params);
		return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
	}

	public <T> List<T> query(String sql, Class<T> rowClass, Object... params) {
		return jdbc.query(sql, params, (rs, rowNum) -> toBean(rs, rowClass));
	}
}
