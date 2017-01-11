package net.peter.batch.jobs.aossUpdateMobile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Random;

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
@ContextConfiguration(classes = { DbConfig4Test.class, SpIbTfaOtpRegUpdate.class })
@Transactional
@ActiveProfiles("test")
public class TestSpIbTfaOtpRegUpdate {

	@Autowired
	SpIbTfaOtpRegUpdate sp;

	@Autowired
	DSLContext sql;

	@Test
	public void testUpdateForAOSS() {

		String accNo = "000000338913";
		String mobile = String.valueOf(new Random().nextInt(10000000));
		sp.updateForAOSS(accNo, mobile, "");

		assertThat("Mobile", selectMobile(accNo), equalTo(mobile));
	}

	private String selectMobile(String accNo) {
		return sql.select().from("TBL_TFA_USER_INFO").where("IB_ACCT_NO = ?", accNo).fetchOne(DSL.field("OTP_MB_NO", String.class));
	}

}