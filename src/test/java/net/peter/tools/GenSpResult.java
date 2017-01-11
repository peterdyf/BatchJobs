package net.peter.tools;

import static com.cncbinternational.spring.apis.Bean.toSpParams;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import net.peter.test.DbConfig4Test;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cncbinternational.spring.constant.ProfileNames;
import com.cncbinternational.spring.template.ConventionalStoreProcedure;
import com.google.common.base.CaseFormat;

import oracle.jdbc.OracleTypes;

/**
 * 
 * Import com.cncbinternational.tools.GenSpResult.ForGen.ParamOut<br>
 * would Cause compile error 'cannot find symbol'.<br>
 * Perhaps because this class never use 'ParamOut'<br>
 * and it lost due to Type Erasure by compiler<br>
 * 
 * @author Peter.DI
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DbConfig4Test.class, GenSpResult.ForGen.class })
@Transactional
@ActiveProfiles(ProfileNames.TEST)
public abstract class GenSpResult {

	@Autowired
	ForGen sp;

	protected abstract GenSpResultWorkspace.ParamIn paramIn();

	@BeforeClass
	public static void disableLog() {
		DisableLog.disable();
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // tools
	public void gen() {
		sp.gen(paramIn());
	}

	@Service
	public static class ForGen extends ConventionalStoreProcedure<GenSpResultWorkspace.ParamIn, GenSpResult.ForGen.ParamOut> {

		public String spName() {
			return GenSpResultWorkspace.SP_NAME;
		}

		public static class ParamOut {
		}

		@Override
		protected void declareOutParameter(StoredProcedure sp) {
			sp.declareParameter(new SqlOutParameter(RESULT_CURSOR, OracleTypes.CURSOR, new ResultSetExtractor<Object>() {
				@Override
				public Object extractData(java.sql.ResultSet resultset) throws SQLException, DataAccessException {
					ResultSetMetaData rsmd = resultset.getMetaData();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						String type = getType(rsmd.getColumnType(i));
						String name = getName(rsmd.getColumnName(i));
						System.out.format("%s %s;\n", type, name);
					}

					return null;
				}
			}));
		}

		private static String getType(int type) {
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

		private static String getName(String dbColName) {
			String name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, dbColName);
			char first = Character.toLowerCase(name.charAt(0));
			return first + name.substring(1);
		}

		@Override
		protected List<ParamOut> execute(GenSpResultWorkspace.ParamIn params) {
			sp.execute(toSpParams(params));
			return null;
		}

		public void gen(GenSpResultWorkspace.ParamIn params) {
			this.execute(params);
		}
	}

}
