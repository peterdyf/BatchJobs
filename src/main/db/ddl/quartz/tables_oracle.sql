--
-- A hint submitted by a user: Oracle DB MUST be created as "shared" and the 
-- job_queue_processes parameter  must be greater than 2
-- However, these settings are pretty much standard after any
-- Oracle install, so most users need not worry about this.
--
-- Many other users (including the primary author of Quartz) have had success
-- runing in dedicated mode, so only consider the above as a hint ;-)
--

delete from qz2_fired_triggers;
delete from qz2_simple_triggers;
delete from qz2_simprop_triggers;
delete from qz2_cron_triggers;
delete from qz2_blob_triggers;
delete from qz2_triggers;
delete from qz2_job_details;
delete from qz2_calendars;
delete from qz2_paused_trigger_grps;
delete from qz2_locks;
delete from qz2_scheduler_state;

drop table qz2_calendars;
drop table qz2_fired_triggers;
drop table qz2_blob_triggers;
drop table qz2_cron_triggers;
drop table qz2_simple_triggers;
drop table qz2_simprop_triggers;
drop table qz2_triggers;
drop table qz2_job_details;
drop table qz2_paused_trigger_grps;
drop table qz2_locks;
drop table qz2_scheduler_state;


CREATE TABLE qz2_job_details
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    JOB_CLASS_NAME   VARCHAR2(250) NOT NULL, 
    IS_DURABLE VARCHAR2(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR2(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR2(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NOT NULL,
    JOB_DATA BLOB NULL,
    CONSTRAINT qz2_JOB_DETAILS_PK PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);
CREATE TABLE qz2_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    NEXT_FIRE_TIME NUMBER(13) NULL,
    PREV_FIRE_TIME NUMBER(13) NULL,
    PRIORITY NUMBER(13) NULL,
    TRIGGER_STATE VARCHAR2(16) NOT NULL,
    TRIGGER_TYPE VARCHAR2(8) NOT NULL,
    START_TIME NUMBER(13) NOT NULL,
    END_TIME NUMBER(13) NULL,
    CALENDAR_NAME VARCHAR2(200) NULL,
    MISFIRE_INSTR NUMBER(2) NULL,
    JOB_DATA BLOB NULL,
    CONSTRAINT qz2_TRIGGERS_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qz2_TRIGGER_TO_JOBS_FK FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP) 
      REFERENCES qz2_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP) 
);
CREATE TABLE qz2_simple_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    REPEAT_COUNT NUMBER(7) NOT NULL,
    REPEAT_INTERVAL NUMBER(12) NOT NULL,
    TIMES_TRIGGERED NUMBER(10) NOT NULL,
    CONSTRAINT qz2_SIMPLE_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qz2_SIMPLE_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES qz2_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qz2_cron_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID VARCHAR2(80),
    CONSTRAINT qz2_CRON_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qz2_CRON_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
      REFERENCES qz2_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qz2_simprop_triggers
  (          
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    STR_PROP_1 VARCHAR2(512) NULL,
    STR_PROP_2 VARCHAR2(512) NULL,
    STR_PROP_3 VARCHAR2(512) NULL,
    INT_PROP_1 NUMBER(10) NULL,
    INT_PROP_2 NUMBER(10) NULL,
    LONG_PROP_1 NUMBER(13) NULL,
    LONG_PROP_2 NUMBER(13) NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR2(1) NULL,
    BOOL_PROP_2 VARCHAR2(1) NULL,
    CONSTRAINT qz2_SIMPROP_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qz2_SIMPROP_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
      REFERENCES qz2_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qz2_blob_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    CONSTRAINT qz2_BLOB_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qz2_BLOB_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES qz2_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qz2_calendars
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    CALENDAR_NAME  VARCHAR2(200) NOT NULL, 
    CALENDAR BLOB NOT NULL,
    CONSTRAINT qz2_CALENDARS_PK PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);
CREATE TABLE qz2_paused_trigger_grps
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL, 
    CONSTRAINT qz2_PAUSED_TRIG_GRPS_PK PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);
CREATE TABLE qz2_fired_triggers 
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    ENTRY_ID VARCHAR2(95) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    FIRED_TIME NUMBER(13) NOT NULL,
    SCHED_TIME NUMBER(13) NOT NULL,
    PRIORITY NUMBER(13) NOT NULL,
    STATE VARCHAR2(16) NOT NULL,
    JOB_NAME VARCHAR2(200) NULL,
    JOB_GROUP VARCHAR2(200) NULL,
    IS_NONCONCURRENT VARCHAR2(1) NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NULL,
    CONSTRAINT qz2_FIRED_TRIGGER_PK PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);
CREATE TABLE qz2_scheduler_state 
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
    CHECKIN_INTERVAL NUMBER(13) NOT NULL,
    CONSTRAINT qz2_SCHEDULER_STATE_PK PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);
CREATE TABLE qz2_locks
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    LOCK_NAME  VARCHAR2(40) NOT NULL, 
    CONSTRAINT qz2_LOCKS_PK PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

create index idx_qz2_j_req_recovery on qz2_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index idx_qz2_j_grp on qz2_job_details(SCHED_NAME,JOB_GROUP);

create index idx_qz2_t_j on qz2_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qz2_t_jg on qz2_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qz2_t_c on qz2_triggers(SCHED_NAME,CALENDAR_NAME);
create index idx_qz2_t_g on qz2_triggers(SCHED_NAME,TRIGGER_GROUP);
create index idx_qz2_t_state on qz2_triggers(SCHED_NAME,TRIGGER_STATE);
create index idx_qz2_t_n_state on qz2_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qz2_t_n_g_state on qz2_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qz2_t_next_fire_time on qz2_triggers(SCHED_NAME,NEXT_FIRE_TIME);
create index idx_qz2_t_nft_st on qz2_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index idx_qz2_t_nft_misfire on qz2_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index idx_qz2_t_nft_st_misfire on qz2_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qz2_t_nft_st_misfire_grp on qz2_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

create index idx_qz2_ft_trig_inst_name on qz2_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index idx_qz2_ft_inst_job_req_rcvry on qz2_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index idx_qz2_ft_j_g on qz2_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qz2_ft_jg on qz2_fired_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qz2_ft_t_g on qz2_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qz2_ft_tg on qz2_fired_triggers(SCHED_NAME,TRIGGER_GROUP);


