-- 원문 공고 직링크 일괄 보정 (2026-06-08).
-- 여러 소스가 공고 대신 회사 홍보페이지/검색목록으로 빠지던 문제 수정.
-- greenhouse·ALIO는 기존 행에서 식별자를 복원할 수 있어 재수집 없이 보정한다.

-- 1) Greenhouse: absolute_url(about.daangn.com·coupang.jobs 등 회사사이트로 빠짐)
--    → greenhouse 호스팅 embed 직링크(공고 본문+지원양식 바로 노출).
--    source_external_id = 'greenhouse-{토큰}-{공고id}' 에서 토큰/공고id 복원. (토큰·id에 '-' 없음)
UPDATE jobs
SET original_url = 'https://job-boards.greenhouse.io/embed/job_app?for='
    || split_part(source_external_id, '-', 2)
    || '&token=' || split_part(source_external_id, '-', 3)
WHERE source_external_id LIKE 'greenhouse-%-%';

-- 2) ALIO 공공기관: 데스크톱 recruitview.do 는 폰에서 모바일 리다이렉트로 idx를 잃고 목록으로 빠짐
--    → mobile2021 경로로 교체(같은 idx로 공고 직링크). host 동일, 경로만 치환.
UPDATE jobs
SET original_url = REPLACE(
        original_url,
        'job.alio.go.kr/recruitview.do?idx=',
        'job.alio.go.kr/mobile2021/recruit/recruitView.do?idx='
    )
WHERE original_url LIKE '%job.alio.go.kr/recruitview.do?idx=%';

-- 3) 서울: 기존 행은 구인등록번호(JO_REGIST_NO)를 저장한 적이 없어 여기서 복원 불가.
--    다음 수집부터 고용24 직링크로 채워진다(코드 반영). 기존 행은 재수집 시 갱신.
