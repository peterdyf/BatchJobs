package net.peter.tools;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import net.peter.test.DbConfig4Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cncbinternational.common.service.FilesService;
import com.cncbinternational.spring.constant.DataSourceNames;
import com.cncbinternational.spring.constant.ProfileNames;
import com.google.common.base.CaseFormat;

import oracle.jdbc.OracleTypes;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DbConfig4Test.class, FilesService.class })
@Transactional
@ActiveProfiles(ProfileNames.TEST)
public abstract class GenSqlBean {

	@Autowired
	@Qualifier(DataSourceNames.DEFAULT)
	DataSource ds;

	@Autowired
	FilesService filesService;

	JdbcTemplate jdbc;
	
	@BeforeClass
	public static void disableLog() {
		DisableLog.disable();
	}

	@Before
	public void setUp() {
		jdbc = new JdbcTemplate(ds);
	}

	protected abstract String fileName();

	protected abstract Object[] getParams();

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // tools
	public void gen() {
		jdbc.query(filesService.readResource(fileName()), new ResultSetExtractor<Object>() {
			@Override
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					String name = convertName(rsmd.getColumnName(i));
					String type = type(rsmd.getColumnType(i));
					System.out.format("%s %s;\n", type, name);
				}
				return null;
			}
		}, getParams());
	}


	private String type(int type) {
		switch (type) {
		case OracleTypes.DATE:
		case OracleTypes.TIMESTAMP:
			return "Date";
		case OracleTypes.VARCHAR:
		case OracleTypes.CHAR:
		case OracleTypes.NVARCHAR:
		case OracleTypes.NCHAR:
		case OracleTypes.CLOB:
			return "String";
		case OracleTypes.INTEGER:
			return "Integer";
		case OracleTypes.NUMBER:
			return "BigDecimal";
		default:
			throw new UnsupportedOperationException("Unknown Type:" + type);
		}
	}

	private static String convertName(String dbColName) {
		String name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, dbColName);
		char first = Character.toLowerCase(name.charAt(0));
		return first + name.substring(1);
	}

}
