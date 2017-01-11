package net.peter.batch.jobs.futureTimeDeposit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cncbinternational.common.service.host.ME07Service;
import com.cncbinternational.common.service.host.message.ME07Out;
import com.cncbinternational.spring.annotation.Loggable;

@Service
public class FutureTimeDepositService {
	
	@Autowired
	private ME07Service me07Service;
	
	@Loggable
	public ME07Out recordsFromHost(String rmid) {
		return me07Service.send(rmid);
	}
}
