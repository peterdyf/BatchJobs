package net.peter.batch.jobs.tokenMailingAddress;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.peter.batch.service.JdbcService;
import com.cncbinternational.common.service.host.TR08Service;
import com.cncbinternational.common.service.host.message.TR08Out.AcctNoInfo;
import com.cncbinternational.spring.annotation.Loggable;

@Service
@SuppressWarnings("PMD.TooManyMethods")
public class TokenMailingAddressMatchingService {

	static final String SQL_CHI = "select * from TB_FORCE_SUBS_CHI where APP_ID = ? and NA_KEY = ?";
	static final String SQL_RM = "select * from TB_FORCE_SUBS_CHI where APP_ID = 'RM' and NA_KEY = ?";
	static final String SQL_CF = "select * from TB_FORCE_SUBS_CF where RMID = ?";
	static final String SQL_CC = "select * from TB_FORCE_SUBS_CC where ID_NO = ?";
	static final String SQL_IM = "select * from TB_FORCE_SUBS_IM where AC_NO = ?";
	static final String SQL_ST = "select * from TB_FORCE_SUBS_ST where AC_NO = ?";

	private final TR08Service tr08Service;
	private final TR08AcctNoAdapter tr08AcctNoAdapter;
	private final JdbcService jdbcService;

	@Autowired
	public TokenMailingAddressMatchingService(TR08Service tr08Service, TR08AcctNoAdapter tr08AcctNoAdapter, JdbcService jdbcService) {
		this.tr08Service = tr08Service;
		this.tr08AcctNoAdapter = tr08AcctNoAdapter;
		this.jdbcService = jdbcService;
	}

	@Loggable
	public MatchRet match(String rmid) {
		// @formatter:off
		return new Matcher<String, MatchRet>(rmid)
				.match(this::matchChiCC)
				.match(this::matchChiIM)
				.match(this::matchChiST)
				.match(this::matchChiRM)
				.match(this::matchCF)
				.match(this::matchCC)
				.match(this::matchIM)
				.match(this::matchST)
				.get(MatchRet::unmatched);
		//@formatter:on
	}

	private Optional<MatchRet> matchChiCC(String rmid) {
		return recordsFromDb(SQL_CC, CcDb.class, rmid).map(CcDb::getAcNo).map(acNo -> matchChi("CC", acNo)).flatMap(flatOptionalStream()).findFirst().map(chi -> MatchRet.fromChiCC(chi, rmid));
	}

	private Optional<MatchRet> matchChiIM(String rmid) {
		return acctsFromHost(rmid, "IM").map(tr08AcctNoAdapter::toCHI).map(acNo -> matchChi("IM", acNo)).flatMap(flatOptionalStream()).findFirst().map(chi -> MatchRet.fromChiIM(chi, rmid));
	}

	private Optional<MatchRet> matchChiST(String rmid) {
		return acctsFromHost(rmid, "ST").map(tr08AcctNoAdapter::toCHI).map(acNo -> matchChi("ST", acNo)).flatMap(flatOptionalStream()).findFirst().map(chi -> MatchRet.fromChiST(chi, rmid));
	}

	private Optional<MatchRet> matchChiRM(String rmid) {
		return recordFromDb(SQL_RM, ChiDb.class, rmid).map(MatchRet::fromChiRM);
	}

	private Optional<MatchRet> matchCF(String rmid) {
		return recordFromDb(SQL_CF, CfDb.class, rmid).map(MatchRet::fromCF);
	}

	private Optional<MatchRet> matchCC(String rmid) {
		return recordFromDb(SQL_CC, CcDb.class, rmid).map(cc -> MatchRet.fromCC(cc, rmid));
	}

	private Optional<MatchRet> matchIM(String rmid) {
		return acctsFromHost(rmid, "IM").map(tr08AcctNoAdapter::toIM).map(acNo -> recordFromDb(SQL_IM, ImDb.class, acNo)).flatMap(flatOptionalStream()).findFirst()
				.map(im -> MatchRet.fromIM(im, rmid));
	}

	private Optional<MatchRet> matchST(String rmid) {
		return acctsFromHost(rmid, "ST").map(tr08AcctNoAdapter::toST).map(acNo -> recordFromDb(SQL_ST, StDb.class, acNo)).flatMap(flatOptionalStream()).findFirst()
				.map(st -> MatchRet.fromST(st, rmid));
	}

	private Stream<AcctNoInfo> acctsFromHost(String rmid, String appId) {
		return tr08Service.send(rmid).getAcctNoInfos().stream().filter(acct -> appId.equals(acct.getAppId()));
	}

	private Optional<ChiDb> matchChi(String appId, String acNo) {
		return jdbcService.queryOne(SQL_CHI, ChiDb.class, appId, acNo);
	}

	private <O> Optional<O> recordFromDb(String sql, Class<O> rowClass, Object... params) {
		return jdbcService.queryOne(sql, rowClass, params);
	}

	private <O> Stream<O> recordsFromDb(String sql, Class<O> rowClass, Object... params) {
		return jdbcService.query(sql, rowClass, params).stream();
	}

	private <T> Function<Optional<T>, Stream<? extends T>> flatOptionalStream() {
		return o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty();
	}
}
