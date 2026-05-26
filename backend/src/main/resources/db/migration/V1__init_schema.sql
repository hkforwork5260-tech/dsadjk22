-- 채용알리미 v0.1 초기 스키마
-- 모든 시간 컬럼은 UTC TIMESTAMPTZ. 사용자 노출 시점에 KST 변환.

-- ============================================================
-- 1. 회사 (companies)
-- ============================================================
CREATE TABLE companies (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    name_normalized  VARCHAR(255) NOT NULL UNIQUE,
    industry         VARCHAR(100),
    group_name       VARCHAR(100),
    size             VARCHAR(32),
    domain           VARCHAR(255),
    homepage_url     TEXT,
    careers_url      TEXT,
    logo_url         TEXT,
    description      TEXT,
    is_approved      BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_companies_industry ON companies(industry);
CREATE INDEX idx_companies_group    ON companies(group_name);
CREATE INDEX idx_companies_size     ON companies(size);

COMMENT ON COLUMN companies.name_normalized IS '소문자·공백제거 후 정규화. dedup 키.';
COMMENT ON COLUMN companies.size            IS 'large_corp | mid | sme | public | startup_unicorn';
COMMENT ON COLUMN companies.is_approved     IS 'admin 검토 통과한 회사만 노출. FALSE = pending.';

-- ============================================================
-- 2. 회사 별칭 (company_aliases)
--    예: "삼성전자" = "삼성전자(주)" = "Samsung Electronics"
-- ============================================================
CREATE TABLE company_aliases (
    id                 BIGSERIAL PRIMARY KEY,
    company_id         BIGINT      NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    alias              VARCHAR(255) NOT NULL,
    alias_normalized   VARCHAR(255) NOT NULL UNIQUE,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_company_aliases_company ON company_aliases(company_id);

-- ============================================================
-- 3. 직군 마스터 (job_categories) — 21개 고정
-- ============================================================
CREATE TABLE job_categories (
    code        VARCHAR(64) PRIMARY KEY,
    label       VARCHAR(100) NOT NULL,
    sort_order  INT NOT NULL DEFAULT 0
);

INSERT INTO job_categories (code, label, sort_order) VALUES
    ('plan_strategy',        '기획·전략',           1),
    ('marketing_pr',         '마케팅·홍보·조사',     2),
    ('accounting_finance',   '회계·세무·재무',       3),
    ('hr_hrd',               '인사·노무·HRD',        4),
    ('admin_legal',          '총무·법무·사무',       5),
    ('it_dev_data',          'IT개발·데이터',        6),
    ('design',               '디자인',              7),
    ('sales_trade',          '영업·판매·무역',       8),
    ('customer_tm',          '고객상담·TM',         9),
    ('purchase_logistics',   '구매·자재·물류',      10),
    ('md_planning',          '상품기획·MD',         11),
    ('driving_delivery',     '운전·운송·배송',      12),
    ('service',              '서비스',             13),
    ('production',           '생산',               14),
    ('construction',         '건설·건축',          15),
    ('medical',              '의료',               16),
    ('research',             '연구·R&D',           17),
    ('education',            '교육',               18),
    ('media_culture_sport',  '미디어·문화·스포츠',   19),
    ('finance_insurance',    '금융·보험',          20),
    ('public_welfare',       '공공·복지',          21);

-- ============================================================
-- 4. 공고 (jobs)
-- ============================================================
CREATE TABLE jobs (
    id                  VARCHAR(64)  PRIMARY KEY,        -- e.g. 'saramin-46123456' or 'samsung-2026-h1'
    company_id          BIGINT       NOT NULL REFERENCES companies(id),
    source              VARCHAR(32)  NOT NULL,           -- 'saramin' | 'manual' | 'direct'
    source_external_id  VARCHAR(128) NOT NULL,
    title               VARCHAR(500) NOT NULL,
    kind                VARCHAR(16)  NOT NULL,           -- 'NEW' | 'UPDATE' | 'CLOSING' | 'EXPIRED'
    location            VARCHAR(255),
    experience          VARCHAR(64),
    education           VARCHAR(64),
    salary              VARCHAR(255),
    posting_date        TIMESTAMPTZ,
    deadline            TIMESTAMPTZ,
    description         TEXT,
    summary             TEXT,                            -- AI 한줄 요약 (Claude Haiku)
    preferred           JSONB,
    process             JSONB,
    tags                JSONB,
    job_category_codes  JSONB,                           -- ['it_dev_data', 'design']
    original_url        TEXT,
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    first_seen_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    last_seen_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    closed_at           TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (source, source_external_id)
);
CREATE INDEX idx_jobs_company        ON jobs(company_id);
CREATE INDEX idx_jobs_deadline       ON jobs(deadline);
CREATE INDEX idx_jobs_kind           ON jobs(kind);
CREATE INDEX idx_jobs_first_seen     ON jobs(first_seen_at DESC);
CREATE INDEX idx_jobs_active_dl      ON jobs(deadline) WHERE is_active = TRUE;

COMMENT ON COLUMN jobs.kind IS 'NEW=오늘 신규 / UPDATE=기존 갱신 / CLOSING=D-3 이내 / EXPIRED=마감';

-- ============================================================
-- 5. 사용자 (users) — v0.1는 device 기반 익명. v0.5+ 로그인 도입 대비.
-- ============================================================
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) UNIQUE,                    -- v0.5+에서 채워짐
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 6. 디바이스 (devices) — v0.1의 실질적 사용자 식별자
-- ============================================================
CREATE TABLE devices (
    device_id     UUID PRIMARY KEY,
    user_id       BIGINT REFERENCES users(id),
    fcm_token     TEXT,
    platform      VARCHAR(16) NOT NULL,                 -- 'android' | 'ios'
    app_version   VARCHAR(32),
    os_version    VARCHAR(32),
    push_morning  BOOLEAN     NOT NULL DEFAULT TRUE,
    push_evening  BOOLEAN     NOT NULL DEFAULT TRUE,
    last_seen_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_devices_user ON devices(user_id);
CREATE INDEX idx_devices_fcm  ON devices(fcm_token);

-- ============================================================
-- 7. 디바이스 관심 직군 (device_categories) — M:N
-- ============================================================
CREATE TABLE device_categories (
    device_id     UUID        NOT NULL REFERENCES devices(device_id) ON DELETE CASCADE,
    category_code VARCHAR(64) NOT NULL REFERENCES job_categories(code),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (device_id, category_code)
);

-- ============================================================
-- 8. 관심기업 (user_favorites) — M:N device ↔ company
-- ============================================================
CREATE TABLE user_favorites (
    device_id    UUID   NOT NULL REFERENCES devices(device_id) ON DELETE CASCADE,
    company_id   BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (device_id, company_id)
);
CREATE INDEX idx_user_favorites_company ON user_favorites(company_id);

-- ============================================================
-- 9. 알림 히스토리 (notification_history)
-- ============================================================
CREATE TABLE notification_history (
    id              VARCHAR(64) PRIMARY KEY,           -- ntf-{yyyymmdd}-{deviceid8}-{seq}
    device_id       UUID         NOT NULL REFERENCES devices(device_id) ON DELETE CASCADE,
    sent_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    kind            VARCHAR(32)  NOT NULL,             -- 'morning_digest' | 'evening_digest' | 'deadline' | 'new_at_favorite'
    title           VARCHAR(255) NOT NULL,
    body            TEXT         NOT NULL,
    job_ids         JSONB        NOT NULL,             -- ['job-id-1', 'job-id-2']
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at         TIMESTAMPTZ,
    delivered       BOOLEAN      NOT NULL DEFAULT FALSE,
    fcm_message_id  VARCHAR(255)
);
CREATE INDEX idx_notif_history_device ON notification_history(device_id, sent_at DESC);
CREATE INDEX idx_notif_history_unread ON notification_history(device_id) WHERE is_read = FALSE;

-- ============================================================
-- 10. API 호출 로그 (api_call_log) — 사람인 한도 모니터링
-- ============================================================
CREATE TABLE api_call_log (
    id                   BIGSERIAL PRIMARY KEY,
    source               VARCHAR(32)  NOT NULL,        -- 'saramin' | 'claude' | 'clearbit' | 'fcm'
    endpoint             VARCHAR(255),
    request_params       JSONB,
    status_code          INT,
    response_size_bytes  INT,
    duration_ms          INT,
    error_message        TEXT,
    called_at            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_api_call_log_source_date ON api_call_log(source, called_at DESC);
