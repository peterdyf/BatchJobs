select u.rmid, u.nm name, NEW_EMAIL_ADDRESS email, RM_RESPONSE_CODE code, RM_RESPONSE_DESC msg, RM_RESPONSE_DT response_date from TBL_PB_PREFERENCE_ESB_RESP t join tbl_pb_user u on u.id = t.PB_USER_ID 
where trunc(create_dt) = trunc(?) and (rm_response_code != '00' or rm_response_code is null)