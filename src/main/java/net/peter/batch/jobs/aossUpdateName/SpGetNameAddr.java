package net.peter.batch.jobs.aossUpdateName;

import org.springframework.stereotype.Service;

import net.peter.batch.jobs.aossUpdateName.SpGetNameAddr.NameAddr;
import net.peter.batch.jobs.aossUpdateName.SpGetNameAddr.SpGetNameAddrParamIn;
import com.cncbinternational.spring.annotation.Order;
import com.cncbinternational.spring.template.ConventionalStoreProcedure4MssqlSingleResult;

@Service
public class SpGetNameAddr extends ConventionalStoreProcedure4MssqlSingleResult<SpGetNameAddrParamIn, NameAddr> {

	public String spName() {
		return "SP_GET_NAMEADDR";
	}

	public static class SpGetNameAddrParamIn {

		@Order(100)
		String appId;

		@Order(200)
		String key;

		public SpGetNameAddrParamIn(String appId, String key) {
			this.appId = appId;
			this.key = key;
		}
	}

	public static class NameAddr {

		@Order(100)
		String versionNo;

		@Order(200)
		String tpLang;

		@Order(300)
		String name1;

		@Order(400)
		String name2;

		@Order(500)
		String addrL1;

		@Order(600)
		String addrL2;

		@Order(700)
		String addrL3;

		@Order(800)
		String addrL4;

		public String getVersionNo() {
			return versionNo;
		}

		public String getTpLang() {
			return tpLang;
		}

		public String getName1() {
			return name1;
		}

		public String getName2() {
			return name2;
		}

		public String getAddrL1() {
			return addrL1;
		}

		public String getAddrL2() {
			return addrL2;
		}

		public String getAddrL3() {
			return addrL3;
		}

		public String getAddrL4() {
			return addrL4;
		}
	}

	public NameAddr get(String rmid) {
		return execute4SingleResult(new SpGetNameAddrParamIn("RM", rmid));
	}

}