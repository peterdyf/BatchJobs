package net.peter.batch.jobs.tokenMailingAddress;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cncbinternational.spring.annotation.Order;

public class RmidFile {

	@Order(10)
	private String rmid;

	public RmidFile() {
		// for reflect
	}

	public RmidFile(String rmid) {
		this.rmid = rmid;
	}

	public String getRmid() {
		return rmid;
	}

	public void setRmid(String rmid) {
		this.rmid = rmid;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
