select tbl_pb_fd_plcmnt.id,
	   tbl_pb_fd_plcmnt.pb_user_id,
       tbl_pb_user.rmid,
       tbl_pb_user.ib_acct_no
FROM tbl_pb_fd_plcmnt
INNER JOIN tbl_pb_user ON
tbl_pb_user.id=tbl_pb_fd_plcmnt.pb_user_id
WHERE 	tbl_pb_fd_plcmnt.sys_const_trx_status_cd = 'AF'
AND 	tbl_pb_user.sys_const_ib_status_cd != 'CLS'
AND	(tbl_pb_user.sys_const_ib_status_cd !='SPD' OR tbl_pb_user.sys_const_ib_status_reason_cd !='B0010')
ORDER BY tbl_pb_fd_plcmnt.dt_created