package net.peter.batch.common.tasklet;

import org.springframework.beans.factory.annotation.Autowired;

import net.peter.batch.service.JdbcService;
import com.google.common.base.Preconditions;

public class TruncateTableTasklet implements SimpleTasklet {

	@Autowired
	private JdbcService jdbc;
	private final String tableName;

	public TruncateTableTasklet(String tableName) {
		Preconditions.checkNotNull(tableName);
		this.tableName = tableName;
	}

	public void execute() {
		jdbc.truncateTable(tableName);
	}
}
