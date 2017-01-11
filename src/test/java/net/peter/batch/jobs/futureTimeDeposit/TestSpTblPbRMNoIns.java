
package net.peter.batch.jobs.futureTimeDeposit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import net.peter.test.DbConfig4Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DbConfig4Test.class, SpTblPbRMNoIns.class })
@Transactional
@ActiveProfiles("test")
public class TestSpTblPbRMNoIns {

	@Autowired
	SpTblPbRMNoIns sp;

	@Autowired
	DSLContext sql;
	
	private static final String RMID = "HKIH9732774";
	private String pbUserIdIn = "";
	private final String rmNumberIn_part1 = "0018000000000";
	private String rmNumberIn_part2 = "";
	private String rmNumberIn = "";
	
	
	@Before
	public void setUp() {
		pbUserIdIn= getPbUserId(RMID);
		rmNumberIn_part2 = getCurrentTimeStamp();
		rmNumberIn = rmNumberIn_part1 + rmNumberIn_part2;
	}
	
	@Test
	public void test() {

		String pbUserIdIn= getPbUserId(RMID);
		String rmNumberIn_part1 = "0018000000000";
		String rmNumberIn_part2 = getCurrentTimeStamp();
		String rmNumberIn = rmNumberIn_part1 + rmNumberIn_part2;
		sp.insert(new SpTblPbRMNoIns.InsRMNoParam(pbUserIdIn, rmNumberIn));

		assertThat("Inserted Records", select(pbUserIdIn, rmNumberIn), equalTo(rmNumberIn));
	}
	
	@After
	public void clear() {
		delete(pbUserIdIn, rmNumberIn);
		assertThat("Deleted Records", select(pbUserIdIn, rmNumberIn), isEmptyOrNullString());
	}
	
	private String getPbUserId(String rmid) {
		return sql.select().from("TBL_PB_USER").where("RMID = ?", rmid).and("IS_CLS = 0").fetchOne(DSL.field("ID", String.class));
	}

	private String select(String pbUserId, String rmNumber) {
		return sql.select().from("TBL_PB_RMNO").where("PB_USER_ID = ?", pbUserId).and("RM_NO = ?", rmNumber).fetchOne(DSL.field("RM_NO", String.class));
	}
	
	private String getCurrentTimeStamp() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}
	
	@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.UnusedPrivateMethod" })
	private void delete(String pbUserId, String rmNumber) {
		sql.delete(DSL.table("TBL_PB_RMNO")).where("PB_USER_ID = ?", pbUserId).and("RM_NO = ?", rmNumber).execute();
	}

}