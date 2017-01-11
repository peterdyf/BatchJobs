package net.peter.tools;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.peter.test.DbConfig4Test;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cncbinternational.spring.constant.ProfileNames;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;

import oracle.jdbc.OracleTypes;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DbConfig4Test.class })
@Transactional
@ActiveProfiles(ProfileNames.TEST)
@SuppressWarnings("PMD") // tools
public abstract class GenSpBean {

	protected static final String RESULT_CURSOR = "result_cursor";

	@Autowired
	DataSource ds;

	JdbcTemplate jdbc;

	Map<String, Object> params = new LinkedHashMap<>();

	protected StoredProcedure sp;

	protected abstract String spName();

	@BeforeClass
	public static void disableLog() {
		DisableLog.disable();
	}
	
	@Before
	public void setUp() {
		jdbc = new JdbcTemplate(ds);
		sp = new StoredProcedure(ds, spName()) {
		};
	}

	@Test
	public void gen() {

		System.out.format("@TransactionalService public class Sp%s extends ConventionalStoreProcedure<ParamIn, ParamOut> {\n", CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, spName()));
		System.out.format("public String spName() {return\"%s\";}", spName());

		System.out.println("public static class ParamIn {");
		new genParams().gen();
		System.out.println("}");

		System.out.println("public static class ParamOut {");
		new genResult().gen();
		System.out.println("}");
		System.out.println("}");

	}

	private class genParams {
		void gen() {
			String sql = "SELECT text FROM all_source WHERE lower(name) = ? ORDER BY line";
			List<String> list = jdbc.queryForList(sql, String.class, spName().toLowerCase());

			if (list.isEmpty()) {
				throw new IllegalArgumentException("Not found SP:" + spName());
			}

			list = new ArrayList<String>(Collections2.filter(list, new Predicate<String>() {
				public boolean apply(String p) {
					return !p.trim().startsWith("--");
				}
			}));

			String context = Joiner.on(" ").join(list);
			// remove comment
			context = context.replaceAll("(?s)/\\*.+?\\*/", "");
			String paramContext = context.substring(context.indexOf('(') + 1, context.indexOf(')'));
			String[] params = paramContext.split(",");
			int order = 0;

			for (String param : params) {
				List<String> parts = splitParam(param);

				int partCount = 3;
				if (parts.size() != partCount) {
					System.err.println("Unknown:" + parts);
				}

				if (parts.get(1).equalsIgnoreCase("in")) {

					String name = convertName(parts.get(0));
					String type = type(parts.get(0), parts.get(2));

					System.out.format("@Order(%d)\n", order += 100);
					System.out.format("%s %s;\n", type, name);
					System.out.println();
				}
			}
		}

		private List<String> splitParam(String param) {

			List<String> parts = Arrays.asList(param.trim().split(" |\t"));

			return new ArrayList<String>(Collections2.filter(parts, new Predicate<String>() {
				public boolean apply(String p) {
					return !p.trim().isEmpty();
				}
			}));

		}

		private String type(String name, String type) {
			if ("date".equalsIgnoreCase(type)) {
				sp.declareParameter(new SqlParameter(name, OracleTypes.DATE));
				params.put(name, new Date());
				return "Date";
			} else if ("varchar".equalsIgnoreCase(type) || "varchar2".equalsIgnoreCase(type)) {
				sp.declareParameter(new SqlParameter(name, OracleTypes.VARCHAR));
				params.put(name, "1");
				return "String";
			} else if ("char".equalsIgnoreCase(type)) {
				sp.declareParameter(new SqlParameter(name, OracleTypes.CHAR));
				params.put(name, "1");
				return "String";
			} else if ("nvarchar".equalsIgnoreCase(type) || "nvarchar2".equalsIgnoreCase(type)) {
				sp.declareParameter(new SqlParameter(name, OracleTypes.NVARCHAR));
				params.put(name, "1");
				return "String";
			} else if ("nchar".equalsIgnoreCase(type)) {
				sp.declareParameter(new SqlParameter(name, OracleTypes.NCHAR));
				params.put(name, "1");
				return "String";
			} else if ("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type)) {
				sp.declareParameter(new SqlParameter(name, OracleTypes.INTEGER));
				params.put(name, 1);
				return "Integer";
			} else if ("number".equalsIgnoreCase(type)) {
				sp.declareParameter(new SqlParameter(name, OracleTypes.NUMBER));
				params.put(name, BigDecimal.ONE);
				return "BigDecimal";
			} else if ("clob".equalsIgnoreCase(type)) {
				sp.declareParameter(new SqlParameter(name, OracleTypes.CLOB));
				params.put(name, "1");
				return "@Lob\nString";
			} else if (StringUtils.endsWithIgnoreCase(type, "%TYPE")) {
				return referType(name, type);
			}

			throw new UnsupportedOperationException("Unknown Type:" + type);
		}

		private String referType(String name, String type) {
			String[] part = type.substring(0, type.length() - 5).split("\\.");
			String table = part[0];
			String field = part[1];

			String sql = "select data_type from user_tab_columns where lower(TABLE_NAME) = ? and lower(COLUMN_NAME)=?";
			String actulType = jdbc.queryForObject(sql, String.class, table.toLowerCase(), field.toLowerCase());
			return type(name, actulType);
		}
	}

	private class genResult {

		protected void setAutoCommitFalse() {
			try {
				sp.getJdbcTemplate().getDataSource().getConnection().setAutoCommit(false);
			} catch (SQLException e) {
				Throwables.propagate(e);
			}
		}

		void gen() {
			sp.declareParameter(new SqlOutParameter(RESULT_CURSOR, OracleTypes.CURSOR, new ResultSetExtractor<Object>() {
				@Override
				public Object extractData(java.sql.ResultSet resultset) throws SQLException, DataAccessException {
					ResultSetMetaData rsmd = resultset.getMetaData();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						String type = type(rsmd.getColumnType(i));
						String name = convertName(rsmd.getColumnName(i));
						System.out.format("%s %s;\n", type, name);
					}

					return null;
				}
			}));

			setAutoCommitFalse();
			sp.execute(params);
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
	}

	private static String convertName(String dbColName) {
		String name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, dbColName);
		char first = Character.toLowerCase(name.charAt(0));
		return first + name.substring(1);
	}

}
