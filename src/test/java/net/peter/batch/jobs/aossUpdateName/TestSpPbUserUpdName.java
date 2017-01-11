package net.peter.batch.jobs.aossUpdateName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import net.peter.test.DbConfig4Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DbConfig4Test.class, SpPbUserUpdName.class })
@Transactional
@ActiveProfiles("test")
public class TestSpPbUserUpdName {

	@Autowired
	SpPbUserUpdName sp;

	@Autowired
	DSLContext sql;

	@Test
	public void test() {

		String rmid = "HKIE7019762";
		String nm = "HZ HZZ UZZH";
		String tcnm = "HZ HZZ UZZH";
		sp.update(new SpPbUserUpdName.UpdNameParam(rmid, nm, tcnm, tcnm));

		assertThat("Update nm", selectNm(rmid), equalTo(nm));
		assertThat("Update tcnm", selectNm(rmid), equalTo(tcnm));
		assertThat("Update scnm", selectNm(rmid), equalTo(tcnm));
	}

	private String selectNm(String rmid) {
		return sql.select().from("TBL_PB_USER").where("RMID = ?", rmid).and("IS_CLS = 0").fetchOne(DSL.field("NM", String.class));
	}

}