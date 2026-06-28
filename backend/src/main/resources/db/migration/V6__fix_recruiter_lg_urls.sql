-- 원문 링크가 개별 공고가 아니라 전체 채용 목록으로 빠지던 버그 보정 (2026-06-28).
-- 기존 적재 행의 original_url을 올바른 상세 페이지 URL로 일괄 교체.
-- (mapper는 이미 수정됨 — 신규 수집은 정상. 이 마이그레이션은 기존 행 보정용.)

-- recruiter: /career/home?positionSn={sn} (홈으로 빠짐) → /career/jobs/{sn} (해당 공고)
UPDATE jobs
SET original_url = REPLACE(original_url, '/career/home?positionSn=', '/career/jobs/')
WHERE source = 'recruiter'
  AND original_url LIKE '%/career/home?positionSn=%';

-- LG: /app/careers/recruit/notice/detail/{id} (홈으로 리다이렉트) → /apply/detail?id={id} (해당 공고)
UPDATE jobs
SET original_url = 'https://careers.lg.com/apply/detail?id=' || split_part(original_url, '/detail/', 2)
WHERE source = 'lg'
  AND original_url LIKE '%/app/careers/recruit/notice/detail/%';
