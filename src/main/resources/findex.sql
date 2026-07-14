CREATE TABLE "index_infos" (
	"id"	BIGINT		NOT NULL,
	"index_classification"	VARCHAR		NOT NULL,
	"index_name"	VARCHAR		NOT NULL,
	"employedItems_count"	INTEGER		NOT NULL,
	"base_point_in_time"	DATE		NOT NULL,
	"base_index"	DECIMAL(10,2)		NOT NULL,
	"source_type"	VARCHAR		NOT NULL,
	"favorite"	BOOLEAN		NOT NULL,
	"created_at"	TIMESTAMPTZ		NOT NULL,
	"updated_at"	TIMESTAMPTZ		NOT NULL
);

COMMENT ON COLUMN "index_infos"."index_classification" IS '지수 분류';

COMMENT ON COLUMN "index_infos"."index_name" IS '지수명';

COMMENT ON COLUMN "index_infos"."employedItems_count" IS '포함 종목 수';

COMMENT ON COLUMN "index_infos"."base_point_in_time" IS '기준 시점';

COMMENT ON COLUMN "index_infos"."base_index" IS '기준 지수';

COMMENT ON COLUMN "index_infos"."source_type" IS '데이터 출처';

COMMENT ON COLUMN "index_infos"."favorite" IS '즐겨찾기';

CREATE TABLE "index_data" (
	"id"	BIGINT		NOT NULL,
	"index_info_id"	BIGINT		NOT NULL,
	"base_date"	DATE		NOT NULL,
	"source_type"	VARCHAR		NOT NULL,
	"market_price"	DECIMAL(10,2)		NOT NULL,
	"closing_price"	DECIMAL(10,2)		NOT NULL,
	"high_price"	DECIMAL(10,2)		NOT NULL,
	"low_price"	DECIMAL(10,2)		NOT NULL,
	"versus"	DECIMAL(10,2)		NOT NULL,
	"fluctuation_rate"	DECIMAL(10,2)		NOT NULL,
	"trading_quantity"	BIGINT		NOT NULL,
	"trading_price"	BIGINT		NOT NULL,
	"market_total_amount"	BIGINT		NOT NULL,
	"created_at"	TIMESTAMPTZ		NOT NULL,
	"updated_at"	TIMESTAMPTZ		NOT NULL
);

COMMENT ON COLUMN "index_data"."index_info_id" IS '지수 정보 ID';

COMMENT ON COLUMN "index_data"."base_date" IS '기준일';

COMMENT ON COLUMN "index_data"."source_type" IS '데이터출처';

COMMENT ON COLUMN "index_data"."marcket_price" IS '시장가';

COMMENT ON COLUMN "index_data"."closing_price" IS '종가';

COMMENT ON COLUMN "index_data"."high_price" IS '고가';

COMMENT ON COLUMN "index_data"."low_price" IS '저가';

COMMENT ON COLUMN "index_data"."versus" IS '전일 대비';

COMMENT ON COLUMN "index_data"."fluctuation_rate" IS '등락률';

COMMENT ON COLUMN "index_data"."trading_quantity" IS '거래량';

COMMENT ON COLUMN "index_data"."trading_price" IS '거래대금';

COMMENT ON COLUMN "index_data"."market_total_amount" IS '시가총액';

CREATE TABLE "auto_sync_configs" (
	"id"	BIGINT		NOT NULL,
	"index_info_id"	BIGINT		NOT NULL,
	"enabled"	BOOLEAN		NOT NULL,
	"created_at"	TIMESTAMP		NOT NULL,
	"updated_at"	TIMESTAMP		NOT NULL
);

COMMENT ON COLUMN "auto_sync_configs"."index_info_id" IS '지수 정보 ID';

COMMENT ON COLUMN "auto_sync_configs"."enabled" IS '자동 연동 활성화 여부';

COMMENT ON COLUMN "auto_sync_configs"."created_at" IS '생성일';

COMMENT ON COLUMN "auto_sync_configs"."updated_at" IS '수정일';

CREATE TABLE "sync_jobs" (
	"id"	BIGINT		NOT NULL,
	"index_info_id"	BIGINT		NOT NULL,
	"target_date"	DATE		NULL,
	"worker"	VARCHAR		NOT NULL,
	"job_time"	DATETIME		NOT NULL,
	"result"	VARCHAR		NOT NULL,
	"created_at"	DATETIME		NOT NULL,
	"job_type"	VARCHAR		NOT NULL
);

COMMENT ON COLUMN "sync_jobs"."index_info_id" IS '대상 지수 정보 ID';

COMMENT ON COLUMN "sync_jobs"."target_date" IS '대상 날짜';

COMMENT ON COLUMN "sync_jobs"."worker" IS '요청 ip or system';

COMMENT ON COLUMN "sync_jobs"."job_time" IS '작업 일시';

COMMENT ON COLUMN "sync_jobs"."result" IS 'SUCCESS, FAILED';

COMMENT ON COLUMN "sync_jobs"."created_at" IS '생성일';

COMMENT ON COLUMN "sync_jobs"."job_type" IS 'INDEX_INFO, INDEX_DATA';

ALTER TABLE "index_infos" ADD CONSTRAINT "PK_INDEX_INFOS" PRIMARY KEY (
	"id"
);

ALTER TABLE "index_data" ADD CONSTRAINT "PK_INDEX_DATA" PRIMARY KEY (
	"id"
);

ALTER TABLE "auto_sync_configs" ADD CONSTRAINT "PK_AUTO_SYNC_CONFIGS" PRIMARY KEY (
	"id"
);

ALTER TABLE "sync_jobs" ADD CONSTRAINT "PK_SYNC_JOBS" PRIMARY KEY (
	"id"
);

