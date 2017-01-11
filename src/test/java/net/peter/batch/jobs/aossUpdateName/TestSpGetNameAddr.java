package net.peter.batch.jobs.aossUpdateName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import net.peter.test.DbConfig4Test;
import net.peter.test.MssqlDbConfig4Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DbConfig4Test.class, MssqlDbConfig4Test.class, SpGetNameAddr.class })
@Transactional
@ActiveProfiles("test")
public class TestSpGetNameAddr {

	@Autowired
	SpGetNameAddr sp;

	@Test
	public void testString() {

		String rmid = "test";
		assertThat("Return", sp.get(rmid), notNullValue());
	}

}