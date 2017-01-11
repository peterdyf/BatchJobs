select 
TBL_ESB_AOSS_NAME_FLAG.ID, 
TBL_ESB_AOSS_NAME_FLAG.RMID, 
TBL_ESB_AOSS_NAME_FLAG.NAME_FLAG,
TBL_PB_USER.NM
from TBL_ESB_AOSS_NAME_FLAG, TBL_PB_USER
where 1 = 1
and TBL_ESB_AOSS_NAME_FLAG.RMID = TBL_PB_USER.RMID
and (TBL_ESB_AOSS_NAME_FLAG.EXECUTED is null or TBL_ESB_AOSS_NAME_FLAG.EXECUTED != 'Y')
and TBL_PB_USER.IS_CLS = 0