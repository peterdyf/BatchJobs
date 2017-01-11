select u.rmid, u.nm name, NEW_EMAIL_ADDRESS email, CC_RESPONSE_CODE code, CC_RESPONSE_DESC msg, CC_RESPONSE_DT response_date from TBL_PB_PREFERENCE_ESB_RESP t join tbl_pb_user u on u.id = t.PB_USER_ID 
where trunc(create_dt) = trunc(?) and cc_response_code != '00'