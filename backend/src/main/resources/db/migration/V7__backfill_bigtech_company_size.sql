-- 대기업 ATS 어댑터(삼성·LG·CJ·KT·현대계열·토스·네이버·무신사·컬리 등)로 들어온 회사들이
-- size=null이라 "대기업" 규모 필터에 안 잡히던 문제 보정 (2026-06-28).
-- inferSize 코드는 이미 수정됨 — 이 마이그레이션은 기존 적재 회사 백필.
-- (재수집 시에도 self-heal되지만 즉시 반영 위해 일괄 업데이트.)

UPDATE companies c
SET size = 'large_corp'
WHERE (c.size IS NULL OR c.size = '')
  AND EXISTS (
    SELECT 1 FROM jobs j
    WHERE j.company_id = c.id
      AND j.source IN ('recruiter', 'greeting', 'samsung', 'toss', 'naver', 'lg', 'cj', 'workday', 'lx')
  );
