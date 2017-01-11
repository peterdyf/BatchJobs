package net.peter.batch.jobs.tokenMailingAddress;

import org.springframework.stereotype.Service;

import com.cncbinternational.common.service.host.message.TR08Out.AcctNoInfo;

@Service
public class TR08AcctNoAdapter {

	public String toIM(AcctNoInfo acc) {
		return String.format("%03d%04d%010d", intP(acc.getBranchCd()), intP(acc.getOther()), intP(acc.getAcctNo()));
	}
	
	public String toST(AcctNoInfo acc) {
		return String.format("%03d%04d%013d", intP(acc.getBranchCd()), intP(acc.getOther()), intP(acc.getAcctNo()));
	}
	
	public String toCHI(AcctNoInfo acc) {
		return String.format("%03d%09d", intP(acc.getBranchCd()), intP(acc.getAcctNo()));
	}

	private int intP(String str) {
		return Integer.parseInt(str);
	}

}
