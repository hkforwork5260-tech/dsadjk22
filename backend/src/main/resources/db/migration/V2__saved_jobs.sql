-- 저장한 공고(북마크). 기기(device) ↔ 공고(job) M:N. 관심기업(user_favorites, 회사용)과 대칭 구조.
-- 로그인 없이 익명 기기ID(X-Device-Id) 기준으로 저장한다.
CREATE TABLE saved_jobs (
    device_id   UUID         NOT NULL REFERENCES devices(device_id) ON DELETE CASCADE,
    job_id      VARCHAR(64)  NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    PRIMARY KEY (device_id, job_id)
);

-- 기기별 저장 목록을 최신순으로 빠르게 조회.
CREATE INDEX idx_saved_jobs_device ON saved_jobs(device_id, created_at DESC);
