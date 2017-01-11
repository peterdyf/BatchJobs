package net.peter.batch.jobs.tokenMailingAddress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Optional;

import net.peter.batch.service.JdbcService;
import org.junit.Before;
import org.junit.Test;

import com.cncbinternational.common.service.host.TR08Service;
import com.cncbinternational.common.service.host.message.TR08Out;
import com.cncbinternational.common.service.host.message.TR08Out.AcctNoInfo;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class TestTokenMailingAddressMatchingService {
	TR08Service tr08Service = mock(TR08Service.class);
	JdbcService jdbcService = mock(JdbcService.class);
	TR08AcctNoAdapter tr08AcctNoAdapter = new TR08AcctNoAdapter(){

		@Override
		public String toIM(AcctNoInfo acc) {
			return acc.getAcctNo();
		}
		
		@Override
		public String toST(AcctNoInfo acc) {
			return acc.getAcctNo();
		}
		
		@Override
		public String toCHI(AcctNoInfo acc) {
			return acc.getAcctNo();
		}
	};

	TokenMailingAddressMatchingService service = new TokenMailingAddressMatchingService(tr08Service, tr08AcctNoAdapter, jdbcService);

	private static final String RMID = "TestRmid";
	private static final String AC_NO_1 = "TestAcNo1";
	private static final String AC_NO_2 = "TestAcNo2";
	private static final String AC_NO_3 = "TestAcNo3";
	private static final String AC_NO_4 = "TestAcNo4";

	private static final String NAME_1 = "TestName1";
	private static final String NAME_2 = "TestName2";
	private static final String NAME_3 = "TestName3";
	private static final String NAME_4 = "TestName4";
	private static final String FULL_NAME = "TestFullName";

	private static final String ADDR_1 = "TestAddr1";
	private static final String ADDR_2 = "TestAddr2";
	private static final String ADDR_3 = "TestAddr3";
	private static final String ADDR_4 = "TestAddr4";
	private static final String ADDR_5 = "TestAddr5";
	private static final String ADDR_6 = "TestAddr6";

	private static final String CITY = "TestCity";

	@Before
	public void setUp() {
		reset(jdbcService);
		reset(tr08Service);

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CF, CfDb.class, RMID)).thenReturn(Optional.empty());
		when(jdbcService.query(TokenMailingAddressMatchingService.SQL_CC, CcDb.class, RMID)).thenReturn(ImmutableList.of());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CC, CcDb.class, RMID)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_RM, ChiDb.class, RMID)).thenReturn(Optional.empty());
		when(tr08Service.send(RMID)).thenReturn(new TR08Out() {
			{
				setAcctNoInfos(ImmutableList.of());
			}
		});
	}

	@Test
	public void testUnmatched() {
		MatchRet ret = service.match(RMID);
		assertThat("rmid", ret.getRmid(), equalTo(RMID));
		assertThat("ret", ret.getRet(), equalTo("N"));
	}

	@Test
	public void testChiCC() {
		CcDb cc1 = new CcDb();
		cc1.setAcNo(AC_NO_1);

		CcDb cc2 = new CcDb();
		cc2.setAcNo(AC_NO_2);

		CcDb cc3 = new CcDb();
		cc2.setAcNo(AC_NO_3);

		ChiDb chi2 = new ChiDb() {
			{
				setNaKey(AC_NO_2);
				setName1(NAME_1);
				setName2(NAME_2);
				setAddr1(ADDR_1);
				setAddr2(ADDR_2);
				setAddr3(ADDR_3);
				setAddr4(ADDR_4);
			}
		};

		when(jdbcService.query(TokenMailingAddressMatchingService.SQL_CC, CcDb.class, RMID)).thenReturn(ImmutableList.of(cc1, cc2, cc3));
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "CC", AC_NO_1)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "CC", AC_NO_2)).thenReturn(Optional.of(chi2));
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "CC", AC_NO_3)).thenReturn(Optional.of(chi2));

		MatchRet ret = service.match(RMID);
		assertThat("rmid", ret.getRmid(), equalTo(RMID));
		assertThat("acNo", ret.getAcNo(), equalTo(AC_NO_2));
		assertThat("category", ret.getCategory(), equalTo("CC"));
		assertThat("name", ret.getName(), equalTo(NAME_1 + " " + NAME_2));
		assertThat("addr", ret.getAddr(), equalTo(ADDR_1 + " " + ADDR_2 + " " + ADDR_3 + " " + ADDR_4));
		assertThat("ret", ret.getRet(), equalTo("Y"));
	}

	@Test
	public void testChiIM() {

		AcctNoInfo acc1 = new AcctNoInfo();
		acc1.setAppId("ST");
		acc1.setAcctNo(AC_NO_1);

		AcctNoInfo acc2 = new AcctNoInfo();
		acc2.setAppId("IM");
		acc2.setAcctNo(AC_NO_2);

		AcctNoInfo acc3 = new AcctNoInfo();
		acc3.setAppId("IM");
		acc3.setAcctNo(AC_NO_3);

		AcctNoInfo acc4 = new AcctNoInfo();
		acc3.setAppId("IM");
		acc3.setAcctNo(AC_NO_4);

		when(tr08Service.send(RMID)).thenReturn(new TR08Out() {
			{
				setAcctNoInfos(ImmutableList.of(acc1, acc2, acc3, acc4));
			}
		});

		ChiDb chi2 = new ChiDb() {
			{
				setNaKey(AC_NO_2);
				setName1(NAME_1);
				setName2(NAME_2);
				setAddr1(ADDR_1);
				setAddr2(ADDR_2);
				setAddr3(ADDR_3);
				setAddr4(ADDR_4);
			}
		};

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "IM", AC_NO_2)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "IM", AC_NO_3)).thenReturn(Optional.of(chi2));
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "IM", AC_NO_4)).thenReturn(Optional.of(chi2));

		MatchRet ret = service.match(RMID);
		assertThat("rmid", ret.getRmid(), equalTo(RMID));
		assertThat("acNo", ret.getAcNo(), equalTo(AC_NO_2));
		assertThat("category", ret.getCategory(), equalTo("IM"));
		assertThat("name", ret.getName(), equalTo(NAME_1 + " " + NAME_2));
		assertThat("addr", ret.getAddr(), equalTo(ADDR_1 + " " + ADDR_2 + " " + ADDR_3 + " " + ADDR_4));
		assertThat("ret", ret.getRet(), equalTo("Y"));
	}

	@Test
	public void testChiST() {

		AcctNoInfo acc1 = new AcctNoInfo();
		acc1.setAppId("");
		acc1.setAcctNo(AC_NO_1);

		AcctNoInfo acc2 = new AcctNoInfo();
		acc2.setAppId("ST");
		acc2.setAcctNo(AC_NO_2);

		AcctNoInfo acc3 = new AcctNoInfo();
		acc3.setAppId("ST");
		acc3.setAcctNo(AC_NO_3);

		AcctNoInfo acc4 = new AcctNoInfo();
		acc3.setAppId("ST");
		acc3.setAcctNo(AC_NO_4);

		when(tr08Service.send(RMID)).thenReturn(new TR08Out() {
			{
				setAcctNoInfos(ImmutableList.of(acc1, acc2, acc3, acc4));
			}
		});

		ChiDb chi2 = new ChiDb() {
			{
				setNaKey(AC_NO_2);
				setName1(NAME_1);
				setName2(NAME_2);
				setAddr1(ADDR_1);
				setAddr2(ADDR_2);
				setAddr3(ADDR_3);
				setAddr4(ADDR_4);
			}
		};

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "ST", AC_NO_2)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "ST", AC_NO_3)).thenReturn(Optional.of(chi2));
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "ST", AC_NO_4)).thenReturn(Optional.of(chi2));

		MatchRet ret = service.match(RMID);
		assertThat("rmid", ret.getRmid(), equalTo(RMID));
		assertThat("acNo", ret.getAcNo(), equalTo(AC_NO_2));
		assertThat("category", ret.getCategory(), equalTo("ST"));
		assertThat("name", ret.getName(), equalTo(NAME_1 + " " + NAME_2));
		assertThat("addr", ret.getAddr(), equalTo(ADDR_1 + " " + ADDR_2 + " " + ADDR_3 + " " + ADDR_4));
		assertThat("ret", ret.getRet(), equalTo("Y"));
	}

	@Test
	public void testRM() {

		ChiDb chi1 = new ChiDb() {
			{
				setNaKey(RMID);
				setAppId("RM");
				setName1(NAME_1);
				setName2(NAME_2);
				setAddr1(ADDR_1);
				setAddr2(ADDR_2);
				setAddr3(ADDR_3);
				setAddr4(ADDR_4);
			}
		};

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_RM, ChiDb.class, RMID)).thenReturn(Optional.of(chi1));

		MatchRet ret = service.match(RMID);
		assertThat("rmid", ret.getRmid(), equalTo(RMID));
		assertThat("acNo", ret.getAcNo(), equalTo(""));
		assertThat("category", ret.getCategory(), equalTo("RM"));
		assertThat("name", ret.getName(), equalTo(NAME_1 + " " + NAME_2));
		assertThat("addr", ret.getAddr(), equalTo(ADDR_1 + " " + ADDR_2 + " " + ADDR_3 + " " + ADDR_4));
		assertThat("ret", ret.getRet(), equalTo("Y"));
	}

	@Test
	public void testCF() {
		CfDb cf = new CfDb() {
			{
				setRmid(RMID);
				setName1(NAME_1);
				setName2(NAME_2);
				setAddr1(ADDR_1);
				setAddr2(ADDR_2);
				setAddr3(ADDR_3);
				setAddr4(ADDR_4);
			}
		};

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CF, CfDb.class, RMID)).thenReturn(Optional.of(cf));
		MatchRet ret = service.match(RMID);
		assertThat("rmid", ret.getRmid(), equalTo(RMID));
		assertThat("acNo", ret.getAcNo(), equalTo(""));
		assertThat("category", ret.getCategory(), equalTo("CF"));
		assertThat("name", ret.getName(), equalTo(NAME_1 + " " + NAME_2));
		assertThat("addr", ret.getAddr(), equalTo(ADDR_1 + " " + ADDR_2 + " " + ADDR_3 + " " + ADDR_4));
		assertThat("ret", ret.getRet(), equalTo("Y"));
	}

	@Test
	public void testCC() {
		CcDb cc1 = new CcDb();
		cc1.setAcNo(AC_NO_1);

		CcDb cc2 = new CcDb() {
			{
				setAcNo(AC_NO_2);
				setFullName(FULL_NAME);
				setAddr1(ADDR_1);
				setAddr2(ADDR_2);
				setAddr3(ADDR_3);
				setAddr4(ADDR_4);
			}
		};

		CcDb cc3 = new CcDb();
		cc3.setAcNo(AC_NO_3);

		when(jdbcService.query(TokenMailingAddressMatchingService.SQL_CC, CcDb.class, RMID)).thenReturn(ImmutableList.of(cc1, cc2, cc3));
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CC, CcDb.class, RMID)).thenReturn(Optional.of(cc2));
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "CC", AC_NO_1)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "CC", AC_NO_2)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "CC", AC_NO_3)).thenReturn(Optional.empty());

		MatchRet ret = service.match(RMID);
		assertThat("rmid", ret.getRmid(), equalTo(RMID));
		assertThat("acNo", ret.getAcNo(), equalTo(AC_NO_2));
		assertThat("category", ret.getCategory(), equalTo("CC"));
		assertThat("name", ret.getName(), equalTo(FULL_NAME));
		assertThat("addr", ret.getAddr(), equalTo(ADDR_1 + " " + ADDR_2 + " " + ADDR_3 + " " + ADDR_4));
		assertThat("ret", ret.getRet(), equalTo("Y"));
	}

	@Test
	public void testIM() {

		AcctNoInfo acc1 = new AcctNoInfo();
		acc1.setAppId("");
		acc1.setAcctNo(AC_NO_1);

		AcctNoInfo acc2 = new AcctNoInfo();
		acc2.setAppId("IM");
		acc2.setAcctNo(AC_NO_2);

		AcctNoInfo acc3 = new AcctNoInfo();
		acc3.setAppId("IM");
		acc3.setAcctNo(AC_NO_3);

		AcctNoInfo acc4 = new AcctNoInfo();
		acc3.setAppId("IM");
		acc3.setAcctNo(AC_NO_4);

		when(tr08Service.send(RMID)).thenReturn(new TR08Out() {
			{
				setAcctNoInfos(ImmutableList.of(acc1, acc2, acc3, acc4));
			}
		});

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "IM", AC_NO_2)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "IM", AC_NO_3)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "IM", AC_NO_4)).thenReturn(Optional.empty());

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_IM, ImDb.class, AC_NO_2)).thenReturn(Optional.empty());

		ImDb im3 = new ImDb() {
			{
				setAcNo(AC_NO_3);
				setName1(NAME_1);
				setName2(NAME_2);
				setName3(NAME_3);
				setName4(NAME_4);
				setAddr1(ADDR_1);
				setAddr2(ADDR_2);
				setAddr3(ADDR_3);
				setAddr4(ADDR_4);
				setAddr5(ADDR_5);
				setAddr6(ADDR_6);
				setCity(CITY);
			}
		};

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_IM, ImDb.class, AC_NO_3)).thenReturn(Optional.of(im3));
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_IM, ImDb.class, AC_NO_4)).thenReturn(Optional.of(im3));

		MatchRet ret = service.match(RMID);
		assertThat("rmid", ret.getRmid(), equalTo(RMID));
		assertThat("acNo", ret.getAcNo(), equalTo(AC_NO_3));
		assertThat("category", ret.getCategory(), equalTo("IM"));
		assertThat("name", ret.getName(), equalTo(NAME_1 + " " + NAME_2 + " " + NAME_3 + " " + NAME_4));
		assertThat("addr", ret.getAddr(), equalTo(ADDR_1 + " " + ADDR_2 + " " + ADDR_3 + " " + ADDR_4 + " " + ADDR_5 + " " + ADDR_6 + " " + CITY));
		assertThat("ret", ret.getRet(), equalTo("Y"));
	}

	@Test
	public void testST() {

		AcctNoInfo acc1 = new AcctNoInfo();
		acc1.setAppId("");
		acc1.setAcctNo(AC_NO_1);

		AcctNoInfo acc2 = new AcctNoInfo();
		acc2.setAppId("ST");
		acc2.setAcctNo(AC_NO_2);

		AcctNoInfo acc3 = new AcctNoInfo();
		acc3.setAppId("ST");
		acc3.setAcctNo(AC_NO_3);

		AcctNoInfo acc4 = new AcctNoInfo();
		acc3.setAppId("ST");
		acc3.setAcctNo(AC_NO_4);

		when(tr08Service.send(RMID)).thenReturn(new TR08Out() {
			{
				setAcctNoInfos(ImmutableList.of(acc1, acc2, acc3, acc4));
			}
		});

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "ST", AC_NO_2)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "ST", AC_NO_3)).thenReturn(Optional.empty());
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_CHI, ChiDb.class, "ST", AC_NO_4)).thenReturn(Optional.empty());

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_ST, StDb.class, AC_NO_2)).thenReturn(Optional.empty());

		StDb st3 = new StDb() {
			{
				setAcNo(AC_NO_3);
				setName1(NAME_1);
				setName2(NAME_2);
				setName3(NAME_3);
				setName4(NAME_4);
				setAddr1(ADDR_1);
				setAddr2(ADDR_2);
				setAddr3(ADDR_3);
				setAddr4(ADDR_4);
				setAddr5(ADDR_5);
				setAddr6(ADDR_6);
				setCity(CITY);
			}
		};

		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_ST, StDb.class, AC_NO_3)).thenReturn(Optional.of(st3));
		when(jdbcService.queryOne(TokenMailingAddressMatchingService.SQL_ST, StDb.class, AC_NO_4)).thenReturn(Optional.of(st3));

		MatchRet ret = service.match(RMID);
		assertThat("rmid", ret.getRmid(), equalTo(RMID));
		assertThat("acNo", ret.getAcNo(), equalTo(AC_NO_3));
		assertThat("category", ret.getCategory(), equalTo("ST"));
		assertThat("name", ret.getName(), equalTo(NAME_1 + " " + NAME_2 + " " + NAME_3 + " " + NAME_4));
		assertThat("addr", ret.getAddr(), equalTo(ADDR_1 + " " + ADDR_2 + " " + ADDR_3 + " " + ADDR_4 + " " + ADDR_5 + " " + ADDR_6 + " " + CITY));
		assertThat("ret", ret.getRet(), equalTo("Y"));
	}

}
