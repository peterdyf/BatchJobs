package net.peter.batch.jobs.aossUpdateEmail;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cncbinternational.spring.annotation.Order;

class AossUpdateEmailFileData {
	@Order(100)
	String rmid;
	@Order(200)
	String emailAddr;

	public void setRmid(String rmid) {
		this.rmid = rmid;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getRmid() {
		return rmid;
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(rmid).append(StringUtils.lowerCase(emailAddr)).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AossUpdateEmailFileData rhs = (AossUpdateEmailFileData) obj;
		return new EqualsBuilder().append(rmid, rhs.rmid).append(StringUtils.lowerCase(emailAddr), StringUtils.lowerCase(rhs.emailAddr)).isEquals();
	}

}
