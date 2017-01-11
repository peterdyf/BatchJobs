package net.peter.batch.jobs.tokenMailingAddress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import com.cncbinternational.common.service.host.message.TR08Out.AcctNoInfo;

public class TestTR08AcctNoAdapter {
	TR08AcctNoAdapter adapter = new TR08AcctNoAdapter();

	@Test
	public void testIM() {
		AcctNoInfo acc = new AcctNoInfo();
		acc.setBranchCd("0694");
		acc.setOther("0000");
		acc.setAcctNo("0210043900");
		assertThat("im", adapter.toIM(acc), equalTo("69400000210043900"));

	}
	
	@Test
	public void testST() {
		AcctNoInfo acc = new AcctNoInfo();
		acc.setBranchCd("0695");
		acc.setOther("0000");
		acc.setAcctNo("00000119356800");
		assertThat("st", adapter.toST(acc), equalTo("69500000000119356800"));

	}
	
	
	@Test
	public void testCHI() {
		AcctNoInfo acc = new AcctNoInfo();
		acc.setBranchCd("0695");
		acc.setAcctNo("0216882600");
		assertThat("chi", adapter.toCHI(acc), equalTo("695216882600"));

	}
}
