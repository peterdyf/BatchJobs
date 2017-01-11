SELECT
	TBL_PB_USER.RMID,
	tbl_pb_fund_txfer_tmp.id,
	tbl_pb_fund_txfer_tmp.template_nm,
	fr_acct_nm.acct_no AS fr_pb_acct_no,
  	fr_acct_nm.eng_dscp AS fr_pb_acct_nm_eng,
	tbl_pb_fund_txfer_tmp.to_acct_no,
  	GET_PB_FUND_TFR_ACCT_DSCP( tbl_pb_fund_txfer_tmp.sys_const_acct_type_cd,
                             tbl_pb_fund_txfer_tmp.pb_user_id,
                             tbl_pb_fund_txfer_tmp.to_acct_no,
                             to_acct_nm.eng_dscp) AS to_pb_acct_nm_eng,
	fr_acct_curr.dscp AS fr_ib_curr_eng_dscp,
	to_acct_curr.dscp AS to_ib_curr_eng_dscp,
	tran_curr.dscp AS tran_curr_eng_dscp,
	tbl_pb_fund_txfer_tmp.note


	FROM tbl_pb_fund_txfer_tmp
     INNER JOIN TBL_PB_USER
      ON TBL_PB_USER.id=tbl_pb_fund_txfer_tmp.PB_USER_ID
  		INNER JOIN tbl_pb_acct fr_acct ON
  			tbl_pb_fund_txfer_tmp.fr_pb_acct_id = fr_acct.id
      INNER JOIN tbl_ib_curr fr_acct_curr ON
          tbl_pb_fund_txfer_tmp.fr_ib_curr_id = fr_acct_curr.id
          AND fr_acct_curr.is_del = 0
      INNER JOIN vw_pb_acc_def_info fr_acct_nm ON
          fr_acct.id = fr_acct_nm.id
  		LEFT OUTER JOIN tbl_pb_acct to_acct ON
  			tbl_pb_fund_txfer_tmp.to_pb_acct_id = to_acct.id
      LEFT OUTER JOIN vw_pb_acc_def_info to_acct_nm ON
          to_acct.id = to_acct_nm.id
      INNER JOIN tbl_ib_curr to_acct_curr ON
          tbl_pb_fund_txfer_tmp.to_ib_curr_id = to_acct_curr.id
          AND to_acct_curr.is_del = 0
      INNER JOIN tbl_ib_curr tran_curr ON
          tbl_pb_fund_txfer_tmp.txfer_ib_curr_id = tran_curr.id
	WHERE tbl_pb_fund_txfer_tmp.is_del = 0 and tbl_pb_fund_txfer_tmp.dt_lst_trans <= ?
	
	