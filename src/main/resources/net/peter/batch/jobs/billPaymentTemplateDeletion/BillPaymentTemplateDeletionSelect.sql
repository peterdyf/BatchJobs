SELECT
  	TBL_PB_USER.RMID,
    TBL_PB_USER.ib_acct_no,
	tbl_pb_bill_pymt_tmp.id,
	tbl_pb_bill_pymt_tmp.template_nm,
 	tbl_ib_mchnt_cat.dscp AS cat_desc,
	tbl_ib_mchnt.eng_dscp AS mchnt_desc,
	tbl_pb_bill_pymt_tmp.bill_no ,
			TBL_IB_MCHNT_BILL.bill_type_cd  || '-' || TBL_IB_MCHNT_BILL.bill_type_eng_dscp AS bill_type_desc,
			TBL_IB_MCHNT_BILL.bill_acct_eng_dscp AS bill_acc_desc
	FROM tbl_pb_bill_pymt_tmp
     INNER JOIN TBL_PB_USER
      ON TBL_PB_USER.id=tbl_pb_bill_pymt_tmp.PB_USER_ID and TBL_PB_USER.IS_CLS=0
   	INNER JOIN TBL_IB_MCHNT
		ON TBL_PB_BILL_PYMT_TMP.ib_mchnt_id = TBL_IB_MCHNT.id
		INNER JOIN TBL_IB_MCHNT_CAT
		ON TBL_IB_MCHNT.ib_mchnt_cat_id = TBL_IB_MCHNT_CAT.id
		LEFT OUTER JOIN TBL_IB_MCHNT_BILL
			ON TBL_IB_MCHNT.id = TBL_IB_MCHNT_BILL.ib_mchnt_id AND
			TBL_IB_MCHNT_BILL.is_del = 0 AND
			TBL_IB_MCHNT_BILL.is_enabled = 1
			AND (
			(TBL_IB_MCHNT_BILL.bill_type_cd = TBL_PB_BILL_PYMT_TMP.bill_type_cd)
			OR (TBL_IB_MCHNT_BILL.bill_type_cd IS NULL AND TBL_PB_BILL_PYMT_TMP.bill_type_cd IS NULL)	)
	WHERE TBL_PB_BILL_PYMT_TMP.is_del = 0 AND
		(exists (select * from tbl_pb_acct where tbl_pb_acct.id = TBL_PB_BILL_PYMT_TMP.fr_pb_acct_id and is_del = 0) OR
		TBL_PB_BILL_PYMT_TMP.fr_pb_acct_id IS NULL)
		and tbl_pb_bill_pymt_tmp.dt_lst_trans <= ?
	